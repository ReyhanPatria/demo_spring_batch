package com.example.scheduled_batch;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@EnableBatchProcessing
public class ScheduledImportUserBatchConfiguration {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job scheduledImporUserJob;

    @Scheduled(fixedRate = 60000)
    public void perform() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        LoggerFactory.getLogger(this.getClass()).info("Performing batch job");
    
        JobParameters params = new JobParametersBuilder()
            .addString("jobID", String.valueOf(System.currentTimeMillis()))
            .toJobParameters();

        jobLauncher.run(scheduledImporUserJob, params);
    }
    
    public DataSource getReadDataSource() {
        return DataSourceBuilder.create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .url("jdbc:mysql://localhost/mini_marqet")
            .username("root")
            .password("")
            .build();
    }

    @Bean
    public JdbcCursorItemReader<Person> getUserDatabaseReader() {
        return new JdbcCursorItemReaderBuilder<Person>()
            .name("userReader")
            .dataSource(getReadDataSource())
            .sql("SELECT user_name, user_email, user_gender, user_phone FROM users")
            .rowMapper(new RowMapper<Person>(){

                @Override
                public Person mapRow(ResultSet rs, int rowNumber) throws SQLException {
                    return Person.builder()
                        .name(rs.getString("user_name"))
                        .email(rs.getString("user_email"))
                        .gender(rs.getString("user_gender"))
                        .phone(rs.getString("user_phone"))
                        .build();
                }
                
            })
            .build();
    }

    @Bean
    public PersonItemProcessor getItemProcessor() {
        return new PersonItemProcessor();
    }

    public Resource getWriteResource() {
        return new FileSystemResource("output/data.csv");
    }

    @Bean
    public FlatFileItemWriter<Person> getUserCsvWriter() {
        return new FlatFileItemWriterBuilder<Person>()
            .name("userWriter")
            .resource(getWriteResource())
            .delimited()
            .names("name", "email", "gender", "phone")
            .build();
    }

    @Bean
    public Job scheduledImportUserJob(JobBuilderFactory jobBuilderFactory, Step scheduledImportUserStep) {
        return jobBuilderFactory.get("scheduledImportUserJob")
            .incrementer(new RunIdIncrementer())
            .flow(scheduledImportUserStep)
            .end()
            .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory
            .get("scheduledImportUserStep")
            .<Person, Person> chunk(5)
            .reader(getUserDatabaseReader())
            .processor(getItemProcessor())
            .writer(getUserCsvWriter())
            .build();
    }
}