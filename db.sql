CREATE TABLE users_web (
	id integer NOT NULL,
	login character varying(1024) NOT NULL,
	password character varying(100) NOT NULL,
	name character varying(100) NOT NULL,
	middlename character varying(100) NOT NULL,
	surname character varying(100) NOT NULL,
	phone character varying(25),
	email character varying(100) NOT NULL,
	languageid smallint NOT NULL DEFAULT 1,
	CONSTRAINT pk_users_web PRIMARY KEY (id),
	CONSTRAINT pk_login UNIQUE (login)
);
ALTER TABLE users_web OWNER TO postgres;
CREATE TABLE meter_user (
	iduser integer NOT NULL,
	idmeter integer NOT NULL,
	lastcash double precision,
	type_device_id integer,
	idaccount integer,
	CONSTRAINT pk_meter_user UNIQUE (iduser, idmeter),
	CONSTRAINT pk_idaccount UNIQUE (idaccount)
);
ALTER TABLE meter_user ADD CONSTRAINT fk_iduser FOREIGN KEY(iduser) 
	REFERENCES users_web(id) ON DELETE CASCADE;
ALTER TABLE meter_user ADD CONSTRAINT fk_idmeter FOREIGN KEY(idmeter) 
	REFERENCES meter(id) ON DELETE CASCADE;
ALTER TABLE meter_user OWNER TO postgres;
COMMENT ON COLUMN meter_user.type_device_id IS 
	'Енергопостачання/Водопостачання';