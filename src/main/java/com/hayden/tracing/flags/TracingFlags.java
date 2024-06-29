package com.hayden.tracing.flags;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TracingFlags {

    private final ConcurrentHashMap<String, Boolean> enabled = new ConcurrentHashMap<>();

    public boolean isEnabled(String key) {
        return enabled.containsKey(key);
    }

    public void add(String value) {
        enabled.put(value, true);
    }

    public void remove(String value) {
        enabled.remove(value);
    }

}
