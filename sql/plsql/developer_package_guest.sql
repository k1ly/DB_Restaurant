alter session set NLS_LANGUAGE = AMERICAN;

create or replace package guest_package as
    function find_category_by_id(p_id in categories.id%type) return sys_refcursor;
    function get_categories(p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor;
    function find_dish_by_id(p_id in dishes.id%type) return sys_refcursor;
    function get_dishes(p_start in number, p_size in number, p_sort in nvarchar2,
                              p_direction in varchar2) return sys_refcursor;
    function count_dishes return number;
    function find_dishes_by_filter(p_filter in nvarchar2, p_start in number, p_size in number,
                                         p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor;
    function count_dishes_by_filter(p_filter nvarchar2) return number;
    function find_dishes_by_category(p_category_id in dishes.category_id%type, p_start in number,
                                           p_size in number,
                                           p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor;
    function count_dishes_by_category(p_category_id dishes.category_id%type) return number;
    function get_reviews(p_start in number, p_size in number, p_sort in nvarchar2,
                               p_direction in varchar2) return sys_refcursor;
    function count_reviews return number;
    function find_role_by_name(p_name in roles.name%type) return sys_refcursor;
    function find_user_by_login(p_login in users.login%type) return sys_refcursor;
    procedure register_user(p_login in users.login%type, p_password in users.password%type, p_name in users.name%type,
                            p_email in users.email%type, p_phone in users.phone%type);
end guest_package;

create or replace package body guest_package as

    function find_category_by_id(p_id in categories.id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from categories where id = p_id;
        return c_cursor;
    end;

    function get_categories(p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from categories order by ' || p_sort || ' ' || p_direction;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function find_dish_by_id(p_id in dishes.id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from dishes where id = p_id;
        return c_cursor;
    end;

    function get_dishes(p_start in number, p_size in number, p_sort in nvarchar2,
                              p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from dishes order by ' || p_sort || ' ' || p_direction ||
                   ') where rownum <= ' || p_start || ', ' ||
                   p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_dishes return number
        is
        v_count number;
    begin
        select nvl((select count(*) from dishes), -1) into v_count from dual;
        return v_count;
    end;

    function find_dishes_by_filter(p_filter in nvarchar2, p_start in number, p_size in number,
                                         p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from dishes' || ' where name like "%' || p_filter || '%"' || ' order by ' ||
                   p_sort || ' ' ||
                   p_direction || ') where rownum <= ' || p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_dishes_by_filter(p_filter nvarchar2) return number
        is
        v_count number;
    begin
        select nvl((select count(*) from dishes where name like '%' || p_filter || '%'), -1) into v_count from dual;
        return v_count;
    end;

    function find_dishes_by_category(p_category_id in dishes.category_id%type, p_start in number,
                                           p_size in number,
                                           p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from dishes where category_id = ' || p_category_id || ' order by ' ||
                   p_sort || ' ' ||
                   p_direction || ') where rownum <= ' || p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_dishes_by_category(p_category_id dishes.category_id%type) return number
        is
        v_count number;
    begin
        select nvl((select count(*) from dishes where category_id = p_category_id), -1) into v_count from dual;
        return v_count;
    end;

    function get_reviews(p_start in number, p_size in number, p_sort in nvarchar2,
                               p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from reviews order by ' || p_sort || ' ' || p_direction ||
                   ') where rownum <= ' || p_start || ', ' ||
                   p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_reviews return number
        is
        v_count number;
    begin
        select nvl((select count(*) from reviews), -1) into v_count from dual;
        return v_count;
    end;

    function find_role_by_name(p_name in roles.name%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from roles where name = p_name;
        return c_cursor;
    end;

    function find_user_by_login(p_login in users.login%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from users where login = p_login;
        return c_cursor;
    end;

    procedure register_user(p_login in users.login%type, p_password in users.password%type, p_name in users.name%type,
                            p_email in users.email%type, p_phone in users.phone%type)
        is
        v_role_id   roles.id%type;
        v_status_id statuses.id%type;
        v_user_id   users.id%type;
        v_order_id  orders.id%type;
    begin
        select id into v_role_id from roles where name = 'CLIENT';
        select id into v_status_id from statuses where name = 'CREATED';
        insert into users(login, password, name, email, phone, role_id)
        values (p_login, p_password, p_name, p_email, p_phone, v_role_id);
        select id into v_user_id from users where login = p_login;
        insert into orders(status_id, customer_id) values (v_status_id, v_user_id);
        select id into v_order_id from orders where status_id = v_status_id and customer_id = v_user_id;
        update users set order_id = v_order_id where id = v_user_id;
    end;

end guest_package;