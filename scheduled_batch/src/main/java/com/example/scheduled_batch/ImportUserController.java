package com.example.scheduled_batch;

import java.io.IOException;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImportUserController {
    @Autowired
    private final ImportUserService importUserService;

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) 
            throws IOException,
                JobRestartException,
                JobParametersInvalidException, 
                JobExecutionAlreadyRunningException, 
                JobInstanceAlreadyCompleteException { 
        
        Resource readResource = new InputStreamResource(file.getInputStream());
        importUserService.executeSingleImportUserJob(readResource);

        return ResponseEntity.ok("File uploaded successfully");
    }
}