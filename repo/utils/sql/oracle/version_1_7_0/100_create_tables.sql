
PROMPT Creating table "IA_IMEX_IMPORT"

CREATE TABLE IA_IMEX_IMPORT (
          id                 VARCHAR2(50)    NOT NULL PRIMARY KEY
	    , original_filename  VARCHAR2(200)	 NOT NULL
        , status             VARCHAR2(10)	 NOT NULL
        , pmid               VARCHAR2(50)	 NOT NULL
        , message            VARCHAR2(4000)	 NULL
        , provider_ac        VARCHAR2(30)    CONSTRAINT fk_Institution$provider REFERENCES IA_Institution(ac)
        , created            DATE		     DEFAULT  SYSDATE 	NOT NULL
 	    , created_user       VARCHAR2(30)	 DEFAULT  USER    	NOT NULL
 	    , updated            DATE		     DEFAULT  SYSDATE 	NOT NULL
 	    , userstamp          VARCHAR2(30)	 DEFAULT  USER    	NOT NULL
)
TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_IMEX_IMPORT IS
    'Represents a log of an IMEx import.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.original_filename IS
    'Filename from which the data were imported.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.status IS
    'Status of the data import.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.pmid IS
    'Publication identifier reflecting the data imported.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.message IS
    'Message attached to this operation.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.provider_ac IS
    'Institution from which this publication originates.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.created_user IS
    'Database user who has created the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.userstamp IS
    'Database user who has performed the last update of the row.';
set term on