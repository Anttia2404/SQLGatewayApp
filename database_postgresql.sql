-- SQL Gateway Application - PostgreSQL Database Setup Script
-- This script creates the murach database and "user" table with sample data

-- Create database (run this as postgres superuser)
-- DROP DATABASE IF EXISTS murach;
-- CREATE DATABASE murach;

-- Connect to murach database before running the rest
-- \c murach

-- Create User table (note: "user" is a reserved word in PostgreSQL, so we use quotes)
DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
    userid SERIAL PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL
);

-- Insert sample data
INSERT INTO "user" (email, firstname, lastname) 
VALUES 
    ('jsmith@gmail.com', 'John', 'Smith'),
    ('andi@murach.com', 'Andrea', 'Steelman'),
    ('joelmurach@yahoo.com', 'Joel', 'Murach'),
    ('mike@murach.com', 'Mike', 'Murach');

-- Verify data
SELECT * FROM "user";
