import {defineAsyncComponent, defineComponent, h, mergeProps, ref, resolveComponent} from "vue";
import MixinsForm from "./MixinsForm";
import MixinsOptions from "@/components/form/basic/MixinsOptions";
import CoreConsts from "@/components/CoreConsts";
import {
    CheckboxGroup,
    Input,
    InputGroup,
    InputNumber,
    InputPassword,
    RadioGroup,
    Rate,
    Slider,
    Switch,
    TextArea
} from "antdv-next";

const UEditor = defineAsyncComponent(() => import('@/components/form/editor/editor.jsx'))
const UMarkdown = defineAsyncComponent(() => import('@/components/form/editor/markdown.jsx'))
const UScriptEditor = defineAsyncComponent(() => import('@/components/code/AceScriptEditor.vue'))
export const UScript = defineComponent({
    name: 'UScript',
    mixins: [MixinsForm],
    components: {UScriptEditor},
    render() {
        // 外层定高容器(AceScriptEditor内部height:100%依赖父级高度); value解包为纯字符串匹配其String声明
        return <div style={this.$props.style || 'height: 300px'}>
            <UScriptEditor {...this.$attrs} update:value={this.$attrs['onUpdate:value']} onChange={this.onChange} style="height: 100%" v-slots={this.$slots}/>
        </div>
    }
})
export const UCheckbox = defineComponent({
    name: 'UCheckbox',
    mixins: [MixinsOptions],
    setup(props, {slots}) {
        let defaultSlots
        if(props.options instanceof Array || props.dict || props.url) {
            defaultSlots = (attrs) => h(resolveComponent('a-checkbox-group'), attrs, slots.default)
        } else {
            defaultSlots = (attrs) => h(resolveComponent('a-checkbox'), attrs, slots.default)
        }

        return {defaultSlots}
    },
    render() {
        return this.defaultSlots(this.$attrs)
    }

})

export const USelect = defineComponent({
    name: 'USelect',
    mixins: [MixinsOptions],
    setup(props, {attrs}) {
        let fieldNames = attrs['fieldNames'];
        if(fieldNames == null) {
            fieldNames = {label: props.labelField, value: props.valueField, children: CoreConsts.Options_ChildrenField};
        }

        return {fieldNames};
    },
    render() {
        return <ASelect {...this.mergeAttrs()} options={this.dataSource} fieldNames={this.fieldNames} v-slots={this.$slots} />
    }
})

export const UCascader = defineComponent({
    name: 'UCascader',
    mixins: [MixinsOptions],
    render() {
        return <a-cascader {...this.mergeAttrs()} options={this.dataSource} v-slots={this.$slots}></a-cascader>
    }

})
export const UAutoComplete = defineComponent({
    name: 'UAutoComplete',
    mixins: [MixinsOptions],
    render() {
        return <a-auto-complete {...this.mergeAttrs()} dataSource={this.dataSource} v-slots={this.$slots}></a-auto-complete>
    }
})

export const URadio = defineComponent({
    name: 'URadio',
    mixins: [MixinsOptions],
    render() {
        let slots = this.$slots.default ? () => {
            return this.$slots.default()
        } : () => {
            return h(resolveComponent('a-radio-group'), this.mergeAttrs({options: this.dataSource}))
        }

        return slots()
    }
})
export const UMentions = defineComponent({
    name: 'UMentions',
    mixins: [MixinsOptions],
    props: {
      prefix: {default: '@'}
    },
    render() {
        return <AMentions {...this.mergeAttrs()} prefix={this.prefix} options={this.dataSource} v-slots={this.$slots} />
    },
    methods: {
        fireSelect(value, model) {
            if(value) {
                if(this.$props['onSelect'] instanceof Function) {
                    let pre = value.at(0);
                    if(pre == this.prefix) {
                        value = value.substring(1);
                    }
                    let selectedModel = this.getSelectedModel(value);
                    this.onSelectProxy(selectedModel, this.prefix);
                    return true;
                }
            }

            return false;
        },
        // 覆写onSelect事件
        onSelectProxy(value, e, v) {
            let onSelect = this.$props['onSelect'];
            if(onSelect instanceof Function) {
                onSelect(value, e, v);
            }
        },
    }
})
export const USegmented = defineComponent({
    name: 'USegmented',
    mixins: [MixinsOptions],
    render() {
        return <ASegmented {...this.mergeAttrs()} options={this.dataSource} v-slots={this.$slots} />
    }
})
export const UTreeSelect = defineComponent({
    name: 'UTreeSelect',
    mixins: [MixinsOptions],
    setup(props, {attrs}) {

        /**
         * 使用方法主要解决TreeSelect组件不能触发同步问题
         * @return {{children: string, title: *, value: *}}
         */
        let getFieldNames = () => {
            let fieldNames = attrs['fieldNames'];
            if(fieldNames == null) {
                fieldNames = {label: props.labelField, value: props.valueField, children: CoreConsts.Options_ChildrenField};
            }

            return fieldNames;
        }

        return {getFieldNames}
    },
    render() {
        return <ATreeSelect {...this.mergeAttrs()} fieldNames={this.getFieldNames()} treeData={this.dataSource} v-slots={this.$slots} />
    }

})

