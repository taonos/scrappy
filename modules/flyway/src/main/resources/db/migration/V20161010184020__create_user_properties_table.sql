CREATE TABLE airbnb_user_properties (
    airbnb_user_id bigint NOT NULL,
    property_id bigint NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NULL,
    CONSTRAINT user_property_pkey PRIMARY KEY (property_id, airbnb_user_id),
    CONSTRAINT "property_id_fk" FOREIGN KEY (property_id)
        REFERENCES properties (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE RESTRICT,
    CONSTRAINT "airbnb_user_id_fk" FOREIGN KEY (airbnb_user_id)
        REFERENCES airbnb_users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE RESTRICT
)