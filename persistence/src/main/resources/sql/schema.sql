IF OBJECT_ID('dbo.discord_guild_chat_log') IS NULL
    CREATE TABLE discord_guild_chat_log (
      id INT IDENTITY(1,1) PRIMARY KEY,
      guild_id VARCHAR(255) UNIQUE,
      messages NVARCHAR(MAX),
      last_chat_time VARCHAR(255),
      last_chat_user_name VARCHAR(255),
    );

IF OBJECT_ID('dbo.chamber_user') IS NULL
    CREATE TABLE chamber_user (
      id INT IDENTITY(1,1) PRIMARY KEY,
      user_name VARCHAR(255) UNIQUE,
      password VARCHAR(255),
      created_time DATETIME,
      last_login_time DATETIME,
    );

IF OBJECT_ID('dbo.chamber_user_chat_log') IS NULL
    CREATE TABLE chamber_user_chat_log (
      id INT IDENTITY(1,1) PRIMARY KEY,
      user_id INT UNIQUE,
      messages NVARCHAR(MAX),
      last_chat_time DATETIME,
      FOREIGN KEY (user_id) REFERENCES chamber_user(id),
    );