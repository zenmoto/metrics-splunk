package io.github.zenmoto.metrics.formatters;

import com.codahale.metrics.Counter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class CounterJsonFormatter implements JsonFormatter<Counter> {
    @Override
    public JsonElement format(Counter value) {
        return new JsonPrimitive(value.getCount());
    }
}
