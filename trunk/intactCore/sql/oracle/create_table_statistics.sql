/**************************************************************************************************************************

  Package:    IntAct

  Purpose:    Create Oracle IA_Statistics table for IntAct

  Usage:      sqlplus username/password @create_tables.sql


  $Date$

  $Locker$


  **************************************************************************************************************************/

/* This table will record the amount of some specific data in the IntAct database, at a given time */



PROMPT Creating table "IA_Statistics"
CREATE TABLE IA_Statistics
(     ac                          INT NOT NULL
                                  CONSTRAINT pk_Statistics
                                  PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
      , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
      , protein_number         NUMBER(10)      DEFAULT  0       NOT NULL
      , interaction_number     NUMBER(10)      DEFAULT  0       NOT NULL
      , binary_interactions    NUMBER(10)      DEFAULT  0       NOT NULL
      , complex_interactions   NUMBER(10)      DEFAULT  0       NOT NULL
      , experiment_number      NUMBER(10)      DEFAULT  0       NOT NULL
      , term_number            NUMBER(10)      DEFAULT  0       NOT NULL
)
TABLESPACE &&intactMainTablespace;

set term off
    COMMENT ON TABLE IA_Statistics.timestamp IS
    'remind the moment of record for this line'
    COMMENT ON TABLE IA_Statistics.proteinNumber IS
    'count how many proteins are stored in the database'
    COMMENT ON TABLE IA_Statistics.interactionNumber IS
    'count how many interactions are referred in the database'
    COMMENT ON TABLE IA_Statistics.binaryInteractions IS
    'how many interactions contain only 2 interactors'
    COMMENT ON TABLE IA_Statistics.complexInteractions IS
    'how many interactions contain more than 2 interactors'
    COMMENT ON TABLE IA_Statistics.experimentNumber IS
    'how many different experiments are stored in the database'
    COMMENT ON TABLE IA_Statistics.termNumber IS
    'how many different controlled vocabularies terms are stored in the database'
set term on

