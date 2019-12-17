select 1;
create extension pgcrypto;

create table if not exists tpc(
    id serial primary key,
    username text unique not null,
    salt text not null,
    password text not null,
    email text unique null,
    create_timestamp timestamp not null default now(),
    update_timestamp timestamp not null default now(),
    last_login timestamp
);

/* verify the table exists */
select column_name from information_schema.columns where table_name = 'users';

/* update the timestamp automatically */
create or replace function user_update_timestamp()
returns trigger as $$
begin
    new.update_timestamp = now();
    return new;
end
$$ language plpgsql;

/* call the update timestamp function upon update */
create trigger user_update_timestamp before update on users for each row execute procedure user_update_timestamp();

/* sample users */
insert into users (username, email, password)
values
( 'user.0', 'user.0@example.com', crypt('2FederateM0re','bf')),
( 'user.1', 'user.1@example.com', crypt('2FederateM0re','bf'));

/* sample users with bcrypt (10 iterations) */
INSERT INTO users (username, email, password)
SELECT
  'user.' || i::text,
  'user.' || i::text || '@example.com',
  crypt('2FederateM0re', gen_salt('bf', 10))
FROM generate_series(0,899) as t(i);

/* sample users with unsalted sha-256 */
INSERT INTO users (username, email, password)
SELECT
  'user.' || i::text,
  'user.' || i::text || '@example.com',
  'ca4f7ca42c410bb3cf48df273bacaac81280be32bdead001c5e2cc5f00277d5e'
FROM generate_series(900,999) as t(i);

select * from users;

/* function to log user in by email */
create or replace function user_login_by_email(
    in_email text,
    in_password text
)
returns boolean as $$
declare success boolean;
begin
    select ( password = crypt(in_password, 'bf')) into success from users where email = in_email;
    return success;
end
$$ language plpgsql;


/* example call to log user in */
select user_login_by_email('user.0@example.com','2FederateM0re');
select user_login_by_email('user.0@example.com', 'wrong');

/* cleanup */
drop function user_login_by_email;
drop trigger user_update_timestamp;
drop function user_update_timestamp;
drop table users;
