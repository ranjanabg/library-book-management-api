DROP DATABASE IF EXISTS `library_management`;
CREATE DATABASE IF NOT EXISTS `library_management`;
USE `library_management`;

CREATE TABLE BOOKS (
    id INT NOT NULL AUTO_INCREMENT,
    isbn VARCHAR (256) NOT NULL,
    rfid VARCHAR (256) NOT NULL,
    title VARCHAR (1000) NOT NULL,
    description BLOB NOT NULL,
    authorName VARCHAR (1000) NOT NULL,
    publisherName VARCHAR (1000) NOT NULL,
    owner VARCHAR (256) NOT NULL,
    price DECIMAL (5, 2) NOT NULL,
    pages INT NOT NULL,
    readCount INT NOT NULL,
    readersRating DECIMAL (5, 2) NOT NULL,
    criticsRating DECIMAL (5, 2) NOT NULL,
    rackId VARCHAR (256) NOT NULL,
    maxIssueDays INT DEFAULT 30,
    PRIMARY KEY (id),
    UNIQUE (isbn),
    UNIQUE (rfid)
);

CREATE TABLE MEMBERS (
    id INT NOT NULL AUTO_INCREMENT,
    rfid VARCHAR (256) NOT NULL,
    firstName VARCHAR(256) NOT NULL,
    lastName VARCHAR(256) NOT NULL,
    emailId VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    mobileNo VARCHAR(11) NOT NULL,
    dateOfBirth DATE NOT NULL,
    type ENUM('READER', 'ADMIN', 'LIBRARIAN') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL,
    createdOn DATE NOT NULL,
    uniquePin INT NOT NULL,
    booksIssued INT DEFAULT 0,
    booksLimit INT DEFAULT 7,
    PRIMARY KEY (id),
    UNIQUE (rfid),
    UNIQUE KEY `name` (firstName, lastName, dateOfBirth)
);

CREATE TABLE BOOK_TRANSACTIONS (
    id INT NOT NULL AUTO_INCREMENT,
    bookId INT NOT NULL,
    memberId INT NOT NULL,
    type ENUM('CHECKOUT', 'RETURN') NOT NULL,
    issuedDate DATE,
    dueDate DATE,
    returnDate DATE,
    noOfTimesRenewed INT,
    PRIMARY KEY (id),
    FOREIGN KEY (bookId)
        REFERENCES BOOKS (id),
    FOREIGN KEY (memberId)
        REFERENCES MEMBERS (id)
);

CREATE TABLE OVERDUE_FEES(
    memberId INT NOT NULL,
    bookId INT NOT NULL,
    amountDue  DECIMAL (5, 2) NOT NULL,
    amountPaid BOOLEAN,
    datePaid DATE,
    FOREIGN KEY (memberId)
        REFERENCES MEMBERS (id),
    FOREIGN KEY (bookId)
        REFERENCES BOOKS (id)
);

CREATE TABLE BOOK_CURRENT_LOCATION(
    `book_id` int,
    `rack_id` varchar(20),
    PRIMARY KEY (`book_id`),
    FOREIGN KEY (`book_id`)
        REFERENCES BOOKS (id)
);

SHOW TABLES;


INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book1ISBN', 'book1RFID', 'To Kill a Mockingbird', 'Story on mocking bird', 'Harper Lee', 'book1Publisher', 'book1Owner', 100.00, 220, 10, 4.5, 3.5, 'B22R61');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book2ISBN', 'book2RFID', '1984, by George Orwell', 'Delve into the life of Winston Smith', 'book2Author', 'book2Publisher', 'book2Owner', 180.00, 250, 25, 1.6, 1.5, 'B22R63');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book3ISBN', 'book3RFID', 'Harry Potter and the Philosopher’s Stone', 'Harry Potter Series', 'J.K. Rowling', 'book3Publisher', 'book3Owner', 214.99, 300, 17, 3.9, 2.6, 'B22R52');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book4ISBN', 'book4RFID', 'The Lord of the Rings', 'Story of Lord of the Rings', 'J.R.R. Tolkien', 'book4Publisher', 'book4Owner', 349.99, 196, 38, 4.2, 4.5, 'B22R07');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book5ISBN', 'book5RFID', 'The Great Gatsby', 'The Great Gatsby explores the decadence of the Jazz Age', 'F. Scott Fitzgerald', 'book5Publisher', 'book5Owner', 263.99, 270, 20, 3.3, 1.6, 'B22R29');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book6ISBN', 'book6RFID', 'Pride and Prejudice', 'Pride And Prejudice details the courtship of two opposed characters', 'Jane Austen', 'book1Publisher', 'book1Owner', 100.00, 220, 10, 4.5, 3.5, 'B22R61');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book7ISBN', 'book7RFID', 'The Diary Of A Young Girl', 'Unforgettable and deeply influential, Anne Frank’s diary', 'Anne Frank', 'book2Publisher', 'book2Owner', 180.00, 250, 25, 1.6, 1.5, 'B22R63');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book8ISBN', 'book8RFID', 'The Book Thief', 'The Book Thief follows Liesel as she rescues books from the tyranny of Nazi rule', 'Markus Zusak', 'book3Publisher', 'book3Owner', 214.99, 300, 17, 3.9, 2.6, 'B22R52');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book9ISBN', 'book9RFID', 'The Hobbit', 'The Hobbit was originally written as a short children’s book', 'J.R.R. Tolkien', 'book4Publisher', 'book4Owner', 349.99, 196, 38, 4.2, 4.5, 'B22R07');
INSERT INTO BOOKS (isbn, rfid, title, description, authorName, publisherName, owner, price, pages, readCount, readersRating, criticsRating, rackId) VALUES ('book10ISBN', 'book10RFID', 'Little Women', 'Join four sisters, each with their own prominent personality', 'Louisa May Alcott', 'book5Publisher', 'book5Owner', 263.99, 270, 20, 3.3, 1.6, 'B22R29');

