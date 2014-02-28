package com.ryanberg.activiti.custom;

import org.activiti.engine.impl.cmd.ExecuteJobsCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.jobexecutor.JobExecutorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class CustomExecuteJobsRunnable implements Runnable
{
    private final Logger logger = LoggerFactory.getLogger(CustomExecuteJobsRunnable.class);

    private final List<String> jobIds;
    private final JobExecutor jobExecutor;

    public CustomExecuteJobsRunnable(JobExecutor jobExecutor, List<String> jobIds) {
        this.jobExecutor = jobExecutor;
        this.jobIds = jobIds;
    }

    public void run() {
        final JobExecutorContext jobExecutorContext = new JobExecutorContext();
        final List<String> currentProcessorJobQueue = jobExecutorContext.getCurrentProcessorJobQueue();
        final CommandExecutor commandExecutor = jobExecutor.getCommandExecutor();

        currentProcessorJobQueue.addAll(jobIds);

        Context.setJobExecutorContext(jobExecutorContext);
        try {
            while (!currentProcessorJobQueue.isEmpty()) {

                try
                {
                    String jobId = currentProcessorJobQueue.remove(0);
                    logger.info("Executing Job Id: {}", jobId);
                    commandExecutor.execute(new ExecuteJobsCmd(jobId));
                }
                catch(Exception e)
                {
                    logger.error("Exception occurred running job!", e);
                }
            }
        }finally {
            Context.removeJobExecutorContext();
        }
    }
}
