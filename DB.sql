drop database if exists finance_tracker;
create database finance_tracker;
use finance_tracker;


CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    last_login DATETIME
);

CREATE TABLE currencies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    abriviation VARCHAR(5) NOT NULL,
    full_name VARCHAR(25) NOT NULL
);
CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    currency_id INT NOT NULL,
    user_id INT NOT NULL,
    balance DOUBLE NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id)
        REFERENCES users (id),
    FOREIGN KEY (currency_id)
        REFERENCES currencies (id)
);

CREATE TABLE icons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    icon_id INT NOT NULL,
    FOREIGN KEY (icon_id)
        REFERENCES icons (id)
);
CREATE TABLE budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    from_date DATETIME NOT NULL,
    to_date DATETIME NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES users (id),
    FOREIGN KEY (category_id)
        REFERENCES categories (id)
);
CREATE TABLE transfers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    currency_id INT NOT NULL,
    from_user_account_id INT NOT NULL,
    to_user_account_id INT NOT NULL,
    date_of_transfer DATETIME NOT NULL,
    description VARCHAR(255),
    FOREIGN KEY (from_user_account_id)
        REFERENCES accounts (id),
    FOREIGN KEY (to_user_account_id)
        REFERENCES accounts (id),
    FOREIGN KEY (currency_id)
        REFERENCES currencies (id)
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    category_id INT NOT NULL,
    created_at DATETIME NOT NULL,
    description VARCHAR(255),
    FOREIGN KEY (category_id)
        REFERENCES categories (id),
    FOREIGN KEY (account_id)
        REFERENCES accounts (id)
);

CREATE TABLE scheduled_payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    due_date DATETIME NOT NULL,
    title VARCHAR(25) NOT NULL,
    category_id INT NOT NULL,
    account_id INT NOT NULL,
    FOREIGN KEY (category_id)
        REFERENCES categories (id),
    FOREIGN KEY (account_id)
        REFERENCES accounts (id)
);


