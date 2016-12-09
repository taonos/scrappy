CREATE OR REPLACE FUNCTION prevent_updating_created_at_column() RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
BEGIN
  NEW.created_at = OLD.created_at;
  RETURN NEW;
END;
$$;