package com.iteaj.framework.logger;

public interface LoggerFilter {

    String type();

    String name();

    boolean filter(PushParams params);
}
