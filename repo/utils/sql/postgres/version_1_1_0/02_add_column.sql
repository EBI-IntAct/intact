--
-- Adding created_user on all main tables.
--

ALTER TABLE IA_ALIAS ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_ANNOTATION ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_BIOSOURCE ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_BIOSOURCE2ANNOT ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_COMPONENT ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_CONTROLLEDVOCAB ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_CV2CV ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_CVOBJECT2ANNOT ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_EXP2ANNOT ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_EXPERIMENT ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_FEATURE ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_FEATURE2ANNOT ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_INSTITUTION ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_INT2ANNOT ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_INT2EXP ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_INTACTNODE ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_INTERACTOR ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_RANGE ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_SEQUENCE_CHUNK ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;
ALTER TABLE IA_XREF ADD COLUMN created_user character varying(30) DEFAULT USER NOT NULL;


ALTER TABLE IA_SEQUENCE_CHUNK ADD COLUMN updated DATE DEFAULT now() NOT NULL;
ALTER TABLE IA_SEQUENCE_CHUNK ADD COLUMN created DATE DEFAULT now() NOT NULL;



CREATE TABLE IA_DB_INFO (
	 dbi_key		    VARCHAR(20)	NOT NULL PRIMARY KEY
	,value			    VARCHAR(20)	NOT NULL
	,created_date		TIMESTAMP		    DEFAULT  now() 	NOT NULL
	,created_user		VARCHAR(30)	DEFAULT  USER    	NOT NULL
	,updated_date		TIMESTAMP		    DEFAULT  now() 	NOT NULL
	,updated_user		VARCHAR(30)	DEFAULT  USER    	NOT NULL
);


CREATE TABLE IA_DB_INFO_AUDIT (
	event			    CHAR(1)
	,dbi_key		    VARCHAR(20)
	,value			    VARCHAR(20)
	,created_date		TIMESTAMP		 NOT NULL
	,created_user		VARCHAR(30) NOT NULL
	,updated_date		TIMESTAMP		 NOT NULL
	,updated_user		VARCHAR(30) NOT NULL
);

INSERT INTO IA_DB_INFO (
	 dbi_key
	,value
)
VALUES
(	 'schema_version'
	,'1.1.0'
);

