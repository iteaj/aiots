import {defineComponent, inject, mergeProps, ref, watch} from "vue";
import {useStore} from "vuex";
import {MetaConst} from "@/utils/MetaUtils";
import CoreConsts from "@/components/CoreConsts";
import SysUtils from "@/utils/SysUtils";
import MixinsForm from "./MixinsForm";
import {emitPageEvent} from "@/utils/pageEvents";
import {FuncContextKey} from "@/utils/ProvideKeys";

export default defineComponent({
    mixins: [MixinsForm],
    props: {
        url: String,
        dict: String,
        options: Array,
        onSelect: null,
        onLoaded: {default: () => { return () => null}}, // 数据加载完成
        labelField: {default: CoreConsts.Options_LabelField},
        valueField: {default: CoreConsts.Options_ValueField},
    },
    data() {
        return {
            isOption: true,
            dataSource: [],
            valueModelMap: {},
        }
    },
    created() {
        let valueField = this.$attrs.fieldNames ? this.$attrs.fieldNames.value || this.$attrs.fieldNames.key : this.valueField;
        let labelField = this.$attrs.fieldNames ? this.$attrs.fieldNames.label || this.$attrs.fieldNames.title : this.labelField;
        if(!this.options) {
            if(this.dict) {
                let {options, valueModelMap} = this.$store.getters['sys/getOptionsByDictType'](this.dict, labelField, valueField, this.onLoaded);
                this.dataSource = ref(options); this.valueModelMap = valueModelMap;

                // 更新父组件options的值
                this.$emit("update:source", this.valueModelMap)
            } else if(this.url) {
                // let url = this.url;
                watch(() => this.url, (newUrl) => {
                    let {options, valueModelMap} = this.$store.getters['sys/getOptionsByUrl'](newUrl, labelField
                        , valueField, MetaConst.DefaultLabelField, MetaConst.DefaultValueField, this.onLoaded);
                    this.dataSource = ref(options); this.valueModelMap = valueModelMap;

                    // 更新父组件options的值
                    this.$emit("update:source", this.valueModelMap)
                }, {immediate: true})
            }

        }

        watch(() => this.options, (newValue) => {
            if(newValue instanceof Array) {
                this.dataSource = this.options;
                if(labelField && valueField) {
                    if(newValue.length > 0) {
                        SysUtils.resolverOptions(this.options, labelField, valueField
                            , MetaConst.DefaultLabelField, MetaConst.DefaultValueField, {}, this.valueModelMap);
                        this.$emit("update:source", this.valueModelMap);
                    } else {
                        this.$emit("update:source", this.valueModelMap = {});
                    }
                }
            }
        }, {immediate: true})
    },
    methods: {
        getDataSource() {
            return this.dataSource;
        },
        /**
         * 返回当前选中项对应的对象
         */
        getSelectedModel(value) {
            if(value instanceof Array) {
                return value.map(item => this.valueModelMap[item]);
            }

            return this.valueModelMap[value];
        },

        /**
         * 合并属性
         * @return {Data}
         */
        mergeAttrs() {
            return mergeProps(this.$attrs, {
                onChange: this.onChangeProxy,
                onSelect: this.onSelectProxy,
                // onFocus: this.onFocusProxy,
                // onBlur: this.onBlurProxy
            });
        },

        onChangeProxy(value, e, v) {
            let onChange = this.$props['onChange'];
            if(onChange instanceof Function) {
                let model = this.getSelectedModel(value);
                onChange(value, model, e, v);
            }
        },

        onSelectProxy(value, e, v) {
            let onSelect = this.$props['onSelect'];
            if(onSelect instanceof Function) {
                let model = this.getSelectedModel(value);
                onSelect(value, model, e, v);
            }
        },

        onFocusProxy(event) {
            // 先执行原有的 onFocus(若有)
            const originalOnFocus = this.$attrs.onFocus;
            if(originalOnFocus instanceof Function) {
                originalOnFocus(event);
            }

            // 发布焦点事件(独立选项控件无FuncContextKey时静默跳过)
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

        /**
         * 覆写MixinsForm: 选项型控件事件携带选中项完整对象(代理解析后回调)
         */
        fireChange(value, model) {
            if(this.$props['onChange'] instanceof Function) {
                this.onChangeProxy(value);
                return true;
            }
            return false;
        },
        fireSelect(value, model) {
            if(this.$props['onSelect'] instanceof Function) {
                this.onSelectProxy(value);
                return true;
            }
            return false;
        }
    }
})
