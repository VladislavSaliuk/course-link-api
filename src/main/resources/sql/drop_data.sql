truncate table public.users restart identity cascade;

truncate table task_categories restart identity cascade;

truncate table booking_slots restart identity cascade;

truncate table defence_sessions restart identity cascade;

alter sequence user_id_seq restart with 1;

alter sequence task_category_id_seq restart with 1;

alter sequence booking_slot_id_seq restart with 1;

alter sequence defence_session_id_seq restart with 1;
