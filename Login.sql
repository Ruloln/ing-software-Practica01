-- Database: postgres

-- DROP DATABASE postgres;

CREATE DATABASE postgres
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Spanish_Mexico.1252'
       LC_CTYPE = 'Spanish_Mexico.1252'
       CONNECTION LIMIT = -1;

COMMENT ON DATABASE postgres
  IS 'default administrative connection database';

-- Table: public."Usuario"

-- DROP TABLE public."Usuario";

CREATE TABLE public."Usuario"
(
  "Numero de cuenta" character varying(10) NOT NULL,
  "Nombre" character varying(64),
  "Correo" character varying(30),
  CONSTRAINT cuenta PRIMARY KEY ("Numero de cuenta")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Usuario"
  OWNER TO postgres;
-- Table: public."Password"

-- DROP TABLE public."Password";

CREATE TABLE public."Password"
(
  "Numero de cuenta" character varying(10) NOT NULL,
  pass character varying(30),
  CONSTRAINT numcuenta PRIMARY KEY ("Numero de cuenta"),
  CONSTRAINT ncuenta FOREIGN KEY ("Numero de cuenta")
      REFERENCES public."Usuario" ("Numero de cuenta") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."Password"
  OWNER TO postgres;
