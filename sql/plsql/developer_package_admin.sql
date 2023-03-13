alter session set NLS_LANGUAGE = AMERICAN;

create or replace package admin_package as
    function get_categories(p_start in number, p_size in number, p_sort in nvarchar2,
                                  p_direction in varchar2) return sys_refcursor;
    function count_categories return number;
    procedure add_category(p_name in categories.name%type);
    procedure update_category(p_id in categories.id%type, p_name in categories.name%type);
    procedure delete_category(p_id in categories.id%type);
    function find_categories_by_filter(p_filter in nvarchar2, p_start in number,
                                             p_size in number, p_sort in nvarchar2,
                                             p_direction in varchar2) return sys_refcursor;
    function count_categories_by_filter(p_filter nvarchar2) return number;
    procedure add_dish(p_name in dishes.name%type, p_description in dishes.description%type,
                       p_image_url in dishes.image_url%type,
                       p_weight in dishes.weight%type, p_price in dishes.price%type, p_discount in dishes.discount%type,
                       p_category_id in dishes.category_id%type);
    procedure update_dish(p_id in dishes.id%type, p_name in dishes.name%type, p_description in dishes.description%type,
                          p_image_url in dishes.image_url%type,
                          p_weight in dishes.weight%type, p_price in dishes.price%type,
                          p_discount in dishes.discount%type,
                          p_category_id in dishes.category_id%type);
    procedure delete_dish(p_id in dishes.id%type);
    function find_role_by_id(p_id in roles.id%type) return sys_refcursor;
    function get_roles(p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor;
    function find_user_by_id(p_id in users.id%type) return sys_refcursor;
    function get_users(p_start in number, p_size in number, p_sort in nvarchar2,
                             p_direction in varchar2) return sys_refcursor;
    function count_users return number;
    function find_users_by_filter(p_filter in nvarchar2, p_start in number, p_size in number,
                                        p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor;
    function count_users_by_filter(p_filter nvarchar2) return number;
    procedure edit_user(p_id in users.id%type, p_blocked in users.blocked%type, p_role_id in users.role_id%type);
end admin_package;

create or replace package body admin_package as

    function get_categories(p_start in number, p_size in number, p_sort in nvarchar2,
                                  p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from categories order by ' || p_sort || ' ' || p_direction ||
                   ') where rownum <= ' || p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_categories return number
        is
        v_count number;
    begin
        select nvl((select count(*) from categories), -1) into v_count from dual;
        return v_count;
    end;

    procedure add_category(p_name in categories.name%type)
        is
    begin
        insert into categories(name) values (p_name);
    end;

    procedure update_category(p_id in categories.id%type, p_name in categories.name%type)
        is
    begin
        update categories set name = p_name where id = p_id;
    end;

    procedure delete_category(p_id in categories.id%type)
        is
    begin
        delete from categories where id = p_id;
    end;

    function find_categories_by_filter(p_filter in nvarchar2, p_start in number,
                                             p_size in number, p_sort in nvarchar2,
                                             p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from categories' || ' where name like "%' || p_filter || '%"' ||
                   ' order by ' || p_sort || ' ' || p_direction || ') where rownum <= ' || p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_categories_by_filter(p_filter nvarchar2) return number
        is
        v_count number;
    begin
        select nvl((select count(*) from categories where name like '%' || p_filter || '%'), -1) into v_count from dual;
        return v_count;
    end;

    procedure add_dish(p_name in dishes.name%type, p_description in dishes.description%type,
                       p_image_url in dishes.image_url%type,
                       p_weight in dishes.weight%type, p_price in dishes.price%type, p_discount in dishes.discount%type,
                       p_category_id in dishes.category_id%type)
        is
    begin
        insert into dishes(name, description, image_url, weight, price, discount, category_id)
        values (p_name, p_description, p_image_url, p_weight, p_price, p_discount, p_category_id);
    end;

    procedure update_dish(p_id in dishes.id%type, p_name in dishes.name%type, p_description in dishes.description%type,
                          p_image_url in dishes.image_url%type,
                          p_weight in dishes.weight%type, p_price in dishes.price%type,
                          p_discount in dishes.discount%type,
                          p_category_id in dishes.category_id%type)
        is
    begin
        update dishes
        set name        = p_name,
            description = p_description,
            image_url   = p_image_url,
            weight      = p_weight,
            price       = p_price,
            discount    = p_discount,
            category_id = p_category_id
        where id = p_id;
    end;

    procedure delete_dish(p_id in dishes.id%type)
        is
    begin
        delete from dishes where id = p_id;
    end;

    function find_role_by_id(p_id in roles.id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from roles where id = p_id;
        return c_cursor;
    end;

    function get_roles(p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from roles order by ' || p_sort || ' ' || p_direction;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function find_user_by_id(p_id in users.id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from users where id = p_id;
        return c_cursor;
    end;

    function get_users(p_start in number, p_size in number, p_sort in nvarchar2,
                             p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from users order by ' || p_sort || ' ' || p_direction ||
                   ') where rownum <= ' ||
                   p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_users return number
        is
        v_count number;
    begin
        select nvl((select count(*) from users), -1) into v_count from dual;
        return v_count;
    end;

    function find_users_by_filter(p_filter in nvarchar2, p_start in number, p_size in number,
                                        p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from users' || ' where name like "%' || p_filter || '%"' || ' order by ' ||
                   p_sort || ' ' ||
                   p_direction || ') where rownum <= ' || p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_users_by_filter(p_filter nvarchar2) return number
        is
        v_count number;
    begin
        select nvl((select count(*) from users where name like '%' || p_filter || '%'), -1) into v_count from dual;
        return v_count;
    end;

    procedure edit_user(p_id in users.id%type, p_blocked in users.blocked%type, p_role_id in users.role_id%type)
        is
    begin
        update users set blocked = p_blocked, role_id = p_role_id where id = p_id;
    end;

end admin_package;