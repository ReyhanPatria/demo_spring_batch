package com.example.scheduled_batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class SingleImportUserBatchConfiguration {
    @Bean(value = "userCsvReader")
    public FlatFileItemReader<Person> userCsvReader() {
        BeanWrapperFieldSetMapper<Person> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(Person.class);

        return new FlatFileItemReaderBuilder<Person>()
            .name("userCsvReader")
            .resource(new FileSystemResource(""))
            .delimited()
            .names("name", "email", "gender", "phone")
            .fieldSetMapper(mapper)
            .build();
    }

    @Bean(value = "singleImportUserJob")
    public Job singleImportUserJob(JobBuilderFactory jobBuilderFactory, 
            Step singleImportUserStep) {
        
        return jobBuilderFactory.get("singleImportUserJob")
            .incrementer(new RunIdIncrementer())
            .flow(singleImportUserStep)
            .end()
            .build();
    }

    @Bean
    public Step singleImportUserStep(StepBuilderFactory stepBuilderFactory, 
            FlatFileItemWriter<Person> userCsvWriter, 
            PersonItemProcessor personItemProcessor) {

        return stepBuilderFactory
            .get("singleImportUserStep")
            .<Person, Person> chunk(5)
            .reader(userCsvReader())
            .processor(personItemProcessor)
            .writer(userCsvWriter)
            .build();
    }
}