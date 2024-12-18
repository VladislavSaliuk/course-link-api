truncate table public.users restart identity cascade;

truncate table public.task_categories restart identity cascade;

truncate table public.booking_slots restart identity cascade;

truncate table public.defence_sessions restart identity cascade;

alter sequence user_id_seq restart with 1;

alter sequence task_category_id_seq restart with 1;

alter sequence booking_slot_id_seq restart with 1;

alter sequence defence_session_id_seq restart with 1;
