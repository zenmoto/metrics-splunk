package io.github.zenmoto.metrics.example;

import com.codahale.metrics.Counter;

public class CounterExample extends AbstractExample {
    private Counter c;

    public CounterExample(String name) {
        c = getMetricRegistry().counter(name);
    }

    @Override
    protected void onTick() {
        c.inc();
    }

    public static void main(String[] args) throws Exception {
        CounterExample e = new CounterExample("heythere");
        e.start();
        e.join();
    }
}
