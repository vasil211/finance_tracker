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
    code VARCHAR(5) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    symbol VARCHAR(5) NOT NULL,
    name_plural VARCHAR(50) NOT NULL
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
    user_id INT DEFAULT NULL,
    FOREIGN KEY (icon_id)
        REFERENCES icons (id),
    UNIQUE KEY unique_key (name , user_id)
);

CREATE TABLE budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    original_budget DOUBLE NOT NULL,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    currency_id INT NOT NULL,
    from_date DATETIME NOT NULL,
    to_date DATETIME NOT NULL,
    FOREIGN KEY (currency_id)
        REFERENCES currencies (id),
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

CREATE TABLE fotgotten_password_tokens (
    token VARCHAR(100) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    created DATETIME NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES users (id)
);

ALTER TABLE accounts ADD INDEX user_id (user_id);
ALTER TABLE budgets ADD INDEX budgets (user_id, category_id);
ALTER TABLE categories ADD INDEX categories (name, user_id);
ALTER TABLE scheduled_payments ADD INDEX scheduled_payments (account_id);
ALTER TABLE transactions ADD INDEX transactions (account_id);
ALTER TABLE transfers ADD INDEX transfers (to_user_account_id,from_user_account_id);
ALTER TABLE fotgotten_password_tokens ADD INDEX fotgotten_password_tokens (token);

insert into icons(url) values("20254611317500-112.jpg");
insert into categories(name,icon_id) values("Plant",1);
insert into icons(url) values("20254611317200-412.jfif");
insert into categories(name,icon_id) values("Car",2);
insert into icons(url) values("20275177307500-87.jpg");
insert into categories(name,icon_id) values("Pet",3);
insert into icons(url) values("20323496383400-126.jfif");
insert into categories(name,icon_id) values("Food",4);
insert into icons(url) values("20336031736200-821.jpg");
insert into categories(name,icon_id) values("House",1);

INSERT INTO `users` VALUES (1,'martok11','$2a$10$os.ZipCPVC.vDcvaRq89pupmkqMMCTXlhoCUes79/rlfRJG2C.b4S','lukasz6@abv.bg','Martin','Kuyumdjiev','2022-10-23 23:07:21'),
(2,'vasil1122','$2a$10$mIUHELmoEnv9.ZH8Y.IiyOxd3ERsMKKihZYUqUerjEgfqYC6NitpW','vasil.vazonov@abv.bg','Vasko','Vazonov','2022-10-25 14:03:01');
INSERT INTO `accounts` VALUES (1,'personal',1,1,17000),(2,'personal',1,2,22300);

INSERT INTO `transfers` VALUES (1,600,1,2,1,'2022-10-23 22:17:13',NULL),
(2,600,1,2,1,'2022-10-23 22:17:31',NULL),
(3,600,1,2,1,'2022-10-23 22:19:16',NULL),
(4,600,1,2,1,'2022-10-23 22:22:12',NULL),
(7,600,1,2,1,'2022-10-23 22:27:09',NULL),
(8,300,1,1,2,'2022-10-23 22:43:43',NULL);


