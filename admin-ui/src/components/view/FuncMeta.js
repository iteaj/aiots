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

    /**
     * 按类型取内容: form=表单控件当前值(受控组件v-model:value, 取$props.value优先, 兜底$attrs.value); func=功能按钮标题(FuncConfig.title()最新, 兜底注册快照label)
     * @return {*} 表单值 或 按钮标题
     */
    getContext() {
        const meta = this.getMeta();
        if(this.type === 'form') {
            if(!meta) {
                return null;
            }
            const props = meta.$props || {};
            const attrs = meta.$attrs || {};
            return props.value !== undefined ? props.value : attrs.value;
        }
        if(this.type === 'func') {
            return meta && meta.title instanceof Function ? meta.title() : this.label;
        }
        return null;
    }
    
    trigger(value) {
        return this.getMeta().trigger(value);
    }
}