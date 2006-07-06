/* 
   Add new table for storing publications.
*/



/*--------------------------------
-- Creating Publication table
--------------------------------*/
SELECT 'IA_Publication';
CREATE TABLE IA_Publication
(       ac                      VARCHAR  (30)    NOT NULL CONSTRAINT pk_Publication PRIMARY KEY
      , deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
      , created                 TIMESTAMP        DEFAULT  now()   NOT NULL
      , updated                 TIMESTAMP        DEFAULT  now()   NOT NULL
      , userstamp               VARCHAR  (30)    DEFAULT  USER    NOT NULL
      , owner_ac                VARCHAR  (30)    CONSTRAINT fk_Publication_owner REFERENCES IA_Institution(ac)
      , shortLabel              VARCHAR  (20)
      , fullName                VARCHAR  (250)
      , created_user            VARCHAR  (30)    DEFAULT  USER    NOT NULL
      , pmid                    VARCHAR  (20)    NOT NULL
)
;

SELECT 'Adding index on IA_Publication';
ALTER TABLE IA_Publication add constraint uq_IA_Publication_pmid unique(pmid);

    COMMENT ON TABLE IA_Publication IS
    'Describes the publication which has yielded information about experiments and Interactions.';
    COMMENT ON COLUMN IA_Publication.fullName IS
    'The full name of the object. ';
    COMMENT ON COLUMN IA_Publication.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Publication.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Publication.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Publication.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Publication.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Publication.created_user IS
    'Database user who has created the row.';
    COMMENT ON COLUMN IA_Publication.pmid IS
    'The pubmed ID of the publication.';


/*----------------------------------------------------------------------
-- Altering Experiment table
-- Modelling the relationship between Publication and experiment
-- only take to ass an extra column in the IA_Experiment table
-- and updating dependaEncies, namely: audit table and audit trigger.
----------------------------------------------------------------------*/

/* Add column allowing to model the relationship between publication and experiment. */
SELECT 'Adding extra column in IA_EXPERIMENT: publication_ac';
ALTER TABLE IA_EXPERIMENT
ADD publication_ac VARCHAR(30) CONSTRAINT fk_experiment_publication_ac REFERENCES IA_Publication(ac);

/* this will impact a modification on the audit trigger */

/*  new index on foreign key */
SELECT 'Adding index on table IA_EXPERIMENT, column publication_ac';
CREATE INDEX i_EXPERIMENT_publication_ac ON IA_EXPERIMENT(publication_ac);


/*-------------------------------------------------------
-- Create indirection table necessary for Annotations
-------------------------------------------------------*/
SELECT 'Creating table "IA_Pub2Annot"';
CREATE TABLE IA_Pub2Annot
(       publication_ac          VARCHAR (30)    NOT NULL CONSTRAINT fk_Pub2Annot_publication REFERENCES IA_Publication(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR (30)    NOT NULL CONSTRAINT fk_Pub2Annot_annotation  REFERENCES IA_Annotation(ac)  ON DELETE CASCADE
     ,  deprecated              DECIMAL (1)     DEFAULT  0      NOT NULL
     ,  created                 TIMESTAMP       DEFAULT  now()  NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER   NOT NULL
     ,  updated                 TIMESTAMP       DEFAULT  now()  NOT NULL
     ,  created_user            VARCHAR (30)    DEFAULT  USER   NOT NULL
)
;

SELECT 'Creating composite primary Key on "IA_Pub2Annot"';
ALTER TABLE IA_Pub2Annot
 ADD CONSTRAINT     pk_Pub2Annot
        PRIMARY KEY  (publication_ac, annotation_ac);

    COMMENT ON TABLE IA_Pub2Annot IS
    'IA_Pub2Annot. Link table from Annotation to Publication.';
    COMMENT ON COLUMN IA_Pub2Annot.publication_ac IS
    'Refers to a Publication to which the Annotation is linked.';
    COMMENT ON COLUMN IA_Pub2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Publication.';
    COMMENT ON COLUMN IA_Pub2Annot.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Pub2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Pub2Annot.updated IS
    'Date of the last update of the row.';



/*------------------------------------------------------
-- Creating table to log request to the key assigner
------------------------------------------------------*/
SELECT 'Creating table "IA_Key_Assigner_Request"';
CREATE TABLE IA_Key_Assigner_Request
(
        submission              DECIMAL(10)      NOT NULL
      , fromId                  DECIMAL(10)      NOT NULL
      , toId                    DECIMAL(10)      NOT NULL
      , partner                 VARCHAR(30)      NOT NULL
      , service_url             VARCHAR(100)     NOT NULL
      , created                 TIMESTAMP        DEFAULT  now()  NOT NULL
      , created_user            VARCHAR(30)      DEFAULT  USER   NOT NULL
)
;

SELECT 'Creating composite primary Key on "IA_Key_Assigner_Request"';
ALTER TABLE IA_Key_Assigner_Request
 ADD CONSTRAINT     pk_Key_Assigner_Request
        PRIMARY KEY  (service_url, submission);


    COMMENT ON TABLE IA_Key_Assigner_Request IS
    'IA_Key_Assigner_Request. Table where we log requests to the Key Assigner.';
    COMMENT ON COLUMN IA_Key_Assigner_Request.submission IS
    'Submission Id for that request.';
    COMMENT ON COLUMN IA_Key_Assigner_Request.fromId IS
    'First Id of the requested range.';
    COMMENT ON COLUMN IA_Key_Assigner_Request.toId IS
    'Last Id of the requested range.';
    COMMENT ON COLUMN IA_Key_Assigner_Request.partner IS
    'The partner who requested the range.';
    COMMENT ON COLUMN IA_Key_Assigner_Request.service_url IS
    'The URL of the Key Assigner accessed.';
    COMMENT ON COLUMN IA_Key_Assigner_Request.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Key_Assigner_Request.created_user IS
    'User who created the row.';


UPDATE ia_db_info
set value = '1.2.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';