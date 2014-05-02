package io.github.zenmoto.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.splunk.Receiver;
import com.splunk.Service;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.OngoingStubbing;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplunkReporterTest {
    @Test
    public void testReport() throws Exception {
        Service splunk = mock(Service.class);
        Receiver receiver = mock(Receiver.class);
        when(splunk.getReceiver()).thenReturn(receiver);

        MetricRegistry reg = new MetricRegistry();

        reg.counter("tcounter").inc();
        reg.meter("tmeter").mark();
        reg.histogram("thist").update(10);
        reg.histogram("thist").update(1);
        reg.timer("ttimer").time().stop();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        SplunkReporter reporter = SplunkReporter.forRegistry(reg).build(splunk);
        reporter.report();

        verify(receiver).submit(captor.capture());
        JsonElement parsed = new JsonParser().parse(captor.getValue());
        System.err.println(captor.getValue());
        assertThat(parsed).isNotNull();

    }

}
