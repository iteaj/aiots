import {defineComponent} from "vue";

export default defineComponent({
    created() {
        let visibleContext = this.getVisibleContext();
        visibleContext.getVisible = () => this.visible; // 打开状态查询: 箭头函数闭包直读组件visible(供收集端过滤未打开弹框视图内容)
        visibleContext.setVisible = this.setVisible;
        visibleContext.setLoading = this.setLoading;
        visibleContext.asyncVisible = this.openByAsync;
        visibleContext.setLoadingTip = this.setLoadingTip;
    },
    watch: {
        // 弹框可见性变化同步焦点视图栈: 打开入栈(栈顶=最新焦点视图)/关闭移除, 页面骨架按焦点裁剪非焦点弹框
        visible(visible) {
            this.syncFocusStack(visible);
        }
    },
    unmounted() {
        // 兜底: 组件卸载时若仍在焦点栈中则移除(正常关闭路径已由watch处理)
        this.syncFocusStack(false);
    },
    methods: {
        setVisible(visible) {
            this.visible = visible;
        },

        setLoading(status, tip) {
            this.spinning = status;
            this.spinTip = tip || "";
        },

        setLoadingTip(tip) {
            this.spinTip = tip;
        },

        switchSpinning(spinning) {
            this.spinning = spinning;
        },

        /**
         * 异步打开弹框, 表单初始化完成后会出发编辑事件
         * @param row 编辑的行 非必填
         * @param isResetToInit {Boolean} 是否重置编辑模型 非必填
         * @return {Promise<unknown>}
         */
        openByAsync(row, isResetToInit) {
            this.visible = true;
            // 触发Modal或者Drawer打开事件
            this.$emit("open", row);
            // 暴露编辑的数据
            this.$emit('update:source', row);

            return new Promise((resolve, reject) => {
                return resolve(this);
            })
        },

        /**
         * @return {VisibleContext}
         */
        getVisibleContext() {
            return this.visibleContext;
        },

        /**
         * @return {LinkContext}
         */
        getLinkContext() {
            return this.linkContext;
        },

        /**
         * 同步焦点视图栈(ViewContext.focusStack)
         * @param visible {Boolean} true=打开入栈 false=关闭/卸载移除
         */
        syncFocusStack(visible) {
            let visibleContext = this.getVisibleContext();
            let linkContext = visibleContext && visibleContext.getLinkContext instanceof Function ? visibleContext.getLinkContext() : null;
            let viewContext = linkContext && linkContext.getViewContext instanceof Function ? linkContext.getViewContext() : null;
            if(!viewContext || !(viewContext.pushFocus instanceof Function)) {
                return; // 无焦点栈能力(未接入AI自描述)时静默忽略
            }

            if(visible) {
                viewContext.pushFocus(visibleContext);
            } else {
                viewContext.removeFocus(visibleContext);
            }
        }

    }
})
