package com.iteaj.framework.logger;

public interface LoggerFilter<T> {

    String type();

    String name();

    boolean filter(T logger);
}
