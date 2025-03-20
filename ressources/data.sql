CREATE DATABASE pay_my_buddy;
\c pay_my_buddy

CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS connection_user (
    user_id INTEGER NOT NULL REFERENCES "user",
    associated_user_id INTEGER NOT NULL REFERENCES "user",
    date DATE NOT NULL,
    PRIMARY KEY (user_id, associated_user_id)
);

CREATE TABLE IF NOT EXISTS transaction (
    id SERIAL NOT NULL PRIMARY KEY,
    sender_user_id INTEGER NOT NULL REFERENCES "user",
    receiver_user_id INTEGER NOT NULL REFERENCES "user",
    description TEXT null,
    amount DECIMAL NOT NULL,
    date DATE NOT NULL
);

INSERT INTO "user" (username, email, password)
VALUES
    ('jdoe', 'jdoe@email.com', 'xxx'),
    ('janedoe', 'janedoe@email.com', 'xxx');

INSERT INTO connection_user (user_id, associated_user_id, date)
VALUES (1, 2, CURRENT_DATE);

INSERT INTO transaction (sender_user_id, receiver_user_id, description, amount, date)
VALUES (1, 2, 'Ma description', '2400.69', NOW());