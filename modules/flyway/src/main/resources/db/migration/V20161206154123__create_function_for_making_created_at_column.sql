CREATE OR REPLACE FUNCTION make_createdAt_column() RETURNS TRIGGER
LANGUAGE plpgsql
AS
$$
BEGIN
  NEW.created_at = NOW();
  RETURN NEW;
END;
$$;