create sequence booking_slot_id_seq
    start with 1 increment by 1;

create table booking_slots (
    booking_slot_id bigint default nextval('booking_slot_id_seq') not null,
    start_time time(6) not null,
    end_time time(6) not null,
    is_booked boolean not null,
    user_id bigint,
    defence_session_id bigint not null,
    primary key (booking_slot_id)
);

alter table if exists booking_slots add constraint Fk_booking_slots_user_id
    foreign key (user_id) references users;

alter table if exists booking_slots add constraint Fk_booking_slots_defence_session_id
    foreign key (defence_session_id) references defence_sessions;