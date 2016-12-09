CREATE TRIGGER modify_updatedAt_on_update
BEFORE UPDATE
  ON properties
FOR EACH ROW
EXECUTE PROCEDURE update_updatedAt_column();