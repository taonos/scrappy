CREATE TRIGGER modify_updatedAt_on_update
BEFORE UPDATE
  ON airbnb_users
FOR EACH ROW
EXECUTE PROCEDURE update_updatedAt_column();

CREATE TRIGGER make_createdAt_on_insert
BEFORE INSERT
  ON airbnb_users
FOR EACH ROW
EXECUTE PROCEDURE make_createdAt_column();