package com.example.demo_batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor  implements ItemProcessor<Person, Person> {
    private static final Logger logger = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(Person person) throws Exception {
        String name = person.getName().toUpperCase();
        String email = person.getEmail().toLowerCase();
        String phone = person.getPhone();
        String gender = person.getGender().equalsIgnoreCase("male") ? "M" : "F";

        Person transformedPerson = Person.builder()
            .name(name)
            .email(email)
            .gender(gender)
            .phone(phone)
            .build();

        logger.info("Transforming ( {} ) into ( {} )",
            person, 
            transformedPerson
        );

        return transformedPerson;
    }
    
}