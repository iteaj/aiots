import {inject} from "vue";
import {CellContextKey} from "@/utils/ProvideKeys";

/**
 * 非Item表单控件的表格单元格注册mixin(UTable/UEditableTable可编辑列场景)
 * <p>表格渲染可编辑单元格时向插槽注入{@link CellContextKey}(含行record与列字段), 混入本mixin的控件
 * (UInput/USelect等非Item系)自动注册自身实例, 供页面自动化写值后触发已绑定事件;
 * 触发统一value语义: 业务@change/@select收(value, model); 选项型控件(MixinsOptions)覆写fire方法
 * 以携带选中项完整对象, 输入型控件model为空</p>
 */
export default {
    data() {
        return {
            cellContext: inject(CellContextKey, null),
        }
    },
    created() {
        if(this.cellContext) {
            this.cellContext.register(this);
        }
    },
    beforeUnmount() {
        if(this.cellContext) {
            this.cellContext.unregister(this);
        }
    },
    methods: {
        /**
         * 触发已绑定change事件(业务未绑定处理器返回false)
         */
        fireChange(value, model) {
            let onChange = this.$props['onChange'];
            if(!(onChange instanceof Function)) {
                onChange = this.$attrs['onChange'];
            }
            if(onChange instanceof Function) {
                onChange(value, model);
                return true;
            }
            return false;
        },
        /**
         * 触发已绑定select事件(业务未绑定处理器返回false)
         */
        fireSelect(value, model) {
            let onSelect = this.$props['onSelect'];
            if(!(onSelect instanceof Function)) {
                onSelect = this.$attrs['onSelect'];
            }
            if(onSelect instanceof Function) {
                onSelect(value, model);
                return true;
            }
            return false;
        },

        /**
         * 表单所属得功能上下文
         * @returns {TableContext|Object|*}
         */
        getFuncContext() {
          if(this.cellContext) {
              return this.cellContext.getTableContext();
          }
        }
    }
}
