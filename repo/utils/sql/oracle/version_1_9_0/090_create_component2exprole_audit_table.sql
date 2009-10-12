PROMPT Creating table "IA_COMPONENT2EXPROLE_audit"
create table IA_COMPONENT2EXPROLE_audit
   (
          EVENT                   CHAR(1) NOT NULL
       ,  COMPONENT_AC            VARCHAR2(30) NOT NULL
       ,  EXPERIMENTALROLE_AC     VARCHAR2(30) NOT NULL
       ,  created                 DATE 
       ,  updated                 DATE
       ,  userstamp               VARCHAR2(30)   
       ,  created_user            VARCHAR2(30)   NOT NULL ENABLE 
   )
tablespace &&intactMainTablespace;
