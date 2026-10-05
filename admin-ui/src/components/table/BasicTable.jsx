import {useStore} from "vuex";
import dayjs from "dayjs";
import {computed, defineComponent, inject, onMounted, onUnmounted, provide, reactive, ref, watch} from "vue";
import {MetaConst} from "@/utils/MetaUtils";
import {FuncContextKey, LinkViewContextKey, ViewContextKey} from "@/utils/ProvideKeys";
import {TableContext} from "@/components/view/Context";
import CoreConsts from "@/components/CoreConsts";
import TableFuncHeader from "@/components/table/TableFuncHeader.vue";
import CellEditProvider from "@/components/table/CellEditProvider";
const onLoaded = (data) => null;

function initSlot(column, slots) {
    let slotName = "";
    if (column.dataIndex instanceof Array) {
        slotName = column.dataIndex.join("_");
    } else if (column.dataIndex instanceof String) {
        slotName = column.dataIndex;
    } else {
        slotName = column.field.split('.').join("_");
    }

    if (slots['c_' + slotName]) {
        column['__slot'] = slots['c_' + slotName]
    } else if (slots[slotName]) {
        column['__slot'] = slots[slotName]
    }
}

function initOptionsLabel(column) {

    let labelField = column.labelField || MetaConst.DefaultLabelField;
    let valueField = column.valueField || MetaConst.DefaultValueField;

    if(column.options instanceof Array) {
        column['__valueLabelMap'] = {}
        column.options.forEach(item => {
            let label = item[labelField];
            let value = item[valueField];

            column['__valueLabelMap'][value] = label;
        })
    } else if(column.dict){
        useStore().getters['sys/getOptionsByDictType'](column.dict, labelField, valueField, column.onLoaded || onLoaded);
        let valueLabelMap = useStore().getters['sys/getValueLabelMap'](column.dict);
        column['__valueLabelMap'] = valueLabelMap
    } else if(column.url) {
        useStore().getters['sys/getOptionsByUrl'](column.url, labelField
            , valueField, MetaConst.DefaultLabelField, MetaConst.DefaultValueField, column.onLoaded || onLoaded);
        let valueLabelMap = useStore().getters['sys/getValueLabelMap'](column.url);
        column['__valueLabelMap'] = valueLabelMap
    }
}

function initColumnFormatterSlot(column) {
    initOptionsLabel(column);

    let formatter = column.formatter;
    if(formatter instanceof Function) {
        // 对formatter创建代理, 新增返回label值
        column.formatter = ({text, record, column}) => {
            let label = column['__valueLabelMap'][text];
            return formatter({text, record, column, label})
        }
    } else {
        column.formatter = ({text, record, column}) => {
            return column['__valueLabelMap'][text] || '';
        }
    }

    let proxySlot = column['__slot']
    if(proxySlot) {
        column['__slot'] = (args) => {
            args['value'] = column.formatter(args);
            return proxySlot(args);
        }
    } else {
        column['__slot'] = column.formatter;
    }

}

