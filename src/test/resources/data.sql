TRUNCATE TABLE transaction RESTART IDENTITY CASCADE;
TRUNCATE TABLE connection_user RESTART IDENTITY CASCADE;
TRUNCATE TABLE "user" RESTART IDENTITY CASCADE;

CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS connection_user (
    user_id INTEGER NOT NULL,
    associated_user_id SERIAL NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (user_id, associated_user_id)
);

CREATE TABLE IF NOT EXISTS transaction (
    id SERIAL NOT NULL PRIMARY KEY,
    sender_user_id SERIAL NOT NULL,
    receiver_user_id SERIAL NOT NULL,
    description TEXT null,
    amount DECIMAL NOT NULL,
    date DATE NOT NULL
);

ALTER TABLE IF EXISTS transaction
    ADD FOREIGN KEY (sender_user_id) REFERENCES "user"(id);

ALTER TABLE IF EXISTS transaction
    ADD FOREIGN KEY (receiver_user_id) REFERENCES "user"(id);

ALTER TABLE IF EXISTS connection_user
    ADD FOREIGN KEY (user_id) REFERENCES "user"(id);

ALTER TABLE IF EXISTS connection_user
    ADD FOREIGN KEY (associated_user_id) REFERENCES "user"(id);

INSERT INTO "user"(username, email, password)
VALUES
    ('jtest', 'jtest@email.com', 'xxx'),
    ('janetest', 'janetest@email.com', 'xxx');

INSERT INTO connection_user (user_id, associated_user_id, date)
VALUES (1, 2, CURRENT_DATE);

INSERT INTO transaction (sender_user_id, receiver_user_id, description, amount, date)
VALUES (1, 2, 'Ma description', '2400.69', NOW());
