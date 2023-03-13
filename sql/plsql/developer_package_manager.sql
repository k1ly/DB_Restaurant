alter session set NLS_LANGUAGE = AMERICAN;

create or replace package manager_package as
    procedure update_order(p_id in orders.id%type, p_status_id in orders.status_id%type,
                           p_manager_id in orders.manager_id%type);
    function find_orders_by_status(p_status_id in orders.status_id%type, p_start in number,
                                         p_size in number,
                                         p_sort in nvarchar2,
                                         p_direction in varchar2) return sys_refcursor;
    function count_orders_by_status(p_status_id orders.status_id%type) return number;
end manager_package;

create or replace package body manager_package as

    procedure update_order(p_id in orders.id%type, p_status_id in orders.status_id%type,
                           p_manager_id in orders.manager_id%type)
        is
        v_status_name   statuses.name%type;
        q_delivery_date varchar2(100);
        q_manager_id    varchar2(100);
        q_query         varchar2(300);
    begin
        select name into v_status_name from statuses where id = p_status_id;
        q_delivery_date :=
                case when v_status_name = 'FINISHED' then ' delivery_date = ' || systimestamp || ',' else '' end;
        q_manager_id := nvl(concat(', manager_id = ', p_manager_id), '');
        q_query := 'update orders set' || q_delivery_date || ' status_id = ' || p_status_id || q_manager_id ||
                   ' where id = ' || p_id;
        execute immediate q_query;
    end;

    function find_orders_by_status(p_status_id in orders.status_id%type, p_start in number,
                                         p_size in number,
                                         p_sort in nvarchar2,
                                         p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from orders where status_id = ' || p_status_id || ' order by ' || p_sort ||
                   ' ' || p_direction || ') where rownum <= ' || p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_orders_by_status(p_status_id orders.status_id%type) return number
        is
        v_count number;
    begin
        select nvl((select count(*) from orders where status_id = p_status_id), -1) into v_count from dual;
        return v_count;
    end;

end manager_package;