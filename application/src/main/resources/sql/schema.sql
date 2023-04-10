CREATE TABLE IF NOT EXISTS public.guild_chat_log (
  id SERIAL PRIMARY KEY,
  guild_id VARCHAR(255) UNIQUE NOT NULL,
  messages TEXT NOT NULL,
  last_chat_time TIMESTAMP NOT NULL,
  last_chat_user_name VARCHAR(255) NOT NULL
);