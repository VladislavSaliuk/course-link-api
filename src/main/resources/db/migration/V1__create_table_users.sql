create sequence user_id_seq
    start with 1 increment by 1;

create table users (
    user_id bigint not null default nextval('user_id_seq'),
    username varchar(255) not null unique,
    email varchar(255) not null unique,
    password varchar(255) not null,
    firstname varchar(255) not null,
    lastname varchar(255) not null,
    role varchar(255) check (role in ('STUDENT','TEACHER','ADMIN', 'ADMIN_TEACHER', 'ADMIN_STUDENT')) not null,
    status varchar(255) check (status in ('ACTIVE','BANNED')) not null,
    primary key (user_id)
);