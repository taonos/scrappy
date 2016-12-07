CREATE OR REPLACE FUNCTION update_updatedAt_column() RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
BEGIN
  IF row(NEW.*) IS DISTINCT FROM row(OLD.*) THEN
    NEW.updated_at = NOW();
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;
END;
$$;