CREATE TRIGGER prevent_modifying_created_at_on_update
BEFORE UPDATE
  ON properties
FOR EACH ROW
EXECUTE PROCEDURE prevent_updating_created_at_column();

CREATE TRIGGER prevent_modifying_created_at_on_update
BEFORE UPDATE
  ON airbnb_user_properties
FOR EACH ROW
EXECUTE PROCEDURE prevent_updating_created_at_column();

CREATE TRIGGER prevent_modifying_created_at_on_update
BEFORE UPDATE
  ON airbnb_users
FOR EACH ROW
EXECUTE PROCEDURE prevent_updating_created_at_column();