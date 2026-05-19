# Core模块接口明细

## 模块概述
- **模块名称**：Core模块
- **功能范围**：系统基础管理功能
- **主要业务**：用户认证、权限管理、系统配置、组织机构管理等
- **表前缀**：sys_
- **模块rest api前缀**：/core
- **模块接口所在包**：com.iteaj.iboot.module.core.controller

## 接口列表

| 控制器 | 功能简介 | 实体类 | 数据库表名 |
|--------|----------|--------|------------|
| AdminCenterController | 管理员中心管理 | Admin | sys_admin |
| AdminController | 管理员账号管理 | Admin | sys_admin |
| ConfigController | 系统配置管理 | Config | sys_config |
| CoreStatisticsController | 系统统计 | 多表 | 多表统计 |
| DictDataController | 字典数据管理 | DictData | sys_dict_data |
| DictTypeController | 字典类型管理 | DictType | sys_dict_type |
| LoginController | 用户登录认证 | Admin | sys_admin |
| MenuController | 菜单权限管理 | Menu | sys_menu |
| NotifyController | 通知管理 | Notify | sys_notify |
| NotifyUserController | 通知用户关联管理 | NotifyUser | sys_notify_user |
| OnlineUserController | 在线用户管理 | OnlineUser | sys_online_user |
| OperaLogController | 操作日志管理 | AccessLog | sys_access_log |
| OrgController | 组织机构管理 | Org | sys_org |
| PostController | 职位管理 | Post | sys_post |
| RegionController | 区域管理 | Region | sys_region |
| RoleController | 角色权限管理 | Role | sys_role |
| UploadController | 文件上传管理 | - | - |

## 使用说明

此文件用于AI快速定位Core模块信息，包含：
1. 模块名称和功能范围
2. 控制器及其功能简介
3. 对应的实体类
4. 数据库表名