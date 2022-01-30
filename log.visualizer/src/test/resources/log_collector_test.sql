create table traced_log
(
    id bigint not null auto_increment,
    trace_id varchar(100) not null comment '추적 ID',
    tag_id varchar(20) not null comment '서비스명 또는 서비스 ID',
    log_type varchar(5) not null comment '로그 레벨',
    log_data text comment '로그 내용',
    logging_time timestamp(6) not null comment '로깅 일시',
    logging_date date not null comment '로깅 일자',
    create_at timestamp not null default current_timestamp comment '등록 일시',
    primary key(id)
);

-- insert
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t1', 'service_x', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:21.264322000', '2021-12-14', '2021-12-15 03:06:56');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t1', 'service_x', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:22.264322000', '2021-12-14', '2021-12-15 04:14:24');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t2', 'service_x', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:22.264322000', '2021-12-14', '2021-12-15 04:15:15');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t2', 'service_y', 'INFO', 'test LOG DATA', '2021-12-14 02:14:23.264322000', '2021-12-14', '2021-12-15 04:17:08');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t2', 'service_y', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:23.564322000', '2021-12-14', '2021-12-15 04:34:11');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t2', 'service_x', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:24.864322000', '2021-12-14', '2021-12-15 05:25:29');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t3', 'service_y', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:25.264322000', '2021-12-14', '2021-12-15 06:23:49');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t3', 'service_z', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:25.364322000', '2021-12-14', '2021-12-15 08:16:08');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t3', 'service_z', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:26.264322000', '2021-12-14', '2021-12-15 08:21:42');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t3', 'service_x', 'INFO', 'test LOG DATA', '2021-12-14 02:14:27.264322000', '2021-12-14', '2021-12-15 08:49:12');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t3', 'service_x', 'INFO', 'test LOG DATA', '2021-12-14 02:14:27.394322000', '2021-12-14', '2021-12-15 09:12:57');
INSERT INTO traced_log
(trace_id, tag_id, log_type, log_data, logging_time, logging_date, create_at)
VALUES('t3', 'service_y', 'ERROR', 'test LOG DATA', '2021-12-14 02:14:30.264322000', '2021-12-14', '2021-12-15 09:13:46');
