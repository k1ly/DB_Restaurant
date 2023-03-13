alter session set NLS_LANGUAGE = AMERICAN;

create profile user_profile limit
    password_life_time 180
    sessions_per_user 20
    failed_login_attempts 5
    password_lock_time 60
    password_reuse_time 10
    connect_time 180
    idle_time 60;

create role role_admin;
grant connect to role_admin;
grant execute on developer.guest_package to role_admin;
grant execute on developer.client_package to role_admin;
grant execute on developer.manager_package to role_admin;
grant execute on developer.admin_package to role_admin;
create user admin
    identified by restaurant_admin1203
    default tablespace RESTAURANT_TS
    profile user_profile
    quota unlimited on RESTAURANT_TS;
grant role_admin to admin;

create role role_manager;
grant connect to role_manager;
grant execute on developer.guest_package to role_manager;
grant execute on developer.client_package to role_manager;
grant execute on developer.manager_package to role_manager;
create user manager
    identified by restaurant_manager1203
    default tablespace RESTAURANT_TS
    profile user_profile
    quota unlimited on RESTAURANT_TS;
grant role_manager to manager;

create role role_client;
grant connect to role_client;
grant execute on developer.guest_package to role_client;
grant execute on developer.client_package to role_client;
create user client
    identified by restaurant_client1203
    default tablespace RESTAURANT_TS
    profile user_profile
    quota unlimited on RESTAURANT_TS;
grant role_client to client;

create role role_guest;
grant connect to role_guest;
grant execute on developer.guest_package to role_guest;
create user guest
    identified by restaurant_guest1203
    default tablespace RESTAURANT_TS
    profile user_profile
    quota unlimited on RESTAURANT_TS;
grant role_guest to guest;
