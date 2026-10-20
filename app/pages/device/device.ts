// 设备模块类型声明与接口地址

// ====== 接口地址 ======

export const DeviceApi = {
	/** 设备详情（含产品信息） */
	DETAIL: "/app/device/detail",
	/** 设备实时数据 */
	REALTIME: "/app/device/realtime",
	/** 设备指令集 */
	COMMANDS: "/app/device/commands",
	/** 告警记录分页 */
	WARN_PAGE: "/app/warnRecord/page",
	/** 指令日志分页 */
	CMD_LOG_PAGE: "/app/device/cmdLog/page",
	/** 采集数据分页 */
	COLLECT_PAGE: "/app/collectData/page",
	/** 执行指令 */
	COMMAND_INVOKE: "/app/device/command/invoke/update",
};

// ====== 类型定义 ======

/**
 * 设备详情（含产品信息）
 */
export type DeviceDetail = {
	/** 设备ID */
	id: number;
	/** 设备UID */
	uid: string;
	/** 设备名称 */
	name: string;
	/** 设备编号 */
	deviceSn: string;
	/** 设备状态 */
	status: string;
	/** 产品ID */
	productId: number;
	/** 产品名称 */
	productName: string;
	/** 产品编码 */
	productCode: string;
	/** 协议编码 */
	protocolCode: string;
	/** 设备类型 */
	deviceType: string;
	/** 设备类型名称 */
	deviceTypeName: string;
	/** 网关名称 */
	gatewayName: string;
	/** 分组名称 */
	deviceGroupName: string;
	/** 父设备编号 */
	parentDeviceSn: string;
	/** ip */
	ip: string;
	/** 地址 */
	address: string;
	/** 创建时间 */
	createTime: string;
};

/**
 * 实时数据项
 */
export type RealtimeItem = {
	field: string;
	name: string;
	value: string;
	label: string;
	unit: string;
	collectTime: string;
};

/**
 * 设备指令项
 */
export type CommandItem = {
	/** 指令编码 */
	commandValue: string;
	/** 指令名称 */
	apiName: string;
	/** 指令类型 */
	apiType: string;
	/** 属性字段 */
	attrField: string;
	/** 属性值 */
	attrValue: string;
};

/**
 * 告警记录
 */
export type WarnItem = {
	content: string;
	warnTime: string;
	deviceSn: string;
	gradeName: string;
};

/**
 * 指令日志
 */
export type CmdLogItem = {
	id: number;
	uid: string;
	deviceName: string;
	status: string;
	resultMsg: string;
	cmdType: string;
	method: string;
	operator: string;
	createTime: string;
};

/**
 * 采集数据项
 */
export type CollectDataItem = {
	fieldName: string;
	value: string;
	unit: string;
	collectTime: string;
};
