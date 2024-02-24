package com.hayden.tracing_apt;

import com.hayden.tracing.observation_aspects.*;
import com.hayden.tracing.template.TemplatingEngine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedAnnotationTypes("com.hayden.tracing_apt.Cdc")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@Slf4j
public class TracingProcessor extends AbstractProcessor {

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // generate aspectJ aspects
        Path path = Paths.get("observation_aspect_template.txt");
        annotations.stream()
                .flatMap(t -> roundEnv.getElementsAnnotatedWith(Cdc.class).stream())
                .filter(Objects::nonNull)
                .map(this::getPatterns)
                .filter(Objects::nonNull)
                .flatMap(tracingAspect -> {
                    SuppliedAspect suppliedAspect = tracingAspect.get();
                    var toReplace = Map.of(
                            suppliedAspect.aspectClassId(), suppliedAspect.getName().get().getAspectClassName(),
                            suppliedAspect.adviceId(), suppliedAspect.getAdvice().get(),
                            suppliedAspect.aspectFunctionId(), suppliedAspect.getName().get().getAspectFunctionName(),
                            suppliedAspect.afterId(), getPointcut(suppliedAspect, supplied -> supplied instanceof SuppliedAfterPointcut suppliedFound ? suppliedFound.toString() : ""),
                            suppliedAspect.aroundId(), getPointcut(suppliedAspect, supplied -> supplied instanceof SuppliedAroundPointcut suppliedFound ? suppliedFound.toString() : ""),
                            suppliedAspect.beforeId(), getPointcut(suppliedAspect, supplied -> supplied instanceof SuppliedBeforePointcut suppliedFound ? suppliedFound.toString() : "")
                    );
                    writeToFile("tracing/src/main/resources/out1", suppliedAspect.aspectClassId());
                    return Stream.of(Map.entry(
                            suppliedAspect.aspectClassId(),
                            TemplatingEngine.Companion.replace(toReplace, path)
                    ));
                })
                .forEach(adviceItem -> {
                    try(var fos = this.processingEnv.getFiler().createSourceFile("com.hayden.tracing_apt.%s".formatted(adviceItem.getKey())).openOutputStream()) {
                        fos.write(adviceItem.getValue().getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        writeToFile("src/main/resources/out", e.toString());
                    }
                });

        return true;
    }

    private TracingAspectSupplier getPatterns(Element l) {
        var lo = getTy(l) ;
        writeToFile("tracing/src/main/resources/out_file", lo.toString() );
        return lo.stream().findAny().map(this::getValue)
                .orElse(null);

    }

    private TracingAspectSupplier getValue(AnnotationMirror cdcElement) {
        var cdcElementValues = cdcElement.getElementValues();
        List<Before> before = getAnnotationValue(cdcElementValues, "before");
        List<After> after = getAnnotationValue(cdcElementValues, "After");
        List<Around> around = getAnnotationValue(cdcElementValues, "Around");
        List<Pointcut> pointCut = getAnnotationValue(cdcElementValues, "Pointcut");
        List<Aspect> aspect = getAnnotationValue(cdcElementValues, "Aspect");
        return new TracingAspectSupplier(before, after, around, pointCut, aspect);
    }

    private static <T> List<T> getAnnotationValue(Map<? extends ExecutableElement, ? extends AnnotationValue> f, String before) {
        return f.entrySet().stream()
                .filter(s -> s.getKey().getSimpleName().toString().contains(before))
                .findAny().map(Map.Entry::getValue)
                .map(TracingProcessor::<T>getTy)
                .orElse(new ArrayList<>());
    }

    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> List<T> getTy(AnnotationValue value) {
        writeToFile("tracing/src/main/resources/out_file1", value.toString() );
        if (value.getValue() instanceof List list && !list.isEmpty()) {
            try {
                return (List<T>) value.getValue();
            } catch (ClassCastException ignored) {
            }
        }
        return new ArrayList<>();
    }

    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<AnnotationMirror> getTy(Element l) {
        return l.getAnnotationMirrors().stream()
                .flatMap(a -> a.getElementValues().values().stream().map(AnnotationValue::getValue))
                .flatMap(t -> {
                    if (t instanceof List list && !list.isEmpty() && list.getFirst() instanceof AnnotationMirror)
                        return list.stream();
                    else if (t instanceof AnnotationMirror m)
                        return Stream.of(m);
                    return Stream.empty();
                })
                .toList();
    }

    private static void writeToFile(String name, String e) {
        try (var fod = new FileOutputStream(name)) {
            fod.write(e.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {

        }
    }

    private static String getPointcut(SuppliedAspect suppliedAspect, Function<SuppliedPointcut, String> created) {
        return suppliedAspect.getPointcut().get()
                .stream()
                .map(s -> Optional.ofNullable(s)
                        .map(created)
                        .orElse("")
                )
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
