PROMPT Creating table "ia_confidence"
CREATE TABLE ia_confidence
(      ac                      VARCHAR2(30)    NOT NULL
                                               CONSTRAINT pk_confidence
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  confidencetype_ac            VARCHAR2(30)    CONSTRAINT fk_confidence$qualifier  REFERENCES IA_ControlledVocab(ac)
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_confidence$owner      REFERENCES IA_Institution(ac)
	,  value				   VARCHAR2(30)	
    ,  created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactIndexTablespace;

CREATE INDEX i_confidence$value  on IA_confidence(value)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_confidence$confidencetype   on IA_confidence(confidencetype_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE ia_confidence IS
    'Represents a confidence score.';
    COMMENT ON COLUMN ia_confidence.confidencetype_ac IS
    'Type of the confidences. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN ia_confidence.value IS
    'Value of the confidence.';
    COMMENT ON COLUMN ia_confidence.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN ia_confidence.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN ia_confidence.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN ia_confidence.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN ia_confidence.userstamp IS
    'Database user who has performed the last update of the column.';
set term on