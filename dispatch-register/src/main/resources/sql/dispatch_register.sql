
CREATE TABLE `dispatch_register`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `gmt_create` TIMESTAMP NOT NULL DEFAULT now()  COMMENT '创建时间',
  `gmt_modified` TIMESTAMP NOT NULL DEFAULT now() ON UPDATE now() COMMENT '修改时间',
  `handler_group` VARCHAR(32) NOT NULL COMMENT '处理群组',
  `hostname` VARCHAR(128) NOT NULL COMMENT '机器名',
  `register_time` BIGINT(20) NOT NULL COMMENT '注册时间',
  `nodes` VARCHAR(512) NOT NULL COMMENT '节点集合字符串',
  `register_version` VARCHAR (32) NOT NULL COMMENT '注册集群版本',
  `version` BIGINT(20) DEFAULT 0 NOT NULL COMMENT '机器版本',
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

ALTER TABLE `dispatch_register` ADD UNIQUE KEY uiq_dr_handlergroup_hostname(handler_group,hostname);
ALTER TABLE `dispatch_register` ADD INDEX idx_dr_handlergroup(handler_group);
ALTER TABLE `dispatch_register` ADD INDEX idx_dr_registerversion(register_version);

