-- This file is used to initialize the database with some data.

-- TRUNCATE TABLE transactions RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE connection_user RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE "users" RESTART IDENTITY CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id SERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS connection_users (
    user_id_1 INTEGER NOT NULL,
    user_id_2 INTEGER NOT NULL,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (user_id_1, user_id_2)
);


CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL NOT NULL PRIMARY KEY,
    sender_user_id INTEGER NOT NULL,
    receiver_user_id INTEGER NOT NULL,
    description TEXT null,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL
);

ALTER TABLE IF EXISTS transactions
    DROP CONSTRAINT IF EXISTS chk_transaction_user_id_different;

ALTER TABLE IF EXISTS transactions
    ADD FOREIGN KEY (sender_user_id) REFERENCES "users"(id),
    ADD FOREIGN KEY (receiver_user_id) REFERENCES "users"(id),
    ADD CONSTRAINT chk_transaction_user_id_different
        CHECK (sender_user_id <> receiver_user_id);

ALTER TABLE IF EXISTS connection_users
    DROP CONSTRAINT IF EXISTS chk_connection_user_id_different;

ALTER TABLE IF EXISTS connection_users
    ADD FOREIGN KEY (user_id_1) REFERENCES "users"(id),
    ADD FOREIGN KEY (user_id_2) REFERENCES "users"(id),
    ADD CONSTRAINT chk_connection_user_id_different
        CHECK (user_id_1 <> user_id_2);

INSERT INTO "users" (username, email, password)
    VALUES
        ('jdoe', 'jdoe@email.com', '$2a$12$Y2j1Yoj8x3Do6.JPXJKd3ucj2Iy4nZzptytPUJEsZSNr6MOGS8AhK'),
        ('janedoe', 'janedoe@email.com', '$2a$12$yvriMy7O4AvwNuMCTUYJ..aNFo8yWSQqnWeKngjlIuTpbP7CKaLky'),
        ('luc', 'luc@email.com', '$2a$12$kn/jQe4gOLpH1dnZYVgI3.MCS8Oj1LXnbh82jZhO02TE8oqpLZsgu'),
        ('clara', 'clara@email.com', '$2a$12$jr6VdWeOHCmsv9G4Rt/6O.WpU7.eaDPtQ8eSYVemLwYDjuM6Oaclm'),
        ('anne', 'anne@email.com', '$2a$12$BDsRp6pzxueCV58eBAQoHOzfNa.O2VV4xOPqpiq56oF8g6hX2AGeq')
ON CONFLICT DO NOTHING;

INSERT INTO connection_users (user_id_1, user_id_2, date)
VALUES (1, 2, CURRENT_DATE)
ON CONFLICT DO NOTHING;

-- INSERT INTO transaction (sender_user_id, receiver_user_id, description, amount, date)
-- VALUES (1, 2, 'Ma description', '2400.69', NOW()) ON CONFLICT DO NOTHING;
