PROMPT Creating sequence for "ia_controlledvocab"

col cMaxVal new_value uMaxVal noprint
select max(SUBSTR(cv.identifier, -4)) cMaxVal FROM ia_controlledvocab cv where identifier like 'IA%';
col cIncVal new_value  uIncVal noprint
select &uMaxVal+1 cIncVal from dual;
CREATE SEQUENCE cv_local_seq
START WITH &uIncVal
INCREMENT BY   1
NOCACHE
NOCYCLE;

