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