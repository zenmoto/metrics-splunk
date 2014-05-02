package io.github.zenmoto.metrics.formatters;

public interface Formatter<T, R> {
    public R format(T value);
}
