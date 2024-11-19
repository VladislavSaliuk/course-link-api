create sequence task_category_id_seq
    start with 1 increment by 1;

create table task_categories (
    task_category_id bigint default nextval('task_category_id_seq') not null,
    task_category_name varchar(255) not null unique,
    primary key (task_category_id)
);