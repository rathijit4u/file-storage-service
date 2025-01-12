-- Enable the uuid-ossp extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the table in the public schema
CREATE TABLE public.apikey (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    key TEXT,
    expiry DATE,
    active BOOLEAN
);

INSERT INTO public.apikey(
	 name, key, expiry, active)
	VALUES ('testing', 'test123', '2026-01-13', True);