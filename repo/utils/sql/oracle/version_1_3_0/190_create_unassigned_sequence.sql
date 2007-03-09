-- creates the "unassigned" sequence
CREATE SEQUENCE unassigned_seq;

-- synonym
create public synonym unassigned_seq for INTACT.unassigned_seq;

-- permissions
grant select on unassigned_seq to INTACT_SELECT ;
