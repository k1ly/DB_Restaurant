alter session set NLS_LANGUAGE = AMERICAN;

create tablespace RESTAURANT_TS
    datafile 'C:\app\tablespaces\restaurant_ts.dbf'
    size 100 m autoextend on next 10 m
    blocksize 8192
    logging
    online
    segment space management auto;

alter user developer default tablespace RESTAURANT_TS;

grant all privileges to developer;