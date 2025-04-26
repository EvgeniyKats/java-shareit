CREATE TABLE IF NOT EXISTS item (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	owner_id bigint NOT NULL,
	request_id bigint,
	name varchar(100) NOT NULL,
	description varchar(300) NOT NULL,
	available boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS user_list (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	email varchar(255) NOT NULL,
	name varchar(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS booking (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	item_id bigint NOT NULL,
    booker_id bigint NOT NULL,
    status integer NOT NULL,
    start_booking_time timestamp NOT NULL,
    end_booking_time timestamp NOT NULL,
    CONSTRAINT start_less_when_end CHECK (start_booking_time < end_booking_time)
);

CREATE TABLE IF NOT EXISTS comment (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id bigint NOT NULL,
    item_id bigint NOT NULL,
    text varchar(500) NOT NULL,
    created_time timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS request (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	description varchar(300) NOT NULL,
    owner_id bigint NOT NULL,
    created_time timestamp NOT NULL
);