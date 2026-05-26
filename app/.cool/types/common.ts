/**
 * 通用分页类型
 */
export type Page<T> = {
	total: number;
	records: T[];
};
