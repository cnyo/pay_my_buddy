-- It contains the SQL commands to create the tables.
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
    date DATE NOT NULL DEFAULT CURRENT_DATE
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
        CHECK (user_id_1 <> user_id_2),
    ADD CONSTRAINT chk_ordered_user_id
        CHECK (user_id_1 < user_id_2);

-- The following function and trigger ensure that the user IDs in the connection_users table are always ordered.
CREATE OR REPLACE FUNCTION enforce_user_id_connection()
    RETURNS TRIGGER AS $_func$
DECLARE
    initial_user_id_1 INTEGER;
BEGIN
    initial_user_id_1 := NEW.user_id_1;
    NEW.user_id_1 := LEAST(NEW.user_id_1, NEW.user_id_2);
    NEW.user_id_2 := GREATEST(initial_user_id_1, NEW.user_id_2);
    RETURN NEW;
END;
$_func$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_user_connection ON connection_users;
CREATE TRIGGER trigger_user_connection BEFORE INSERT OR UPDATE ON connection_users
    FOR EACH ROW
EXECUTE FUNCTION enforce_user_id_connection();

-- The following SQL commands insert sample data into the users and connection_users tables.
INSERT INTO "users" (username, email, password)
    VALUES
        ('jdoe', 'jdoe@email.com', '$2a$12$Y2j1Yoj8x3Do6.JPXJKd3ucj2Iy4nZzptytPUJEsZSNr6MOGS8AhK'),
        ('janedoe', 'janedoe@email.com', '$2a$12$yvriMy7O4AvwNuMCTUYJ..aNFo8yWSQqnWeKngjlIuTpbP7CKaLky'),
        ('luc', 'luc@email.com', '$2a$12$kn/jQe4gOLpH1dnZYVgI3.MCS8Oj1LXnbh82jZhO02TE8oqpLZsgu'),
        ('clara', 'clara@email.com', '$2a$12$jr6VdWeOHCmsv9G4Rt/6O.WpU7.eaDPtQ8eSYVemLwYDjuM6Oaclm'),
        ('anne', 'anne@email.com', '$2a$12$BDsRp6pzxueCV58eBAQoHOzfNa.O2VV4xOPqpiq56oF8g6hX2AGeq')
ON CONFLICT DO NOTHING;

INSERT INTO connection_users (user_id_1, user_id_2)
VALUES (1, 2)
ON CONFLICT DO NOTHING;

INSERT INTO transactions (sender_user_id, receiver_user_id, description, amount)
VALUES (1, 2, 'Ma description', '2400.69') ON CONFLICT DO NOTHING;
