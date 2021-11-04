package com.iteaj.framework.spring.boot;

import org.springframework.boot.SpringBootExceptionReporter;

public class FrameworkExceptionReporter implements SpringBootExceptionReporter {

    @Override
    public boolean reportException(Throwable failure) {
        return false;
    }
}
