package io.github.zenmoto.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.JsonParser;
import com.splunk.Args;
import com.splunk.Receiver;
import com.splunk.Service;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SplunkReporterTest {
    private Service splunk;
    private Receiver receiver;
    private MetricRegistry reg;
    private JsonParser parser = new JsonParser();

    @Before
    public void setup() throws Exception {
        splunk = mock(Service.class);
        receiver = mock(Receiver.class);
        when(splunk.getReceiver()).thenReturn(receiver);

        reg = new MetricRegistry();

        reg.counter("tcounter").inc();
        reg.meter("tmeter").mark();
        reg.histogram("thist").update(10);
        reg.histogram("thist").update(1);
        reg.timer("ttimer").time().stop();
    }

    @Test
    public void testReport() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        SplunkReporter reporter = SplunkReporter.forRegistry(reg).build(splunk);
        reporter.report();

        verify(receiver, times(4)).submit(anyString(), any(Args.class), captor.capture());
        //JsonArray parsed = parser.parse(captor.getValue()).getAsJsonArray();
        //assertNotNull(parsed);
//        assertTrue(parsed.has("timers"));
//        assertTrue(parsed.has("meters"));
//        assertTrue(parsed.has("histograms"));
//        assertTrue(parsed.has("timers"));
    }

    @Test
    public void testSplunkSettings() throws Exception {
        String index = "testIndex";
        String source = "crazySource";
        String sourcetype = "interestingSourcetype";

        SplunkReporter reporter = SplunkReporter.forRegistry(reg)
                .withIndex(index)
                .withSource(source)
                .withSourcetype(sourcetype)
                .build(splunk);
        ArgumentCaptor<String> indexCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Args> argsCaptor = ArgumentCaptor.forClass(Args.class);

        reporter.report();
        verify(receiver, atLeastOnce()).submit(indexCaptor.capture(), argsCaptor.capture(), anyString());
        assertThat(indexCaptor.getValue(), is(index));
        Args supplied = argsCaptor.getValue();
        assertThat((String)supplied.get("sourcetype"), is(sourcetype));
    }

}