SELECT * FROM BOOKS;


INSERT INTO MEMBERS (rfid, firstName, lastName, emailId, password, mobileNo, dateOfBirth, type, status, createdOn, uniquePin, booksIssued, booksLimit) VALUES ('member1RFID', 'Shraddha', 'Sukhija', 'shraddha.sukhija@gmail.com', 'adam@123', '4234109932', '1989-09-08', 'READER', 'ACTIVE','2000-05-01','12345','0','5');
INSERT INTO MEMBERS (rfid, firstName, lastName, emailId, password, mobileNo, dateOfBirth, type, status, createdOn, uniquePin, booksIssued, booksLimit) VALUES ('member2RFID', 'Ranjana', 'BG', 'ranjanabg.ranj@gmail.com', 'eve@321', '4234109931', '1989-10-01', 'READER', 'ACTIVE','2020-07-01','23456','0','5');
INSERT INTO MEMBERS (rfid, firstName, lastName, emailId, password, mobileNo, dateOfBirth, type, status, createdOn, uniquePin, booksIssued, booksLimit) VALUES ('member3RFID', 'Chris', 'Jack', 'ranjanabg.ranj@gmail.com', 'nyugen9881', '4238764932', '1991-11-15', 'READER', 'ACTIVE','2016-08-01','34567','3','5');
INSERT INTO MEMBERS (rfid, firstName, lastName, emailId, password, mobileNo, dateOfBirth, type, status, createdOn, uniquePin, booksIssued, booksLimit) VALUES ('member4RFID', 'Henrey', 'Stuart', 'ranjanabg.ranj@gmail.com', 'vanessa1', '4267542765', '1970-12-08', 'LIBRARIAN', 'ACTIVE','2018-05-01','45678','0','5');
INSERT INTO MEMBERS (rfid, firstName, lastName, emailId, password, mobileNo, dateOfBirth, type, status, createdOn, uniquePin, booksIssued, booksLimit) VALUES ('member5RFID', 'Meghan', 'Roy', 'ranjana@uw.edu', 'ranjana', '4254108532', '1990-12-08', 'ADMIN', 'ACTIVE','2021-12-08','45678','0','100');

SELECT * FROM MEMBERS;


insert into book_current_location (book_id, rack_id) values (1, 'B22R62');
insert into book_current_location (book_id, rack_id) values (2, 'B22R64');

SELECT * FROM book_current_location;

-- NOT REQUIRED TO INSERT ENTRIES IN ANY OTHER TABLE AS CHECKOUT, RETURN, RENEW OPERATIONS WOULD TAKE CARE OF ALL OTHER TABLES
INSERT INTO BOOK_TRANSACTIONS (bookId, memberId, type, issuedDate, dueDate, noOfTimesRenewed) VALUES ('2', '3', 'CHECKOUT', '2021-11-15', '2021-12-15', '0');
INSERT INTO BOOK_TRANSACTIONS (bookId, memberId, type, issuedDate, dueDate, noOfTimesRenewed) VALUES ('3', '3', 'CHECKOUT', '2021-11-15', '2021-12-15', '0');
INSERT INTO BOOK_TRANSACTIONS (bookId, memberId, type, issuedDate, dueDate, noOfTimesRenewed) VALUES ('4', '3', 'CHECKOUT', '2021-11-15', '2021-12-15', '0');

SELECT * FROM BOOK_TRANSACTIONS;

INSERT INTO OVERDUE_FEES (memberId, bookId, amountDue, amountPaid) VALUES ('3', '5', '2.99', false);

SELECT * FROM OVERDUE_FEES;
