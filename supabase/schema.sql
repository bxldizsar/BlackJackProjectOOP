-- Blackjack: the shared games table. One row = one game room.
-- Run this in Supabase > SQL Editor. No keys or passwords in this file!

create table public.games (
  code        text primary key check (code ~ '^[A-Z0-9]{4}$'),
  status      text not null check (status in ('waiting', 'playing', 'finished', 'timeout')),
  turn        text check (turn in ('p1', 'p2', 'dealer')),
  deck        text not null default '',
  dealer_hand text not null default '',
  p1_hand     text not null default '',
  p2_hand     text not null default '',
  round       int  not null default 1,
  version     int  not null default 0,
  p1_id       uuid not null,
  p2_id       uuid,
  updated_at  timestamptz not null default now()
);

-- the game can read, create and change rows, but never delete them
grant select, insert, update on public.games to authenticated;

-- every row is hidden until a policy allows it (policies come next)
alter table public.games enable row level security;

- POLICIES: who can see and change which row

-- read: the two players of the room, or anyone looking for a free waiting room
create policy games_select on public.games for select to authenticated
  using (auth.uid() in (p1_id, p2_id) or (status = 'waiting' and p2_id is null));

-- create: only a new waiting room, with yourself as player 1
create policy games_insert on public.games for insert to authenticated
  with check (p1_id = auth.uid() and status = 'waiting' and p2_id is null);

-- change: the two players, or someone joining a free waiting room
create policy games_update on public.games for update to authenticated
  using (auth.uid() in (p1_id, p2_id) or (status = 'waiting' and p2_id is null))
  with check (auth.uid() in (p1_id, p2_id));

-- TRIGGER: checks every change before it is saved
create function public.games_before_update() returns trigger
language plpgsql set search_path = '' as $$
begin
  -- the version must go up by exactly one, so an old state can't overwrite a new one
  if new.version <> old.version + 1 then
    raise exception 'stale version % (expected %)', new.version, old.version + 1;
  end if;
  -- player 1 never changes
  if new.p1_id <> old.p1_id then
    raise exception 'p1_id cannot change';
  end if;
  -- player 2 can only be set once
  if old.p2_id is not null and new.p2_id is distinct from old.p2_id then
    raise exception 'p2_id cannot change';
  end if;
  new.updated_at := now();
  return new;
end $$;

create trigger games_before_update before update on public.games
  for each row execute function public.games_before_update();