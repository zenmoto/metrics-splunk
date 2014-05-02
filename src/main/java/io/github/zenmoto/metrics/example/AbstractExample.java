package io.github.zenmoto.metrics.example;

import com.codahale.metrics.MetricRegistry;
import com.splunk.Service;
import com.splunk.ServiceArgs;
import io.github.zenmoto.metrics.SplunkReporter;

public abstract class AbstractExample {
    private Thread t;
    private TickManager tickManager = new TickManager();
    private MetricRegistry metricRegistry;

    protected final void start() {
        if (t == null || !t.isAlive()) {
            t = new Thread(new TickManager());
        }
    }

    protected final void stop() {
        tickManager.stop();
    }

    protected void join() throws InterruptedException {
        t.join();
    }

    protected abstract void onTick();
    protected long nextTick() {
        return 2000L;
    }

    protected final MetricRegistry getMetricRegistry() {
        if (metricRegistry == null) {
            setupRegistry();
        }
        return metricRegistry;
    }

    private synchronized void setupRegistry() {
        if (metricRegistry == null) {
            metricRegistry = new MetricRegistry();
            ServiceArgs splunkArgs = new ServiceArgs();
            splunkArgs.setHost("localhost");
            splunkArgs.setUsername("admin");
            splunkArgs.setPassword("changeme");
            Service splunk = Service.connect(splunkArgs);
            SplunkReporter reporter = SplunkReporter
                    .forRegistry(metricRegistry)
                    .build(splunk);
        }
    }


    private class TickManager implements Runnable {

        private volatile boolean shouldRun;

        @Override
        public void run() {
            shouldRun = true;
            while (shouldRun) {
                onTick();
                try {
                    Thread.sleep(nextTick());
                } catch (InterruptedException e) { }
            }
        }

        public void stop() {
            shouldRun = false;
        }
    }

}
