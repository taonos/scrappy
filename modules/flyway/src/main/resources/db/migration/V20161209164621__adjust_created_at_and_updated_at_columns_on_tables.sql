ALTER TABLE properties
    ALTER COLUMN created_at TYPE timestamp with time zone ;
ALTER TABLE properties
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE properties
    ALTER COLUMN updated_at TYPE timestamp with time zone ;
ALTER TABLE properties
    ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE properties
    ALTER COLUMN updated_at SET NOT NULL;



ALTER TABLE airbnb_user_properties
    ALTER COLUMN created_at TYPE timestamp with time zone ;
ALTER TABLE airbnb_user_properties
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE airbnb_user_properties
    ALTER COLUMN updated_at TYPE timestamp with time zone ;
ALTER TABLE airbnb_user_properties
    ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE airbnb_user_properties
    ALTER COLUMN updated_at SET NOT NULL;



ALTER TABLE airbnb_users
    ALTER COLUMN created_at TYPE timestamp with time zone ;
ALTER TABLE airbnb_users
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE airbnb_users
    ALTER COLUMN updated_at TYPE timestamp with time zone ;
ALTER TABLE airbnb_users
    ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE airbnb_users
    ALTER COLUMN updated_at SET NOT NULL;