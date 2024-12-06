DROP DATABASE IF EXISTS COURSEMARKET;

CREATE DATABASE COURSEMARKET;

USE COURSEMARKET;

CREATE TABLE users(
	userID int primary key auto_increment,
    username varchar(20) BINARY not null,
    pw varchar(20) BINARY not null,
    email varchar(50) not null
);

CREATE TABLE schedule(
	schedID int primary key auto_increment,
    userID int not null,
    courseCode char(8) NOT NULL,
    professor char(50) NOT NULL,
    stime char(20) NOT NULL,
    contact char(20) NOT NULL,
    FOREIGN KEY fk1(userID) REFERENCES users(userID)
);

CREATE TABLE interested(
	entryID int primary key auto_increment,
    userID int not null,
    schedID int not null,
    FOREIGN KEY fk2(userID) REFERENCES users(userID),
    FOREIGN KEY fk3(schedID) REFERENCES schedule(schedID)
);