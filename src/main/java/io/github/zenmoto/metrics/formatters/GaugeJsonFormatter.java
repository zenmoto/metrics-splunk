package io.github.zenmoto.metrics.formatters;

import com.codahale.metrics.Gauge;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GaugeJsonFormatter implements JsonFormatter<Gauge> {
    @Override
    public JsonElement format(Gauge value) {
        JsonObject obj = new JsonObject();
        return getJsonPrimitiveForValue(value.getValue());
    }

    private static JsonPrimitive getJsonPrimitiveForValue(Object val) {
        if (val instanceof String) {
            return new JsonPrimitive((String) val);
        } else if (val instanceof Boolean) {
            return new JsonPrimitive((Boolean) val);
        } else if (val instanceof Number) {
            return new JsonPrimitive((Number) val);
        } else if (val instanceof Character) {
            return new JsonPrimitive((Character) val);
        } else {
            return new JsonPrimitive(val.toString());
        }
    }
}
