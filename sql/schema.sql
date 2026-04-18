-- ============================================================
-- Library Management System - Database Schema
-- Database: MariaDB
-- Run this script once to create the database and tables.
-- ============================================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Drop tables if they exist (for clean re-runs during development)
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

-- ------------------------------------------------------------
-- items table
-- Stores both books and DVDs. Columns specific to one type
-- are nullable so a single table can hold both.
-- ------------------------------------------------------------
CREATE TABLE items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    item_type VARCHAR(10) NOT NULL,         -- 'book' or 'dvd'
    author VARCHAR(255),                     -- books only
    isbn VARCHAR(20),                        -- books only
    publication_year INT,                    -- books only
    director VARCHAR(255),                   -- DVDs only
    runtime_minutes INT                      -- DVDs only
);

-- ------------------------------------------------------------
-- users table
-- Stores both members and librarians.
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    user_type VARCHAR(15) NOT NULL,          -- 'member' or 'librarian'
    membership_type VARCHAR(50),             -- members only
    employee_id VARCHAR(50)                  -- librarians only
);

-- ------------------------------------------------------------
-- loans table
-- Links items and members for checkout tracking.
-- ------------------------------------------------------------
CREATE TABLE loans (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    member_id INT NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (member_id) REFERENCES users(user_id)
);

-- ------------------------------------------------------------
-- Sample data (optional - useful for testing and demos)
-- ------------------------------------------------------------
INSERT INTO items (title, available, item_type, author, isbn, publication_year)
VALUES
    ('The Great Gatsby', TRUE, 'book', 'F. Scott Fitzgerald', '9780743273565', 1925),
    ('1984', TRUE, 'book', 'George Orwell', '9780451524935', 1949),
    ('To Kill a Mockingbird', TRUE, 'book', 'Harper Lee', '9780061120084', 1960);

INSERT INTO items (title, available, item_type, director, runtime_minutes)
VALUES
    ('Inception', TRUE, 'dvd', 'Christopher Nolan', 148),
    ('The Matrix', TRUE, 'dvd', 'The Wachowskis', 136);

INSERT INTO users (name, email, user_type, membership_type)
VALUES
    ('Alice Johnson', 'alice@example.com', 'member', 'standard'),
    ('Bob Smith', 'bob@example.com', 'member', 'premium');

INSERT INTO users (name, email, user_type, employee_id)
VALUES
    ('Carol Williams', 'carol@library.com', 'librarian', 'EMP001');
