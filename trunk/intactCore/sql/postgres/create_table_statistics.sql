/**************************************************************************************************************************

  Package:    IntAct PostgreSQL DDL step 1

  Purpose:    Create PostgreSQL IA_Statistics table for IntAct

  Usage:      sqlplus username/password @create_tables.sql


  $Date$

  $Locker$


  **************************************************************************************************************************/


DROP SEQUENCE statistics_seq;

DROP TABLE IA_Statistics;

/* first create the sequence which allows to specify the ac field as an autoincremented field */
CREATE SEQUENCE statistics_seq;

/* This table will record the amount of some specific data in the IntAct database, at a given time */
CREATE TABLE IA_Statistics
(     ac                       INT4
                               DEFAULT nextval('statistics_seq')
                               CONSTRAINT pk_Statistics
                               PRIMARY KEY
      , timestamp              TIMESTAMP DEFAULT  now()   NOT NULL
      , protein_number         INT4      DEFAULT  0       NOT NULL
      , interaction_number     INT4      DEFAULT  0       NOT NULL
      , binary_interactions    INT4      DEFAULT  0       NOT NULL
      , complex_interactions   INT4      DEFAULT  0       NOT NULL
      , experiment_number      INT4      DEFAULT  0       NOT NULL
      , term_number            INT4      DEFAULT  0       NOT NULL
);

COMMENT ON COLUMN IA_Statistics.timestamp IS
    'remind the moment of record for this line';
COMMENT ON COLUMN IA_Statistics.protein_number IS
    'count how many proteins are stored in the database';
COMMENT ON COLUMN IA_Statistics.interaction_number IS
    'count how many interactions are referred in the database';
COMMENT ON COLUMN IA_Statistics.binary_interactions IS
    'how many interactions contain only 2 interactors';
COMMENT ON COLUMN IA_Statistics.complex_interactions IS
    'how many interactions contain more than 2 interactors';
COMMENT ON COLUMN IA_Statistics.experiment_number IS
    'how many different experiments are stored in the database';
COMMENT ON COLUMN IA_Statistics.term_number IS
    'how many different controlled vocabularies terms are stored in the database';

