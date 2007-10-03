PROMPT "Creating IMEx sequence"
CREATE SEQUENCE imex_sequence;

PROMPT "Creating public synonym on IMEx sequence"
Create public synonym imex_sequence for intact.imex_sequence;

PROMPT "Granting select on IMEx sequence to INTACT_CURATOR"
grant select on imex_sequence to INTACT_CURATOR;