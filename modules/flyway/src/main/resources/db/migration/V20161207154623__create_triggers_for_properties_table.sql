CREATE TRIGGER modify_updatedAt_on_update
BEFORE UPDATE
  ON properties
FOR EACH ROW
EXECUTE PROCEDURE update_updatedAt_column();

CREATE TRIGGER make_createdAt_on_insert
BEFORE INSERT
  ON properties
FOR EACH ROW
EXECUTE PROCEDURE make_createdAt_column();