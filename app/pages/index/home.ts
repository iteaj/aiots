// 首页接口地址与类型定义

export const HomeApi = {
	/** 首页统计 */
	STATISTICS: "/app/index/statistics",
};

/** 首页统计响应 */
export type HomeStatistics = {
	/** 总设备数 */
	deviceTotal: number;
	/** 在线设备数 */
	deviceOnline: number;
	/** 离线设备数 */
	deviceOffline: number;
	/** 今日告警数 */
	alarmToday: number;
	/** 产品总数 */
	productTotal: number;
	/** 最新告警列表 */
	latestWarns?: WarnItem[];
	/** 各产品设备统计 */
	productStats?: ProductStatItem[];
};

/** 告警项 */
export type WarnItem = {
	content: string;
	warnTime: string;
	deviceSn: string;
	gradeName: string;
};

/** 产品设备统计 */
export type ProductStatItem = {
	name: string;
	value: number;
};
