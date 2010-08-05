PROMPT Creating user tables

create table ia_preference (pk number(19,0) not null, key varchar2(255 char), value varchar2(255 char), user_id number(19,0), primary key (pk));
create table ia_role (pk number(19,0) not null, name varchar2(255 char) not null unique, primary key (pk));
create table ia_user (pk number(19,0) not null, disabled number(1,0) not null, email varchar2(255 char) not null unique, firstName varchar2(255 char) not null, lastLogin timestamp, lastName varchar2(255 char) not null, login varchar2(255 char) not null unique, openIdUrl varchar2(255 char), password varchar2(255 char), primary key (pk));
create table ia_user2role (user_id number(19,0) not null, role_id number(19,0) not null, primary key (user_id, role_id));
create index idx_preference_key on ia_preference (key);
alter table ia_preference add constraint FK_PREF_USER foreign key (user_id) references ia_user;
create index idx_role_name on ia_role (name);
create index idx_user_email on ia_user (email);
create index idx_user_login on ia_user (login);
alter table ia_user2role add constraint FK_ROLE_USER foreign key (role_id) references ia_role;
alter table ia_user2role add constraint FK_USER_ROLES foreign key (user_id) references ia_user;
create sequence users_seq;

create public synonym ia_user for intact.ia_user;
create public synonym ia_role for intact.ia_role;
create public synonym ia_preference for intact.ia_preference;
create public synonym ia_user2role for intact.ia_user2role;
create public synonym users_seq for intact.users_seq;

commit;
