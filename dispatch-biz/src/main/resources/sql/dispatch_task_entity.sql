
CREATE TABLE `dispatch_task_entity`(
  `id` BIGINT(20) NOT NULL COMMENT 'id',
  `gmt_create` TIMESTAMP NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '创建时间',
  `gmt_modified` TIMESTAMP NOT NULL DEFAULT now() ON UPDATE now() COMMENT '修改时间',
  `biz_unique_id` VARCHAR(32) NOT NULL COMMENT '业务唯一id',
  `parameter` VARCHAR(512) NOT NULL COMMENT '参数',
  `trace_id` VARCHAR(32) NOT NULL COMMENT '跟踪id',
  `handler_group` VARCHAR(32) NOT NULL COMMENT '处理群组',
  `handler` VARCHAR(32) NOT NULL COMMENT '处理器名称',
  `node` INT(11) NOT NULL COMMENT '任务节点',
  `loadbalance` INT(11) NOT NULL COMMENT '负载均衡值',
  `status` SMALLINT(4) NOT NULL COMMENT '状态',
  `next_exec_time` BIGINT(20) NOT NULL COMMENT '下一次执行时间',
  `fail_strategy` SMALLINT(4) DEFAULT 1 COMMENT '失败处理策略',
  `retry_time` SMALLINT DEFAULT 0 COMMENT '重试次数',
  `remark` VARCHAR(512) COMMENT '备注',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
ALTER TABLE `dispatch_task_entity` ADD UNIQUE KEY uiq_dte_bizid(biz_unique_id);
ALTER TABLE `dispatch_task_entity` ADD INDEX idx_dte_handlergroup_id(handler_group,id);
ALTER TABLE `dispatch_task_entity` ADD INDEX idx_dte_handlergroup_bizid(handler_group,biz_unique_id);
ALTER TABLE `dispatch_task_entity` ADD INDEX idx_dte_status(status);
ALTER TABLE `dispatch_task_entity` ADD INDEX idx_dte_handlergroup_node_status_nxts(handler_group,node,status,next_exec_time);

ALTER TABLE `dispatch_task_entity` ADD UNIQUE KEY uiq_dte_traceid(trace_id);
