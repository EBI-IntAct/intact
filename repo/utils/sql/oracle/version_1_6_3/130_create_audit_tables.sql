
PROMPT Creating table "IA_COMPONENT2EXP_PREPS_audit"
create table IA_COMPONENT2EXP_PREPS_audit
        (
                 EVENT                              CHAR(1) NOT NULL
                ,COMPONENT_AC                       VARCHAR2(30) NOT NULL
                ,CVOBJECT_AC                        VARCHAR2(30) NOT NULL
                ,DEPRECATED                         NUMBER(1) NOT NULL
                ,CREATED                            DATE NOT NULL
                ,USERSTAMP                          VARCHAR2(30) NOT NULL
                ,UPDATED                            DATE NOT NULL
                ,CREATED_USER                       VARCHAR2(30) NOT NULL
        )
tablespace &&intactAuditTablespace
/



PROMPT Creating table "IA_COMPONENT2PART_DETECT_audit"
create table IA_COMPONENT2PART_DETECT_audit
        (
                 EVENT                              CHAR(1) NOT NULL
                ,COMPONENT_AC                       VARCHAR2(30) NOT NULL
                ,CVOBJECT_AC                        VARCHAR2(30) NOT NULL
                ,DEPRECATED                         NUMBER(1) NOT NULL
                ,CREATED                            DATE NOT NULL
                ,USERSTAMP                          VARCHAR2(30) NOT NULL
                ,UPDATED                            DATE NOT NULL
                ,CREATED_USER                       VARCHAR2(30) NOT NULL
        )
tablespace &&intactAuditTablespace
/



PROMPT Creating table "IA_INSTITUTION2ANNOT_audit"
create table IA_INSTITUTION2ANNOT_audit
        (
                 EVENT                              CHAR(1) NOT NULL
                ,INSTITUTION_AC                     VARCHAR2(30) NOT NULL
                ,ANNOTATION_AC                      VARCHAR2(30) NOT NULL
                ,DEPRECATED                         NUMBER(1) NOT NULL
                ,CREATED                            DATE NOT NULL
                ,USERSTAMP                          VARCHAR2(30) NOT NULL
                ,UPDATED                            DATE NOT NULL
                ,CREATED_USER                       VARCHAR2(30) NOT NULL
        )
tablespace &&intactAuditTablespace
/



PROMPT Creating table "IA_INSTITUTION_ALIAS_audit"
create table IA_INSTITUTION_ALIAS_audit
        (
                 EVENT                              CHAR(1) NOT NULL
                ,AC                                 VARCHAR2(30) NOT NULL
                ,DEPRECATED                         NUMBER(1) NOT NULL
                ,CREATED                            DATE NOT NULL
                ,UPDATED                            DATE NOT NULL
                ,USERSTAMP                          VARCHAR2(30) NOT NULL
                ,ALIASTYPE_AC                       VARCHAR2(30) NULL
                ,PARENT_AC                          VARCHAR2(30) NULL
                ,OWNER_AC                           VARCHAR2(30) NULL
                ,NAME                               VARCHAR2(30) NULL
                ,CREATED_USER                       VARCHAR2(30) NOT NULL
        )
tablespace &&intactAuditTablespace
/



PROMPT Creating table "IA_INSTITUTION_XREF_audit"
create table IA_INSTITUTION_XREF_audit
        (
                 EVENT                              CHAR(1) NOT NULL
                ,AC                                 VARCHAR2(30) NOT NULL
                ,DEPRECATED                         NUMBER(1) NOT NULL
                ,CREATED                            DATE NOT NULL
                ,UPDATED                            DATE NOT NULL
                ,USERSTAMP                          VARCHAR2(30) NOT NULL
                ,QUALIFIER_AC                       VARCHAR2(30) NULL
                ,DATABASE_AC                        VARCHAR2(30) NULL
                ,PARENT_AC                          VARCHAR2(30) NULL
                ,OWNER_AC                           VARCHAR2(30) NULL
                ,PRIMARYID                          VARCHAR2(30) NULL
                ,SECONDARYID                        VARCHAR2(30) NULL
                ,DBRELEASE                          VARCHAR2(10) NULL
                ,CREATED_USER                       VARCHAR2(30) NOT NULL
        )
tablespace &&intactAuditTablespace
/
