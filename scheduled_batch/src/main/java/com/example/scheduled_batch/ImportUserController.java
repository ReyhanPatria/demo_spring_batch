package com.example.scheduled_batch;

import java.io.IOException;

import org.springframework.batch.core.Job;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImportUserController {
    @Autowired
    private JobLauncher jobLauncer;

    @Autowired
    @Qualifier(value = "singleImportUserJob")
    private Job singleImportUserJob;

    @Autowired
    @Qualifier(value = "userCsvReader")
    private FlatFileItemReader<Person> userCsvReader;

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) 
            throws IOException, JobExecutionAlreadyRunningException, JobRestartException, 
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        
        Resource readResource = new InputStreamResource(file.getInputStream());
        userCsvReader.setResource(readResource);

        JobParameters params = new JobParametersBuilder()
            .addString("jobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();

        jobLauncer.run(singleImportUserJob, params);

        return ResponseEntity.ok("File uploaded successfully");
    }
}