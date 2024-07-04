package com.hayden.tracing_apt;

import com.hayden.tracing_apt.observation_aspects.*;
import com.hayden.tracing_apt.template.TemplatingEngine;
import com.hayden.tracing_apt.observation_aspects.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedAnnotationTypes("com.hayden.tracing_apt.Cdc")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@Slf4j
public class TracingProcessor extends AbstractProcessor {

    public static final String ASPECT_TEMPLATE = "com/hayden/tracing_apt/template/observation_aspect_provided_template.txt";


    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // generate aspectJ aspects
        annotations.stream()
                .flatMap(t -> roundEnv.getElementsAnnotatedWith(Cdc.class).stream())
                .filter(Objects::nonNull)
                .map(this::getPatterns)
                .filter(Objects::nonNull)
                .flatMap(TracingProcessor::getToWriteJavaAspectFile)
                .forEach(this::writeAspectFileToFile);

        return true;
    }

    private void writeAspectFileToFile(Map.Entry<String, String> adviceItem) {
        try(var fos = this.processingEnv.getFiler().createSourceFile("com.hayden.tracing_apt.%s".formatted(adviceItem.getKey())).openOutputStream()) {
            fos.write(adviceItem.getValue().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.printf("Failed to write: %s\n", e.getMessage());
        }
    }

    @SneakyThrows
    @NotNull
    private static Stream<Map.Entry<String, String>> getToWriteJavaAspectFile(TracingAspectSupplier tracingAspect) {
        SuppliedAspect suppliedAspect = tracingAspect.get();
        return Optional.of(TemplatingEngine.Companion.replace(createReplacementMap(suppliedAspect), ASPECT_TEMPLATE))
                .stream()
                .map(toWriteAspect -> Map.entry(
                        suppliedAspect.getName().get().getAspectClassName(),
                        toWriteAspect
                ));
    }

    @NotNull
    private static Map<String, String> createReplacementMap(SuppliedAspect suppliedAspect) {
        var toReplace = Map.of(
                suppliedAspect.aspectClassId(), suppliedAspect.getName().get().getAspectClassName(),
                suppliedAspect.adviceId(), suppliedAspect.getAdvice().get(),
                suppliedAspect.aspectFunctionId(), suppliedAspect.getName().get().getAspectFunctionName(),
                suppliedAspect.afterId(), getPointcut(suppliedAspect, supplied -> supplied instanceof SuppliedAfterPointcut suppliedFound ? suppliedFound.getPc() : ""),
                suppliedAspect.aroundId(), getPointcut(suppliedAspect, supplied -> supplied instanceof SuppliedAroundPointcut suppliedFound ? suppliedFound.getPc() : ""),
                suppliedAspect.beforeId(), getPointcut(suppliedAspect, supplied -> supplied instanceof SuppliedBeforePointcut suppliedFound ? suppliedFound.getPc() : ""),
                suppliedAspect.monitoringTypes(), suppliedAspect.getMonitoringTypes().get()
        );
        return toReplace;
    }

    private TracingAspectSupplier getPatterns(Element l) {
        var lo = getTy(l) ;
        return lo.stream().findAny().map(this::getValue)
                .orElse(null);

    }

    private TracingAspectSupplier getValue(AnnotationMirror cdcElement) {
        var cdcElementValues = cdcElement.getElementValues();
        return new TracingAspectSupplier(
                getAnnotationValue(cdcElementValues, "before", "value"),
                getAnnotationValue(cdcElementValues, "after", "value"),
                getAnnotationValue(cdcElementValues, "around", "value"),
                getAnnotationValue(cdcElementValues, "pointcut", "value"),
                getAnnotationValue(cdcElementValues, "aspect", "value"),
                getAnnotationEnumValue(cdcElementValues, "monitoringTypes"),
                getAnnotationStringValueByKey(cdcElementValues, "aspectName"),
                getAnnotationStringValueByKey(cdcElementValues, "aspectFunctionName")
        );
    }


    private static String getAnnotationStringValueByKey(Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues,
                                                        String filterKey) {
        return (String) annotationValues.entrySet().stream()
                .filter(k -> k.getKey().getSimpleName().toString().startsWith(filterKey))
                .findAny()
                .map(Map.Entry::getValue)
                .map(AnnotationValue::getValue)
                .orElse(null);
    }

    private static List<String> getAnnotationEnumValue(Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues,
                                                       String filterKey) {
        return annotationValues.entrySet().stream()
                .filter(s -> s.getKey().getSimpleName().toString().contains(filterKey))
                .findAny().map(Map.Entry::getValue)
                .map(TracingProcessor::getTy)
                .stream()
                .map(Object::toString)
                .toList();
    }

    private static List<String> getAnnotationValue(Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues,
                                                   String filterKey,
                                                   String annotationFieldName) {
        return annotationValues.entrySet().stream()
                .filter(s -> s.getKey().getSimpleName().toString().contains(filterKey))
                .findAny()
                .map(Map.Entry::getValue)
                .map(TracingProcessor::getTy)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(i -> annotationMirrorToString(i, annotationFieldName))
                .toList();
    }

    @NotNull
    private static Stream<String> annotationMirrorToString(Object i, String annotationFieldName) {
        if (i instanceof AnnotationMirror annotationMirror)  {
            return annotationMirror.getElementValues().entrySet().stream()
                    .filter(k -> k.getKey().toString().contains(annotationFieldName))
                    .map(e -> e.getValue().getValue().toString());
        } else if (i instanceof AnnotationValue annotationValue) {
            if (annotationValue.getValue() instanceof String value)
                return Stream.of(value);
            else
                return Stream.of(annotationValue.toString());
        }
        return Stream.empty();
    }

    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<?> getTy(AnnotationValue value) {
        if (value.getValue() instanceof List list && !list.isEmpty()) {
            try {
                return (List) value.getValue();
            } catch (ClassCastException ignored) {
            }
        } else {
            return List.of(value.getValue());
        }
        return new ArrayList();
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
                    else return Stream.of(t.toString());
                })
                .toList();
    }

    private static void writeToFile(String name, String e) {
        System.out.println(name + ": " + e);
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
