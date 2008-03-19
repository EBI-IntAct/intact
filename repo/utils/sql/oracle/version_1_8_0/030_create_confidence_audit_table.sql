PROMPT Creating table "IA_CONFIDENCE_audit"
create table IA_CONFIDENCE_audit
	(
		 EVENT                              CHAR(1) NOT NULL
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,INTERACTION_AC                     VARCHAR2(30) NULL
		,CONFIDENCETYPE_AC                  VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,VALUE                              VARCHAR2(30) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace INTACT_TAB
/