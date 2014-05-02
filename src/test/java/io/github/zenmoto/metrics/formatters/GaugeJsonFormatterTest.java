package io.github.zenmoto.metrics.formatters;

import com.codahale.metrics.Gauge;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GaugeJsonFormatterTest {
    @Test
    public void testFormat() throws Exception {
        Gauge gauge = mock(Gauge.class);
        GaugeJsonFormatter formatter = new GaugeJsonFormatter();

        when(gauge.getValue()).thenReturn("one");
        assertThat(formatter.format(gauge)).isEqualTo(new JsonPrimitive("one"));

        when(gauge.getValue()).thenReturn(1);
        assertThat(formatter.format(gauge)).isEqualTo(new JsonPrimitive(1));
    }
}
