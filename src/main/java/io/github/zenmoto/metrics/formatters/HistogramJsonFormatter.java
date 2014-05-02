package io.github.zenmoto.metrics.formatters;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Snapshot;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HistogramJsonFormatter implements JsonFormatter<Histogram> {
    @Override
    public JsonElement format(Histogram value) {
        Snapshot s = value.getSnapshot();
        JsonObject o = new JsonObject();
        o.addProperty("min", s.getMin());
        o.addProperty("max", s.getMax());
        o.addProperty("median", s.getMedian());
        o.addProperty("mean", s.getMean());
        o.addProperty("75th", s.get75thPercentile());
        o.addProperty("95th", s.get95thPercentile());
        o.addProperty("98th", s.get98thPercentile());
        o.addProperty("99th", s.get99thPercentile());
        o.addProperty("999th", s.get999thPercentile());
        return o;
    }
}
