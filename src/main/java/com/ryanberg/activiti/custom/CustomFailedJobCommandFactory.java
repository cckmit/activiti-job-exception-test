package com.ryanberg.activiti.custom;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.jobexecutor.FailedJobCommandFactory;


public class CustomFailedJobCommandFactory implements FailedJobCommandFactory
{
    @Override
    public Command<Object> getCommand(String jobId, Throwable exception)
    {
        return new NoRetryFailedJobCommand(jobId, exception);
    }
}