const typeFormatMaps = {datetime: 'YYYY-MM-DD HH:mm:ss', date: 'YYYY-MM-DD', month: 'MM', week: 'E', time: 'HH:mm:ss'}
function initDatetimeColumnSlot(column) {
    let formatter = column.formatter;
    if(!(formatter instanceof Function)) {
        column.formatter = ({text, record, column}) => {
            try {
                if (text) {
                    let picker = column.picker || 'datetime';
                    return dayjs(text).format(column.format || typeFormatMaps[picker]);
                } else {
                    return '';
                }
            } catch (e) {
                console.error(e);
                return ''
            }
        }
    }

    let proxySlot = column['__slot']
    if(proxySlot) {
        column['__slot'] = (args) => {
            args['value'] = column.formatter(args);
            return proxySlot(args);
        }
    } else {
        column['__slot'] = column.formatter;
    }
}
function initSortableColumn(column, config) {
    column['__slot'] = (args) => {
        return <span class="iv-table-drag color-text-tertiary" draggable={true} onDragstart={(e) => config.onHandleDragStart(args.record, e)}>::</span>
    }
}
function initColumn(column, slots, config) {
    column.align = column.align || 'center';
    column.dataIndex = column.dataIndex || column.field.split(".");
    if(column.ellipsis == undefined && config.ellipsis) {
        column.ellipsis = config.ellipsis;
    }

    initSlot(column, slots); // 初始化插槽

    if(column.dict || column.url || column.options) {
        initColumnFormatterSlot(column);
    } else if(column.type == 'date') {
        initDatetimeColumnSlot(column);
    } else if(column.sortable === true) {
        initSortableColumn(column, config);
    } else if(column.formatter instanceof Function) {
        column['__slot'] = column.formatter;
    }
}
function initTableColumns(columns, slots, config) {
    slots = {...slots};
    columns.forEach(column => {
        if(!column['__init']) {
            column['__init'] = true;
            initColumn(column, slots, config);
            if(column['__slot'] && !slots['bodyCell']) {
                slots['bodyCell'] = (args) => {
                    if(args.column == null) return null;
                    let vnode = args.column['__slot'] ? args.column['__slot'](args) : args.text;
                    // 常驻可编辑列(列定义editable=true): 包单元格上下文供控件注册实例, 写值后触发已绑定事件
                    if(args.column.editable === true && config.tableContext) {
                        return <CellEditProvider tableContext={config.tableContext} record={args.record} column={args.column}>{vnode}</CellEditProvider>;
                    }
                    return vnode;
                }
            }
        }
    })
    return slots;
}
export default defineComponent({
    name: 'UTable',
    props: {
        sticky: {default: false},
        dataSource: {type: Array},
        func: {type: Boolean, default: true},
        size: {type: String, default: "middle"},
        bordered: {type: Boolean, default: true},
        columns: {type: Array, default: () => []},
        ellipsis: {type: Boolean, default: true},
        uid: {type: String, required: true, default: CoreConsts.DefaultTableUid},
        pagination: {
            default: () => {
                return reactive({
                    total: 0,
                    defaultPageSize: 10,
                    showQuickJumper: true,
                    showSizeChanger: true,
                    showTotal: (total, range) => `共 ${total}条`,
                    pageSizeOptions: ['10', '20', '30', '50', '100']
                })
            }
        }, // 是否分页, 不支持使用对象
    },
    components: {TableFuncHeader},
    setup(props, {slots, emit, attrs}) {
        let stickyRef = ref(props.sticky);
        let selectedRows = ref([]);
        let selectedRowKeys = ref([]);
        let dataSourceRef = ref(props.dataSource);
        let {columns, expandedRowKeys} = props;
        let unfoldRowKeys = ref(expandedRowKeys);
        let loading = reactive({spinning: false, description: '数据加载中...'});

        /**
         * @type {LinkContext}
         */
        let linkContext = inject(LinkViewContextKey);
        let tableContext = new TableContext(linkContext);

        // 默认重置列宽度事件
        let defResizeColumn = (width, column) => column.width = width;

        // 监控展开行数据变化
        watch(() => props['expandedRowKeys'], (newVal) => {
            unfoldRowKeys.value = newVal;
        })

        // 监听数据源变化
        watch(() => props.dataSource, (newVal) => {
            dataSourceRef.value = newVal;
        })

        watch(() => props.sticky, (newVal) => {
            stickyRef.value = newVal;
        })

        // 代理选中改变事件
        if(attrs.rowSelection) {
            let proxyChangeEvent = attrs.rowSelection.onChange;
            attrs.rowSelection.onChange = (selectedKeys, rows) => {
                selectedRows.value = rows;
                selectedRowKeys.value = selectedKeys;
                if(proxyChangeEvent instanceof Function) {
                    proxyChangeEvent(selectedKeys, rows);
                }
            }
        }

        // 分页触发事件
        if(props.pagination instanceof Object) {
            let onChange = props.pagination.onChange;
            props.pagination.onChange = (current, pageSize) => {
                if(onChange instanceof Function) {
                    onChange(current, pageSize);
                }

                props.pagination.current = current;
                props.pagination.pageSize = pageSize;
                tableContext.pageChange(current, pageSize);
            }

            // 每页条数改变事件
            let onShowSizeChange = props.pagination.onShowSizeChange;
            props.pagination.onShowSizeChange = (current, pageSize) => {
                if(onShowSizeChange instanceof Function) {
                    onShowSizeChange(current, pageSize);
                }

                // props.pagination.pageSize = pageSize;
                // tableContext.sizeChange(current, pageSize);
            }
        }

        // 更新列改变
        let slotsRef = ref(initTableColumns(columns, slots, {ellipsis: props.ellipsis, onHandleDragStart, tableContext}));

        let setLoading = (status, tip) => {
            loading.spinning = status;
            loading.description = tip || CoreConsts.TableSpinLoadingTip;
        }
        let setSticky = (status) => stickyRef.value = status
        let setDataSource = (ds) => {
            dataSourceRef.value = ds;
            emit("update:source", ds)
        }
        let setTotalRows = (total) => {
            if(props.pagination instanceof Object) {
                props.pagination.total = total;
            }
        }

        if(linkContext) {
            tableContext.uid = props.uid;

            onMounted(() => {
                linkContext.addChildrenContext(tableContext);
            })
            onUnmounted(() => {
                linkContext.removeChildrenContext(tableContext);
            })

            tableContext['setSticky'] = setSticky
            tableContext['setLoading'] = setLoading;
            tableContext['setTotalRows'] = setTotalRows;
            tableContext['getDataSource'] = () => dataSourceRef.value;
            tableContext['getTotalRows'] = () => props.pagination instanceof Object ? props.pagination.total : null;
            tableContext['setDataSource'] = setDataSource;
            tableContext['getColumns'] = () => props.columns;
            tableContext['resetPagination'] = (page, size) => {
                if(props.pagination instanceof Object) {
                    page = page ? page : 1;
                    size = size ? size : props.pagination.defaultPageSize;
                    tableContext['pageSize'] = props.pagination.pageSize = size;
                    tableContext['currentPage'] = props.pagination.current = page;
                }
            }
            if(props.pagination instanceof Object) { // 如果需要分页
                tableContext['currentPage'] = 1;
                tableContext['pageSize'] = props.pagination.defaultPageSize
            }
        }
        // 拖拽排序
        const dragKey = ref(null)
        function onHandleDragStart(record, event) {
            dragKey.value = record[attrs.rowKey]
            event.dataTransfer?.setData('text/plain', dragKey.value)
        }
        const onRow = record => ({
            onDragover: (event) => {
                event.preventDefault()
            },
            onDrop: () => {
                if (!dragKey.value || dragKey.value === record[attrs.rowKey]) {
                    return
                }
                const current = [...dataSourceRef.value]
                const fromIndex = current.findIndex(item => item[attrs.rowKey] === dragKey.value)
                const toIndex = current.findIndex(item => item[attrs.rowKey] === record[attrs.rowKey])
                if (fromIndex === -1 || toIndex === -1) {
                    return
                }
                const drop = [];
                if(fromIndex < toIndex) {
                    drop[0] = current[toIndex]
                    drop[1] = current[toIndex+1]
                } else {
                    drop[0] = current[toIndex-1]
                    drop[1] = current[toIndex]
                }

                const drag = current[fromIndex]
                const [moved] = current.splice(fromIndex, 1)
                current.splice(toIndex, 0, moved)
                setDataSource(current)
                dragKey.value = null

                emit('sorted', {drag, drop, fromIndex, toIndex})
            },
        })

        provide(FuncContextKey, tableContext);
        // 暴露TableContext
        if(attrs['onUpdate:context'] instanceof Function) {
            attrs['onUpdate:context'](tableContext);
        }
        // 完整路径uid(父上下文:自身), 用于data-uid脚本光标DOM定位
        let fullUid = linkContext ? `${linkContext.uid}:${props.uid}` : props.uid;
        return {slotsRef, selectedRows, selectedRowKeys, onRow
            , unfoldRowKeys, loading, dataSourceRef, stickyRef, setSticky
            , setDataSource, setLoading, setTotalRows, defResizeColumn, tableContext, fullUid}
    },
    created() {
        this.tableContext['expanded'] = this.expanded;
        this.tableContext['getSelectedRows'] = this.getSelectedRows;
        this.tableContext['getSelectedRowKeys'] = this.getSelectedRowKeys;
        this.tableContext['getRowKey'] = () => this.$attrs.rowKey;
    },
    render() {
        if(this.stickyRef || this.stickyRef === '') {
            if(typeof this.stickyRef !== 'object') {
                this.stickyRef = {offsetHeader: -16}
            }
        }

        initTableColumns(this.columns, this.slotsRef, {});
        let onResizeColumn = this.$attrs['onResizeColumn'] || this.defResizeColumn;
        return (
            <div class="iv-basic-table" data-uid={this.fullUid}>
                {this.$slots['func'] ? <TableFuncHeader columns={this.columns} func={this.func} tableContext={this.tableContext} v-slots={this.$slots}/> : ''}
                <ATable {...this.$attrs} columns={this.columns} ref="ATableRef" sticky={this.stickyRef}
                    loading={this.loading} dataSource={this.dataSourceRef} size={this.size} bordered={this.bordered}
                    pagination={this.pagination} v-slots={this.slotsRef} expandedRowKeys={this.unfoldRowKeys}
                    onExpandedRowsChange={this.expandedRowsChange} onResizeColumn={onResizeColumn} customRow={(row) => {
                        return {
                            onClick: (e) => this.$emit('rowClick', {e, row}),       // 点击行
                            onDblclick: (e) => this.$emit('rowDblclick', {e, row}), // 行双击
                        }
                     }} on-row={this.onRow}>
                </ATable>
            </div>)
    },
    methods: {
        getPagination() {
          return this.$props.pagination ? this.page : false;
        },

        /**
         * @returns {UnwrapRef<TableContext>}
         */
        getTableContext() {
            return this.tableContext;
        },
        /**
         * 展开/折叠
         */
        expanded(expandedRows) {
            if(expandedRows) {
                this.unfoldRowKeys = expandedRows;
                return; // 展开传入的行
            }

            if(this.unfoldRowKeys && this
                .unfoldRowKeys.length > 0) {
                this.unfoldRowKeys = [];
            } else {
                this.unfoldRowKeys = this.getAllKeys();
            }
        },

        /**
         * 展开行改变
         * @param expandedRows
         */
        expandedRowsChange(expandedRows) {
            this.unfoldRowKeys = expandedRows;
        },
        /**
         * 获取所有的可展开keys
         * @return {null|[]}
         */
        getAllKeys() {
            let dataSource = this.dataSourceRef;
            if(dataSource instanceof Array) {
                let keys = [];
                let doGetAllKeys = (children) => {
                    children.forEach(item => {
                        if(item.children instanceof Array) {
                            keys.push(item[this.$attrs.rowKey])
                            doGetAllKeys(item.children)
                        }
                    })

                    return keys;
                }

                return doGetAllKeys(dataSource);
            }

            return null;
        },

        getDataSource() {
            return this.$refs['ATableRef'].dataSource;
        },

        getSelectedRows() {
            return this.selectedRows;
        },

        getSelectedRowKeys() {
            return this.selectedRowKeys;
        },

        setSelectedRowKeys(selectedRowKeys) {
            this.selectedRowKeys = selectedRowKeys;
        }
    }
})

export {initTableColumns}
