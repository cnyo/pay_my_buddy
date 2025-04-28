-- This file is used to initialize the database with some data.

-- TRUNCATE TABLE transaction RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE connection_user RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE "user" RESTART IDENTITY CASCADE;

-- USE pay_my_buddy;

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

INSERT INTO "user" (username, email, password)
VALUES
    ('jdoe', 'jdoe@email.com', '$2a$12$Y2j1Yoj8x3Do6.JPXJKd3ucj2Iy4nZzptytPUJEsZSNr6MOGS8AhK'),
    ('janedoe', 'janedoe@email.com', '$2a$12$yvriMy7O4AvwNuMCTUYJ..aNFo8yWSQqnWeKngjlIuTpbP7CKaLky'),
    ('luc', 'luc@email.com', '$2a$12$kn/jQe4gOLpH1dnZYVgI3.MCS8Oj1LXnbh82jZhO02TE8oqpLZsgu'),
    ('clara', 'clara@email.com', '$2a$12$jr6VdWeOHCmsv9G4Rt/6O.WpU7.eaDPtQ8eSYVemLwYDjuM6Oaclm'),
    ('anne', 'anne@email.com', '$2a$12$BDsRp6pzxueCV58eBAQoHOzfNa.O2VV4xOPqpiq56oF8g6hX2AGeq')
ON CONFLICT DO NOTHING;

INSERT INTO connection_user (user_id, associated_user_id, date)
VALUES (1, 2, CURRENT_DATE) ON CONFLICT DO NOTHING;

-- INSERT INTO transaction (sender_user_id, receiver_user_id, description, amount, date)
-- VALUES (1, 2, 'Ma description', '2400.69', NOW()) ON CONFLICT DO NOTHING;
