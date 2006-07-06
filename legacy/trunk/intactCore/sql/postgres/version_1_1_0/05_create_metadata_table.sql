
--
-- Creating table for storing metadata information.
--

CREATE TABLE IA_DB_INFO (
	 dbi_key		    VARCHAR(20)	  NOT NULL PRIMARY KEY
	,value			    VARCHAR(20)	  NOT NULL
	,created_date		TIMESTAMP	  DEFAULT  now() 	NOT NULL
	,created_user		VARCHAR(30)	  DEFAULT  USER    	NOT NULL
	,updated_date		TIMESTAMP	  DEFAULT  now() 	NOT NULL
	,updated_user		VARCHAR(30)	  DEFAULT  USER    	NOT NULL
);


INSERT INTO IA_DB_INFO (
	 dbi_key
	,value
)
VALUES
(	 'schema_version'
	,'1.1.0'
);
