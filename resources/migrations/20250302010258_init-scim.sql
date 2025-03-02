-- init-scim
--$up
create schema scim;
--$
create table scim.resourcetype (
  id text primary key,
  _created_at timestamptz default current_timestamp,
  _updated_at timestamptz default current_timestamp,
  resource jsonb
)
--$
create table scim."schema" (
  id text primary key,
  _created_at timestamptz default current_timestamp,
  _updated_at timestamptz default current_timestamp,
  resource jsonb
)
--$down
drop table if exists scim.resourcetype;
--$
drop table if exists scim."schema";
--$
drop schema if exists scim;