const typeMaps = {date: 'a-date-picker', month: 'a-month-picker'
    , range: 'a-range-picker', week: 'a-week-picker', time: 'a-time-picker'}
export const UDateTime = defineComponent({
    name: 'UDateTime',
    props: ['picker'],
    mixins: [],
    render() {
        let tag = typeMaps[this.$props['picker']] || 'a-date-picker';
        return h(resolveComponent(tag), this.getFormAttrs(), this.$slots)
    }
})
export const UDatePicker = defineComponent({
    name: 'UDatePicker',
    render() {
        return <ADatePicker {...this.$attrs} v-slots={this.$slots}/>
    }
})
export const UMonthPicker = defineComponent({
    name: 'UMonthPicker',
    render() {
        return <AMonthPicker {...this.$attrs} v-slots={this.$slots}/>
    }
})
export const UWeekPicker = defineComponent({
    name: 'UWeekPicker',
    render() {
        return <AWeekPicker {...this.$attrs} v-slots={this.$slots}/>
    }
})
export const URangePicker = defineComponent({
    name: 'URangePicker',
    render() {
        return <ARangePicker {...this.$attrs} v-slots={this.$slots}/>
    }
})
export const UTimePicker = defineComponent({
    name: 'UTimePicker',
    render() {
        return <ATimePicker {...this.$attrs} v-slots={this.$slots}/>
    }
})
export const UTransfer = defineComponent({
    name: 'UTransfer',
    mixins: [MixinsOptions],
    setup(props, {attrs}) {
        let fieldNames = attrs['fieldNames'];
        if(fieldNames == null) {
            fieldNames = {label: props.labelField, value: props.valueField, children: CoreConsts.Options_ChildrenField};
        }
        let selectedKeys = ref([]);

        let render = attrs.render || (item => item[fieldNames.label]);
        let rowKey = attrs.rowKey || (record => record[fieldNames.value] + '');

        let attrOptions = {rowKey, render}
        return {fieldNames, selectedKeys, attrOptions};
    },
    render() {
        return <ATransfer {...mergeProps(this.$attrs, this.attrOptions)}
                          v-model={[this.selectedKeys, 'selectedKeys', ["modifier"]]}
                          dataSource={this.dataSource} v-slots={this.$slots}/>
    }
})
const formComponents = {USelect, UCheckbox, UCascader, UAutoComplete, UTransfer, USegmented
    , URadio, UDateTime, UTreeSelect, UDatePicker, UMonthPicker, URangePicker, UTimePicker, UWeekPicker, UMentions}

/**
 * 非Item表单控件包装(混入MixinsForm): 支持表格可编辑列注册与事件触发
 * <p>属性/事件/slots全透传且行为与antdv原生一致; 业务ref调用的实例方法(focus/blur)透传内部组件</p>
 */
function formCell(name, component) {
    return defineComponent({
        name, mixins: [MixinsForm], inheritAttrs: false,
        setup(props, {attrs, slots, expose}) {
            const innerRef = ref(null);
            expose(['focus', 'blur'].reduce((acc, m) => {
                acc[m] = (...args) => innerRef.value && innerRef.value[m] && innerRef.value[m](...args);
                return acc;
            }, {}));
            return () => h(component, {...attrs, ref: innerRef}, slots)
        }
    })
}

export default {
    install(app) {
        Object.values(formComponents).forEach(component => {
            app.component(component.name, component);
        })

        app.component("UInput", formCell("UInput", Input));
        app.component("UTextarea", formCell("UTextarea", TextArea));
        app.component("UInputGroup", formCell("UInputGroup", InputGroup));
        app.component("UInputNumber", formCell("UInputNumber", InputNumber));
        app.component("UInputPassword", formCell("UInputPassword", InputPassword));
        app.component("USwitch", formCell("USwitch", Switch));
        app.component("URate", formCell("URate", Rate));
        app.component("USlider", formCell("USlider", Slider));
        app.component("URadioGroup", formCell("URadioGroup", RadioGroup));
        app.component("UCheckboxGroup", formCell("UCheckboxGroup", CheckboxGroup));
        app.component("UScript", UScript);
        app.component("UEditor", UEditor);
        app.component("UMarkdown", UMarkdown);
    }
}
