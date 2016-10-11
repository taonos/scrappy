CREATE TABLE airbnb_users (
  id bigint PRIMARY KEY NOT NULL,
  first_name character varying(50) NOT NULL,
  about text NOT NULL,
  document jsonb NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NULL
)