PROMPT Creating table "IA_IMEX_IMPORT_audit"
create table IA_IMEX_IMPORT_audit
	(
		 EVENT                              CHAR(1) NOT NULL
		,ID                                 NUMBER(19) NOT NULL
		,CREATED                            TIMESTAMP(6) NULL
		,CREATED_USER                       VARCHAR2(30) NULL
		,UPDATED                            TIMESTAMP(6) NULL
		,USERSTAMP                          VARCHAR2(30) NULL
		,ACTIVATIONTYPE                     VARCHAR2(255) NULL
		,COUNT_TOTAL                        NUMBER(10) NULL
		,COUNT_NOT_FOUND                    NUMBER(10) NULL
		,COUNT_FAILED                       NUMBER(10) NULL
		,IMPORT_DATE                        TIMESTAMP(6) NULL
		,REPOSITORY                         VARCHAR2(255) NULL
	)
tablespace &&intactIndexTablespace;
/

PROMPT Creating table "IA_IMEX_IMPORT_PUB_audit"
create table IA_IMEX_IMPORT_PUB_audit
	(
		 EVENT                              CHAR(1) NOT NULL
		,PMID                               VARCHAR2(50) NOT NULL
		,CREATED                            TIMESTAMP(6) NOT NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
		,UPDATED                            TIMESTAMP(6) NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,MESSAGE                            CLOB
		,ORIGINAL_FILENAME                  VARCHAR2(255) NULL
		,RELEASE_DATE                       TIMESTAMP(6) NULL
		,STATUS                             VARCHAR2(255) NULL
		,IMEXIMPORT_ID                      NUMBER(19) NOT NULL
		,PROVIDER_AC                        VARCHAR2(30) NULL
	)
tablespace &&intactIndexTablespace;
/
