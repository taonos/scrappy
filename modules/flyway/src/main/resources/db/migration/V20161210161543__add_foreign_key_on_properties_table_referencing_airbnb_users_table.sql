ALTER TABLE properties
    ADD COLUMN airbnb_user_id bigint NOT NULL;
ALTER TABLE properties
    ADD CONSTRAINT airbnb_user_id_fk FOREIGN KEY (airbnb_user_id)
    REFERENCES airbnb_users (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE RESTRICT;