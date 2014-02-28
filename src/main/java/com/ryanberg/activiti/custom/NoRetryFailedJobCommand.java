package com.ryanberg.activiti.custom;

import org.activiti.engine.impl.cfg.TransactionContext;
import org.activiti.engine.impl.cfg.TransactionState;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.jobexecutor.MessageAddedNotification;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;


public class NoRetryFailedJobCommand implements Command<Object>
{
    private static final Logger logger = LoggerFactory.getLogger(NoRetryFailedJobCommand.class);

    private String jobId;
    private Throwable exception;

    public NoRetryFailedJobCommand(String jobId, Throwable exception)
    {
        this.jobId = jobId;
        this.exception = exception;
    }

    @Override
    public Object execute(CommandContext commandContext)
    {
        logger.info("Handling Job Exception -- Setting retries to 0");

        JobEntity job = commandContext.getJobEntityManager().findJobById(this.jobId);
        job.setRetries(0); // set retries to 0 so the job executor does not retry this job
        job.setLockOwner(null);
        job.setLockExpirationTime(null);

        if(exception != null) {
            job.setExceptionMessage(exception.getMessage());
            job.setExceptionStacktrace(getExceptionStacktrace());
        }

        JobExecutor jobExecutor = Context.getProcessEngineConfiguration().getJobExecutor();
        MessageAddedNotification messageAddedNotification = new MessageAddedNotification(jobExecutor);
        TransactionContext transactionContext = commandContext.getTransactionContext();
        transactionContext.addTransactionListener(TransactionState.COMMITTED, messageAddedNotification);

        return null;
    }

    private String getExceptionStacktrace() {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
