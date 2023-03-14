create table if not exists migrations (
  key text CONSTRAINT pkey PRIMARY KEY
)
----
-- src: https://gist.github.com/pesterhazy/9f7c0a7a9edd002759779c1732e0ac43
create or replace function idempotent(migration_name text,code text) returns void as $$
begin
if exists (select key from migrations where key=migration_name) then
  raise notice 'Migration already applied: %', migration_name;
else
  raise notice 'Running migration: %', migration_name;
  execute code;
  insert into migrations (key) VALUES (migration_name);
end if;
end;
$$ language plpgsql strict;
----
create table IF NOT EXISTS my_test (
    id int
);
----
insert into my_test(id) values (42);
