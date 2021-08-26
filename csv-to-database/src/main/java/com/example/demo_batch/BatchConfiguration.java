package com.example.demo_batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    public Resource getReadResource() {
        return new FileSystemResource("input/sample-data.csv");
    }

    public DataSource getWriteDataSource() {
        return DataSourceBuilder.create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .url("jdbc:mysql://localhost/demo_batch")
            .username("root")
            .password("")
            .build();
    }

    @Bean
    public FlatFileItemReader<Person> getReader() {
        BeanWrapperFieldSetMapper<Person> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(Person.class);

        return new FlatFileItemReaderBuilder<Person>()
            .name("userReader")
            .delimited()
            .names("name", "email", "gender", "phone")
            .resource(getReadResource())
            .fieldSetMapper(mapper)
            .build();
    }

    @Bean
    public PersonItemProcessor getItemProcessor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> getWriter() {
        return new JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO people (name, email, gender, phone) VALUES (:name, :email, :gender, :phone)")
            .dataSource(getWriteDataSource())
            .build();
    }

    @Bean
	public Job importUserJob(JobBuilderFactory jobBuilderFactory, Step step1) {
		return jobBuilderFactory.get("importUserJob")
			.incrementer(new RunIdIncrementer())
			.flow(step1)
			.end()
			.build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("step1")
			.<Person, Person> chunk(5)
			.reader(getReader())
			.processor(getItemProcessor())
			.writer(getWriter())
			.build();
	}
}