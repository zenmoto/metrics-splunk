package io.github.zenmoto.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.splunk.Args;
import com.splunk.Receiver;
import com.splunk.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by poncelod on 3/10/14.
 */
public class SplunkReporter extends ScheduledReporter {

    private final Receiver receiver;
    private final ObjectMapper mapper;
    private final boolean showSamples;
    private final String index;
    private final Args splunkArgs = new Args();
    private final Map<String, Object> extraAttributes;

    public SplunkReporter(Builder builder, Service splunk) {
        super(builder.registry, builder.prefix, builder.filter, builder.rateUnit, builder.durationUnit);
        receiver = splunk.getReceiver();
        this.showSamples = builder.showSamples;
        splunkArgs.put("source", builder.source);
        splunkArgs.put("sourcetype", builder.sourcetype);
        this.index = builder.index;
        this.extraAttributes = builder.extraAttributes;
        mapper = new ObjectMapper().registerModule(new MetricsModule(builder.rateUnit, builder.durationUnit, this.showSamples));
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        if (gauges.size() >0 || counters.size() > 0 || histograms.size() > 0 || meters.size() > 0 || timers.size() > 0) {
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            result.addAll(rearrangeMetrics(gauges, "g"));
            result.addAll(rearrangeMetrics(counters, "c"));
            result.addAll(rearrangeMetrics(histograms, "h"));
            result.addAll(rearrangeMetrics(meters, "m"));
            result.addAll(rearrangeMetrics(timers, "t"));
            try {
                for (Map<String, Object> e : result) {
                    receiver.submit(index, splunkArgs, mapper.writeValueAsString(e));
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error in serializing metrics to JSON", e);
                // TODO: figure out what to do here.
            }
        }
    }

    private List<Map<String, Object>> rearrangeMetrics(Map<String, ? extends Object> metrics, String label) {
        List<Map<String, Object>> retval = new ArrayList<Map<String, Object>>(metrics.size());
        for (Map.Entry<String, ? extends Object> s : metrics.entrySet()) {
            Map<String, Object> metric = new HashMap<String, Object>();
            metric.put("name", s.getKey());
            metric.put("val", s.getValue());
            metric.put("type", label);
            retval.add(metric);
        }
        return retval;
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
        private String prefix = null;
        private TimeUnit rateUnit = TimeUnit.SECONDS;
        private TimeUnit durationUnit = TimeUnit.MILLISECONDS;
        private MetricFilter filter = MetricFilter.ALL;
        private String index = null;
        public boolean showSamples = false;
        public String source = "metrics";
        private String sourcetype = "metrics";
        private Map<String, Object> extraAttributes = new HashMap<String, Object>();

        private Builder(MetricRegistry registry) {
            this.registry = registry;
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
         * The source to be specified on events published to Splunk
         * @param source
         * @return {@code this}
         */
        public Builder withSource(String source) {
            this.source = source;
            return this;
        }

        /**
         * The sourcetype to be specified on events published to Splunk
         * @param sourcetype
         * @return {@code this}
         */
        public Builder withSourcetype(String sourcetype) {
            this.sourcetype = sourcetype;
            return this;
        }

        /**
         * The Splunk index to log to
         *
         * @param index
         *          the prefix for all metric names
         * @return {@code this}
         */
        public Builder withIndex(String index) {
            this.index = index;
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
         * Whether or not to show samples from
         *
         * @param showSamples
         * @return
         */
        public Builder showSamples(boolean showSamples) {
            this.showSamples = showSamples;
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
         * Add an extra attribute to be supplied in the reporting object
         * @param name Key to add in the resultant JSON
         * @param value Value to be serialized out in resultant JSON
         * @return {@code this}
         */
        public Builder addAttribute(String name, Object value) {
            extraAttributes.put(name, value);
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
            return new SplunkReporter(this, splunk);
        }

    }
}
