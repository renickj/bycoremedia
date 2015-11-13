-- DB config for MySql

CREATE SCHEMA cm7management CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
GRANT ALL PRIVILEGES ON cm7management.*
 TO 'cm7management'@'localhost'
  IDENTIFIED BY 'cm7management';

CREATE SCHEMA cm7caefeeder CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
GRANT ALL PRIVILEGES ON cm7caefeeder.*
 TO 'cm7caefeeder'@'localhost'
  IDENTIFIED BY 'cm7caefeeder';

CREATE SCHEMA cm7master CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
GRANT ALL PRIVILEGES ON cm7master.*
 TO 'cm7master'@'localhost'
  IDENTIFIED BY 'cm7master';

CREATE SCHEMA cm7mcaefeeder CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
GRANT ALL PRIVILEGES ON cm7mcaefeeder.*
 TO 'cm7mcaefeeder'@'localhost'
  IDENTIFIED BY 'cm7mcaefeeder';

CREATE SCHEMA cm7replication CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
GRANT ALL PRIVILEGES ON cm7replication.*
 TO 'cm7replication'@'localhost'
  IDENTIFIED BY 'cm7replication';

