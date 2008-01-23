-- Creating table "ia_confidence"
CREATE TABLE ia_confidence
(      ac                      varchar(30)    NOT NULL     CONSTRAINT pk_confidence    PRIMARY KEY
    ,  deprecated              bool      DEFAULT  false       NOT NULL
    ,  created                 timestamp            DEFAULT now()
    ,  updated                 timestamp            DEFAULT now()
    ,  userstamp               varchar(30)    DEFAULT  USER    NOT NULL
    ,  interaction_ac          varchar(30)    CONSTRAINT fk_confidence_interaction REFERENCES IA_INTERACTOR(ac)
    ,  confidencetype_ac       varchar(30)    CONSTRAINT fk_confidence_qualifier  REFERENCES IA_ControlledVocab(ac)
    ,  owner_ac                varchar(30)    CONSTRAINT fk_confidence_owner      REFERENCES IA_Institution(ac)
    ,  value		       varchar(30)	
    ,  created_user            varchar(30)    DEFAULT  USER    NOT NULL
);

CREATE INDEX i_confidence_value  on IA_confidence(value)    ;
CREATE INDEX i_confidence_confidencetype   on IA_confidence(confidencetype_ac) ;
CREATE INDEX i_confidence_interaction   on IA_confidence(interaction_ac) ;

-- set term off
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
--set term on