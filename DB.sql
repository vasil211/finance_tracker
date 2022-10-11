drop database if exists finance_tracker;
create database finance_tracker;
use finance_tracker;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(25) UNIQUE NOT NULL,
    password VARCHAR(40) NOT NULL,
    email VARCHAR(45) UNIQUE NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    last_login DATETIME
);
CREATE TABLE budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE not null,
    from_date DATETIME not null,
    to_date DATETIME not null
);

CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    user_id INT NOT NULL,
    budget_id INT,
    balance DOUBLE,
    FOREIGN KEY (user_id)
        REFERENCES users (id),
    FOREIGN KEY (budget_id)
        REFERENCES budgets (id)
);

CREATE TABLE icons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25),
    icon_id INT NOT NULL,
    FOREIGN KEY (icon_id)
        REFERENCES icons (id)
);

CREATE TABLE transfers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    currency VARCHAR(10),
    from_user_account_id INT NOT NULL,
    to_user_account_id INT NOT NULL,
    date_of_transfer DATETIME NOT NULL,
    description VARCHAR(255),
    FOREIGN KEY (from_user_account_id)
        REFERENCES accounts (id),
    FOREIGN KEY (to_user_account_id)
        REFERENCES accounts (id)
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    category_id INT NOT NULL,
    date_of_transactions DATETIME,
    description VARCHAR(255),
    FOREIGN KEY (category_id)
        REFERENCES categories (id),
    FOREIGN KEY (account_id)
        REFERENCES accounts (id)
);

CREATE TABLE scheduled_payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    status BOOLEAN DEFAULT FALSE,
    date_of_transactions DATETIME NOT NULL,
    title VARCHAR(25) NOT NULL,
    category_id INT NOT NULL,
    account_id INT NOT NULL,
    FOREIGN KEY (category_id)
        REFERENCES categories (id),
    FOREIGN KEY (account_id)
        REFERENCES accounts (id)
);


