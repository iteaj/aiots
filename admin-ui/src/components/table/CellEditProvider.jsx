import {defineComponent, provide} from "vue";
import {CellContextKey} from "@/utils/ProvideKeys";

/**
 * 表格可编辑单元格上下文(UTable/UEditableTable渲染可编辑列插槽时包裹)
 * <p>向插槽后代provide单元格字段信息(CellContextKey), 混入MixinsForm的控件(UInput/USelect等)
 * created自动register自身实例; 实例按[行对象:列字段]登记到TableContext, 供页面自动化写值后触发已绑定事件;
 * 单元格内通常单控件, 业务v-if切换控件时旧实例先卸载注销、新实例后注册覆盖</p>
 */
export default defineComponent({
    name: 'CellEditProvider',
    inheritAttrs: false,
    props: {
        tableContext: {type: Object, required: true}, // TableContext(控件实例登记目标)
        record: {type: Object, required: true},       // 当前行数据对象(与dataSource行同引用)
        column: {type: Object, required: true},       // 列定义(字段名取dataIndex点路径/field)
    },
    setup(props, {slots}) {
        // 字段名口径与动作引擎set_value列匹配一致(dataIndex数组点路径优先)
        const fieldOf = () => Array.isArray(props.column.dataIndex) ? props.column.dataIndex.join('.') : (props.column.dataIndex);
        let instance = null;
        const cellContext = {
            register(comp) {
                instance = comp;
                props.tableContext.registerCell(props.record, fieldOf(), comp);
            },
            unregister() {
                let last = instance;
                instance = null;
                if(last) {
                    props.tableContext.unregisterCell(props.record, fieldOf(), last);
                }
            },
            getColumn()  {
              return props.column;
            },
            getTableContext() {
              return props.tableContext;
            },
            getInstance() {
                return instance;
            }
        };
        provide(CellContextKey, cellContext);
        return () => (slots.default && slots.default()) || null;
    }
})
