ALTER TABLE airbnb_user_properties
    ALTER COLUMN created_at TYPE timestamp with time zone ;
ALTER TABLE airbnb_user_properties
    ALTER COLUMN created_at DROP NOT NULL;

ALTER TABLE airbnb_users
    ALTER COLUMN created_at TYPE timestamp with time zone ;
ALTER TABLE airbnb_users
    ALTER COLUMN created_at DROP NOT NULL;

ALTER TABLE properties
    ALTER COLUMN created_at TYPE timestamp with time zone ;
ALTER TABLE properties
    ALTER COLUMN created_at DROP NOT NULL;