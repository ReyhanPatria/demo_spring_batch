package com.example.scheduled_batch;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person person) throws Exception {
        String name = person.getName().toUpperCase();
        String email = person.getEmail().toLowerCase();
        String gender = (person.getGender().equalsIgnoreCase("male")) ? "M" : "F";
        String phone = person.getPhone();

        return Person.builder()
            .name(name)
            .email(email)
            .gender(gender)
            .phone(phone)
            .build();
    }
}