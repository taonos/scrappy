CREATE OR REPLACE FUNCTION prevent_updating_created_at_column() RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
BEGIN
  IF NEW.created_at IS DISTINCT FROM OLD.created_at THEN
    RAISE EXCEPTION 'Cannot update `created_at` column!';
  ELSE
    RETURN NEW;
  END IF;
END;
$$;