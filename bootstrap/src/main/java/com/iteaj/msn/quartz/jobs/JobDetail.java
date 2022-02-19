package com.iteaj.msn.quartz.jobs;

import org.quartz.impl.JobDetailImpl;

public class JobDetail extends JobDetailImpl {

    @Override
    public boolean isPersistJobDataAfterExecution() {
        return super.isPersistJobDataAfterExecution();
    }

    @Override
    public boolean isConcurrentExectionDisallowed() {
        return super.isConcurrentExectionDisallowed();
    }
}
