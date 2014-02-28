package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class TestParallelJobException {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	public void startProcess() throws Exception {
		RepositoryService repositoryService = activitiRule.getRepositoryService();


        repositoryService.createDeployment().addClasspathResource("diagrams/ParallelJobException.bpmn").deploy();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "Activiti");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ParallelJobException", variableMap);
        assertNotNull(processInstance.getId());
        System.out.println("id " + processInstance.getId() + " "
                + processInstance.getProcessDefinitionId());

        ManagementService managementService = activitiRule.getManagementService();

        Thread.sleep(5000); // let activiti execution run for a bit


        // Display results of test

        Job failedJob = managementService.createJobQuery().processInstanceId(processInstance.getId()).withException().singleResult();

        System.out.println("*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*");
        System.out.println("I am the job that failed [id=" + failedJob.getId() + "]");

        List<Job> blockedJobs = managementService.createJobQuery().processInstanceId(processInstance.getProcessInstanceId()).withRetriesLeft().list();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

        for(Job job : blockedJobs)
        {
            JobEntity jobEntity = (JobEntity) job;
            System.out.println("I am a job [id=" + job.getId() + "] that is 'orphaned' and will not run until: " + sdf.format(jobEntity.getLockExpirationTime()) + " because my peer threw an exception!");
        }

        if(blockedJobs.isEmpty())
        {
            System.out.println("Hooray! No 'orphaned' Jobs!");
        }
        System.out.println("*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*");


    }
}