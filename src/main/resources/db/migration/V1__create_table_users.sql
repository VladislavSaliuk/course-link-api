create sequence user_id_seq
    start with 1 increment by 1;

create table users (
    user_id bigint default nextval('user_id_seq') not null,
    username varchar(255) not null unique,
    email varchar(255) not null unique,
    password varchar(255) not null,
    firstname varchar(255) not null,
    lastname varchar(255) not null,
    role varchar(255) check (role in ('STUDENT','TEACHER','ADMIN_TEACHER','ADMIN_STUDENT','ADMIN')),
    status varchar(255) check (status in ('ACTIVE','BANNED')),
    primary key (user_id)
);