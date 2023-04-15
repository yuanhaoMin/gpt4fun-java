IF OBJECT_ID('dbo.guild_chat_log') IS NULL
    CREATE TABLE guild_chat_log (
      id INT IDENTITY(1,1) PRIMARY KEY,
      guild_id VARCHAR(255) UNIQUE,
      messages NVARCHAR(MAX),
      last_chat_time VARCHAR(255),
      last_chat_user_name VARCHAR(255)
    );

IF OBJECT_ID('dbo.user_chat_log') IS NULL
    CREATE TABLE user_chat_log (
      id INT IDENTITY(1,1) PRIMARY KEY,
      user_id VARCHAR(255) UNIQUE,
      messages NVARCHAR(MAX),
      last_chat_time VARCHAR(255),
    );