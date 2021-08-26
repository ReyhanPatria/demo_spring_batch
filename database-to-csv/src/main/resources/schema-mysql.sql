DROP TABLE IF EXISTS people;

CREATE TABLE people (
    person_id INT AUTO-INCREMENT NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);