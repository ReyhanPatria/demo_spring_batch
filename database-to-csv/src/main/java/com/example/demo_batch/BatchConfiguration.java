package com.example.demo_batch;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    public DataSource getReadDataSource() {
        return DataSourceBuilder.create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .url("jdbc:mysql://localhost/mini_marqet")
            .username("root")
            .password("")
            .build();
    }

    public Resource getWriteResource() {
        return new FileSystemResource("output/data.csv");
    }

    @Bean
    public JdbcCursorItemReader<Person> getReader() {
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

    @Bean
    public FlatFileItemWriter<Person> getWriter() {
        return new FlatFileItemWriterBuilder<Person>()
            .name("userWriter")
            .delimited()
            .names("name", "email", "gender", "phone")
            .resource(getWriteResource())
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