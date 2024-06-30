CREATE DATABASE IF NOT EXISTS iot CACHEMODEL 'last_row' KEEP 365;
use iot;
DROP STABLE IF EXISTS iot_data;
create STABLE iot_data (
    collect_time timestamp,
    cid varchar(36),
    field_id bigint,
    field nchar(36),
    task_id bigint,
    address nchar(128),
    collect_value nchar(256),
    collect_status bool,
    reason nchar(128),
    collect_mode varchar(16),
    create_time timestamp
) tags (
    uid varchar(36),
    device_group_id bigint,
    product_code nchar(36),
    protocol_code nchar(36)
);