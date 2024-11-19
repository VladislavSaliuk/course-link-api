create sequence defence_session_id_seq
    start with 1 increment by 1;

create table defence_sessions (
    defence_session_id bigint default nextval('defence_session_id_seq') not null,
    description varchar(255) not null,
    defense_date date not null,
    start_time time(6) not null,
    end_time time(6) not null,
    break_duration integer,
    task_category_id bigint not null,
    primary key (defence_session_id)
);

alter table if exists defence_sessions
    add constraint Fk_defence_sessions_task_category_id
    foreign key (task_category_id) references task_categories;