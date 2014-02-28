package com.ryanberg.activiti.custom;

import org.activiti.spring.components.jobexecutor.SpringJobExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;


public class CustomSpringJobExecutor extends SpringJobExecutor
{

    private TaskExecutor taskExecutor;

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    /**
     * Required spring injected {@link TaskExecutor}} implementation that will be used to execute runnable jobs.
     *
     * @param taskExecutor
     */
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void executeJobs(List<String> jobIds) {
        try {
            taskExecutor.execute(new CustomExecuteJobsRunnable(this, jobIds));
        } catch (RejectedExecutionException e) {
            rejectedJobsHandler.jobsRejected(this, jobIds);
        }
    }
}
