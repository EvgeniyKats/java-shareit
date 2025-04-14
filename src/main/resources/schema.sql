CREATE TABLE IF NOT EXISTS item (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	owner_id bigint NOT NULL,
	name varchar(100) NOT NULL,
	description varchar(200) NOT NULL,
	available boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS user_list (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	email varchar(255) NOT NULL,
	name varchar(512) NOT NULL UNIQUE
);