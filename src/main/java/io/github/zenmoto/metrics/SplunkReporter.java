package io.github.zenmoto.metrics;

import com.codahale.metrics.*;
import com.google.gson.JsonObject;
import com.splunk.Receiver;
import com.splunk.Service;
import io.github.zenmoto.metrics.formatters.*;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by poncelod on 3/10/14.
 */
public class SplunkReporter extends ScheduledReporter {

    private final Service splunk;
    private final Receiver receiver;

    public SplunkReporter(MetricRegistry registry, Service splunk, String prefix, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter) {
        super(registry, prefix, filter, rateUnit, durationUnit);
        this.splunk = splunk;
        receiver = splunk.getReceiver();
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        JsonObject o = new JsonObject();
        o.addProperty("type", "metric");
        addMetricToReport(o, "g", new GaugeJsonFormatter(), gauges);
        addMetricToReport(o, "c", new CounterJsonFormatter(), counters);
        addMetricToReport(o, "h", new HistogramJsonFormatter(), histograms);
        addMetricToReport(o, "m", new MeterJsonFormatter(), meters);
        addMetricToReport(o, "t", new TimerJsonFormatter(), timers);
        receiver.submit(o.toString());
    }

    private <T> void addMetricToReport(JsonObject o, String label, JsonFormatter<T> f, SortedMap<String, T> values) {
        JsonObject sub = new JsonObject();
        for (Map.Entry<String, T> obj : values.entrySet()) {
            sub.add(obj.getKey(), f.format(obj.getValue()));
        }
        o.add(label, sub);
    }

    /**
     * Returns a new {@link Builder} for {@link SplunkReporter}.
     *
     * @param registry
     *          the registry to report
     * @return a {@link Builder} instance for a {@link SplunkReporter}
     */
    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private String prefix;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.prefix = null;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }

        /**
         * Prefix all metric names with the given string.
         *
         * @param prefix
         *          the prefix for all metric names
         * @return {@code this}
         */
        public Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit
         *          a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit
         *          a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter
         *          a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Builds a {@link SplunkReporter} with the given properties, sending
         * metrics using the given {@link Service} client.
         *
         * @param splunk
         *          a {@link Service} instance
         * @return a {@link SplunkReporter}
         */
        public SplunkReporter build(Service splunk) {
            return new SplunkReporter(registry,
                    splunk,
                    prefix,
                    rateUnit,
                    durationUnit,
                    filter);
        }

    }
}
