---------------------------
-- Creating audit table
---------------------------

@setup_tablespaces.sql

PROMPT Creating table "IA_Publication_audit"
CREATE TABLE IA_Publication_audit
(       event                   CHAR(1)         NOT NULL
      , ac                      VARCHAR2(30)    NOT NULL
      , deprecated              NUMBER(1)       NOT NULL
      , created                 DATE            NOT NULL
      , updated                 DATE            NOT NULL
      , userstamp               VARCHAR2(30)    NOT NULL
      , owner_ac                VARCHAR2(30)
      , shortLabel              VARCHAR2(20)
      , fullName                VARCHAR2(250)
      , created_user            VARCHAR2(30)    NOT NULL
      , pmid                    VARCHAR2(20)    NOT NULL
)
TABLESPACE &&intactAuditTablespace
;


PROMPT Creating table "IA_pub2annot_audit"
CREATE TABLE IA_Pub2Annot_audit
(       event                   CHAR(1)         NOT NULL
     ,  publication_ac          VARCHAR2(30)    NOT NULL
     ,  annotation_ac           VARCHAR2(30)    NOT NULL
     ,  deprecated              NUMBER(1)       NOT NULL
     ,  created                 DATE            NOT NULL
     ,  userstamp               VARCHAR2(30)    NOT NULL
     ,  updated                 DATE            NOT NULL
     ,  created_user            VARCHAR2(30)    NOT NULL
)
TABLESPACE &&intactAuditTablespace
;


PROMPT Creating table "IA_Key_Assigner_Request_audit"
CREATE TABLE IA_Key_Assigner_Request_audit
(
        event                   CHAR(1)         NOT NULL
      , submission              NUMBER(10)      NOT NULL
      , fromId                  NUMBER(10)      NOT NULL
      , toId                    NUMBER(10)      NOT NULL
      , partner                 VARCHAR2(30)    NOT NULL
      , service_url             VARCHAR2(100)   NOT NULL
      , created                 DATE            NOT NULL
      , created_user            VARCHAR2(30)    NOT NULL
)
TABLESPACE &&intactAuditTablespace
;