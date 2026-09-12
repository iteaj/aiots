export class FuncMeta {
    /**
     * @param key {String} 唯一键
     * @param type {String} 类型
     * @param label {String} 显示标签
     * @param meta {Object} 元数据
     */
    constructor(key, label, type, meta = {}) {
        this.key = key;
        this.type = type;
        this.label = label;
        this.meta = meta;
    }

    getKey() {
        return this.key;
    }

    /**
     * form: 表单类型
     * func: 功能类型(可点击按钮)
     * @return {String}
     */
    getType() {
        return this.type;
    }

    getLabel() {
        return this.label;
    }

    getMeta() {
        return this.meta;
    }

    trigger(value) {
        this.getMeta().trigger(value);
    }
}