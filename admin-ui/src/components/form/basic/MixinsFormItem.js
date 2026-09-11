import {computed, defineComponent, inject, mergeProps, reactive} from "vue";
import {RowContextKey} from "@/utils/ProvideKeys";
import {emitPageEvent} from "@/utils/pageEvents";

export default defineComponent({
    props: ['name', 'label', 'labelCol', 'wrapperCol', 'colon', 'hasFeedback'
        , 'help', 'labelAlign', 'validateStatus', 'validateFirst', 'validateTrigger'
        , 'extra', 'autoLink', 'required', 'field', 'class', 'style'],
    data() {
        return {
            attrs: null,
            namePath: [],
            colConfig: inject(RowContextKey),
            /**
             * @type {FormContext}
             */
            formContext: inject('formContext'),
        }
    },
    created() {
        if(this.$attrs.span) { // 以span为主
            this.colConfig = {span: this.$attrs.span};
        }

        if(this.name) {
            if(!(this.name instanceof Array)) {
                this.namePath = [this.name];
                console.warn(`[name]必须是数组或者用[field]替代[name]]`)
            } else {
                this.namePath = this.name;
            }
        } else if(this.field){
            this.namePath = this.field.split('.');
        } else {
            return console.error(`表单组件未设置属性[field or name]`)
        }

        if(this.formContext && this.namePath) {
            let initModel = inject('initModel');
            let defaultValue = this.$attrs.defaultValue;
            initModel(this.namePath, this.getDefaultValue(defaultValue));
            // 注册字段组件实例(供页面自描述: 焦点视图/component_schema读取端实时提取 列名/类型/必填/禁用; 卸载时以null解除)
            // 键=声明字段名field(与模型/校验键一致); field可能为空(name数组声明), 回退namePath点路径兜底
            if(this.formContext['registerField'] instanceof Function) {
                this.formContext['registerField'](this.field || this.namePath.join('.'), this);
            }
        }
    },
    beforeUnmount() {
        // 字段卸载时解除注册(meta=null, 键与注册一致=field或namePath点路径兜底): 避免fieldMetas残留幽灵字段
        if(this.formContext && this.formContext['registerField'] instanceof Function) {
            this.formContext['registerField'](this.field || this.namePath.join('.'), null);
        }
    },
    methods: {
        getDefaultValue(defaultValue) {
            return defaultValue != undefined ? defaultValue : null;
        },
        onFocusProxy(event) {
            // 先执行原有的 onFocus(若有)
            const originalOnFocus = this.$attrs.onFocus;
            if(originalOnFocus instanceof Function) {
                originalOnFocus(event);
            }
            // 发布焦点事件
            emitPageEvent('field:focus', this);
        },
        onBlurProxy(event) {
            // 先执行原有的 onBlur(若有)
            const originalOnBlur = this.$attrs.onBlur;
            if(originalOnBlur instanceof Function) {
                originalOnBlur(event);
            }

            // 发布失焦事件
            emitPageEvent('field:blur', this);
        },
        getFormItemProps() {
            return {...this.$props, name: this.namePath, style: null, class: null, onChange: null, onSelect: null}
        },
        getColProps() {
            return {...this.$attrs, ...this.colConfig};
        },
        getFormAttrs(options) {
            if(this.attrs) {
                let assign = Object.assign(this.attrs, this.$attrs, options || {});
                return assign;
            }

            this.attrs = this.$attrs;
            if(this.formContext && !this.attrs['onUpdate:value']) {
                let value = computed(() => {
                    return this.formContext.getFieldValue(this.namePath)
                });
                this.attrs = {...this.$attrs, value: value, 'onUpdate:value': (val) => {
                        this.formContext.setFieldValue(this.namePath, val);
                    }, ...options, class: this.$props.class, style: this.$props.style}
            }
            // 注入字段焦点事件代理方法(供页面操作引擎监听记录当前聚焦字段)
            // this.attrs = {
            //     ...this.attrs,
            //     onFocus: this.onFocusProxy,
            //     onBlur: this.onBlurProxy
            // };
            if("isOption" in this) {
                return this.proxyOptionsEvent(this.attrs);
            } else {
                return this.attrs;
            }
        },
        getCheckedAttrs() {
            if(this.attrs) {
                return this.attrs;
            }

            this.attrs = this.$attrs;
            let value = computed(() => this.formContext.getFieldValue(this.namePath));
            if(this.formContext && !this.attrs['onUpdate:checked']) {
                this.attrs = reactive(mergeProps(this.$attrs, {
                    checked: value, 'onUpdate:checked': (val) => {
                        this.formContext.setFieldValue(this.namePath, val);
                    }
                }));
            }
            // 注入字段焦点事件代理方法(供页面操作引擎监听记录当前聚焦字段)
            // this.attrs = {
            //     ...this.attrs,
            //     onFocus: this.onFocusProxy,
            //     onBlur: this.onBlurProxy
            // };

            if("isOption" in this) {
                return this.proxyOptionsEvent(this.attrs);
            } else {
                return this.attrs;
            }
        },
        getTransferAttrs(options) {
            if(this.attrs) {
                return mergeProps(this.attrs, this.$attrs, options);
            }
            this.attrs = this.$attrs;
            if(this.formContext && !this.attrs['onUpdate:targetKeys']) {
                let value = computed(() => {
                    return this.formContext.getFieldValue(this.namePath) || []
                });
                this.attrs = {...this.$attrs, targetKeys: value, 'onUpdate:targetKeys': (val) => {
                        this.formContext.setFieldValue(this.namePath, val);
                    }, ...options, class: this.$props.class, style: this.$props.style}
            }
            // 注入字段焦点事件代理方法(供页面操作引擎监听记录当前聚焦字段)
            // this.attrs = {
            //     ...this.attrs,
            //     onFocus: this.onFocusProxy,
            //     onBlur: this.onBlurProxy
            // };

            if("isOption" in this) {
                return this.proxyOptionsEvent(this.attrs);
            } else {
                return this.attrs;
            }
        },
        /**
         * 表单所属FuncContext
         * @returns {*}
         */
        getFuncContext() {
            if(this.formContext) {
                return this.formContext.getParentContext();
            }
        }
    }
})
