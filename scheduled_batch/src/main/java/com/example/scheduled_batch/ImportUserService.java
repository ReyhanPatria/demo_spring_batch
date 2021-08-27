package com.example.scheduled_batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ImportUserService {
    @Autowired
    private JobLauncher jobLauncer;

    @Autowired
    @Qualifier(value = "userCsvReader")
    private FlatFileItemReader<Person> userCsvReader;

    @Autowired
    @Qualifier(value = "singleImportUserJob")
    private Job singleImportUserJob;
    
    public JobExecution executeSingleImportUserJob(Resource readResource) 
            throws JobRestartException, 
                JobParametersInvalidException, 
                JobExecutionAlreadyRunningException, 
                JobInstanceAlreadyCompleteException {
        
        userCsvReader.setResource(readResource);

        JobParameters params = new JobParametersBuilder()
            .addString("jobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();

        return jobLauncer.run(singleImportUserJob, params);
    }
}