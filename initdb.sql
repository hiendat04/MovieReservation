CREATE DATABASE movies;
-- Set encoding and standard conforming strings
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

-- Schema and Type Definitions
CREATE TYPE public.userstatus AS ENUM ('active', 'inactive');
CREATE TYPE public.usertype AS ENUM ('admin', 'user');

-- Cast Definitions
CREATE CAST (public.userstatus AS character varying) WITH INOUT AS IMPLICIT;
CREATE CAST (public.usertype AS character varying) WITH INOUT AS IMPLICIT;
CREATE CAST (character varying AS public.userstatus) WITHOUT FUNCTION AS ASSIGNMENT;
CREATE CAST (character varying AS public.usertype) WITHOUT FUNCTION AS ASSIGNMENT;

-- Table Definitions
CREATE TABLE public.addresses (
                                  id SERIAL PRIMARY KEY,
                                  street VARCHAR(255),
                                  city VARCHAR(255),
                                  state VARCHAR(50),
                                  zip_code VARCHAR(20)
);

CREATE TABLE public.movies (
                               id SERIAL PRIMARY KEY,
                               title VARCHAR(255) NOT NULL,
                               genre VARCHAR(50),
                               release_date DATE
);

CREATE TABLE public.permissions (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(100) NOT NULL
);

CREATE TABLE public.roles (
                              id SERIAL PRIMARY KEY,
                              name VARCHAR(100) NOT NULL
);

CREATE TABLE public.permissions_roles (
                                          permission_id INT REFERENCES public.permissions(id),
                                          role_id INT REFERENCES public.roles(id),
                                          PRIMARY KEY (permission_id, role_id)
);

CREATE TABLE public.schedules (
                                  id SERIAL PRIMARY KEY,
                                  movie_id INT REFERENCES public.movies(id),
                                  show_time TIMESTAMP NOT NULL
);

CREATE TABLE public.screens (
                                id SERIAL PRIMARY KEY,
                                name VARCHAR(50) NOT NULL
);

CREATE TABLE public.seats (
                              id SERIAL PRIMARY KEY,
                              screen_id INT REFERENCES public.screens(id),
                              seat_number VARCHAR(10) NOT NULL
);

CREATE TABLE public.show_times (
                                   id SERIAL PRIMARY KEY,
                                   schedule_id INT REFERENCES public.schedules(id),
                                   screen_id INT REFERENCES public.screens(id),
                                   available_seats INT NOT NULL
);

CREATE TABLE public.tickets (
                                id SERIAL PRIMARY KEY,
                                user_id INT REFERENCES public.users(id),
                                show_time_id INT REFERENCES public.show_times(id),
                                seat_id INT REFERENCES public.seats(id)
);

CREATE TABLE public.users (
                              id SERIAL PRIMARY KEY,
                              username VARCHAR(50) NOT NULL UNIQUE,
                              email VARCHAR(255) NOT NULL UNIQUE,
                              password_hash VARCHAR(255) NOT NULL,
                              status public.userstatus NOT NULL DEFAULT 'active',
                              type public.usertype NOT NULL DEFAULT 'user'
);

-- Indexes and Constraints
ALTER TABLE public.tickets ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE public.tickets ADD CONSTRAINT fk_show_time FOREIGN KEY (show_time_id) REFERENCES public.show_times(id);
ALTER TABLE public.tickets ADD CONSTRAINT fk_seat FOREIGN KEY (seat_id) REFERENCES public.seats(id);

-- Sequences
CREATE SEQUENCE public.addresses_id_seq OWNED BY public.addresses.id;
CREATE SEQUENCE public.movies_id_seq OWNED BY public.movies.id;
CREATE SEQUENCE public.permissions_id_seq OWNED BY public.permissions.id;
CREATE SEQUENCE public.roles_id_seq OWNED BY public.roles.id;
CREATE SEQUENCE public.schedules_id_seq OWNED BY public.schedules.id;
CREATE SEQUENCE public.screens_id_seq OWNED BY public.screens.id;
CREATE SEQUENCE public.seats_id_seq OWNED BY public.seats.id;
CREATE SEQUENCE public.show_times_id_seq OWNED BY public.show_times.id;
CREATE SEQUENCE public.tickets_id_seq OWNED BY public.tickets.id;
CREATE SEQUENCE public.users_id_seq OWNED BY public.users.id;
