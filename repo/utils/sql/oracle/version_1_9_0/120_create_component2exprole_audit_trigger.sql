PROMPT Creating audit trigger for "ia_component2exprole"
create or replace trigger trgAud_ia_component2exprole
   before update or delete
   on ia_component2exprole
   for each row

declare
   l_event char(1);
begin

   if deleting then          l_event := 'D';
   elsif updating then
       l_event := 'U';
   end if ;        insert into ia_component2exprole_audit
       ( event
       , component_ac
       , experimentalrole_ac
       )
   values
       ( l_event
       , :old.component_ac
       , :old.experimentalrole_ac
       );
end;
/
