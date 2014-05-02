package io.github.zenmoto.metrics.formatters;

import com.codahale.metrics.Meter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MeterJsonFormatter implements JsonFormatter<Meter> {

    @Override
    public JsonElement format(Meter value) {
        JsonObject o = new JsonObject();
        o.addProperty("count", value.getCount());
        o.addProperty("rate15m", value.getFifteenMinuteRate());
        o.addProperty("rate5m", value.getFiveMinuteRate());
        o.addProperty("rate1m", value.getOneMinuteRate());
        o.addProperty("meanrate", value.getMeanRate());
        return o;
    }
}
