import {defineComponent, provide} from "vue";
import {$View, ViewContext} from "@/components/view/Context";
import {
    EditableTableKey,
    LinkViewContextKey,
    LinkViewPosition,
    PositionConst,
    ViewContextKey
} from "@/utils/ProvideKeys";
import CoreConsts from "@/components/CoreConsts";
import ULinkView from "@/components/view/LinkView.vue";
import UBasicDrawer from "@/components/drawer/BasicDrawer";
import UBasicModal from "@/components/modal/BasicModal";

export default defineComponent({
    name: "UView",
    props: {
        // 功能名称 比如 用户管理
        name: {type: String, default: ''},
        // 数据记录的唯一标识(用于编辑、删除、获取、设置等)
        rowKey: {type: String, default: CoreConsts.DefaultRowKey},
    },
    components: {ULinkView},
    setup(props) {
        const viewContext = new ViewContext(props);
        provide(LinkViewContextKey, null);
        provide(ViewContextKey, viewContext);
        provide(LinkViewPosition, PositionConst.Page);
        provide(EditableTableKey, false);
        return {viewContext}
    },
    created() {
        let $parent = this.$parent;
        $parent.$view = new $View(this.viewContext);
    },
    render() {
        return <ULinkView {...this.$props} uid={CoreConsts.PrimaryUid} class="u-page-view">
            {this.$slots.default ? this.$slots.default() : []}
        </ULinkView>
    },
    methods: {
        /**
         * @return {$View|void|*}
         */
        getView() {
            return this.$parent.$view;
        }
    }
})

/**
 * UDrawerView = UDrawer + UChildView 组合组件
 * 用于在抽屉内提供独立子视图上下文，支持完整 CRUD
 * 用法：
 *   <UDrawerView uid="xxx" title="xxx" @open="onOpen">
 *     <UViewSearch>...</UViewSearch>
 *     <UViewTable>...</UViewTable>
 *     <UViewModal>...</UViewModal>
 *   </UDrawerView>
 */
export const UDrawerView = defineComponent({
    name: 'UDrawerView',
    components: {UBasicDrawer, ULinkView},
    props: {
        uid: {type: String, required: true},
        rowKey: {type: String, default: 'id'},
    },
    render() {
        debugger
        return <UBasicDrawer {...this.$attrs} uid={this.uid} v-slots={{
            ...this.$slots,
            default: () => <ULinkView uid={this.uid} rowKey={this.rowKey}>
                {this.$slots.default ? this.$slots.default() : []}
            </ULinkView>
        }}/>
    }
})

/**
 * UModalView = UModal + UChildView 组合组件
 * 用于在弹窗内提供独立子视图上下文，支持完整 CRUD
 * 用法：
 *   <UModalView uid="xxx" title="xxx" @open="onOpen">
 *     <UViewSearch>...</UViewSearch>
 *     <UViewTable>...</UViewTable>
 *     <UViewModal>...</UViewModal>
 *   </UModalView>
 */
export const UModalView = defineComponent({
    name: 'UModalView',
    components: {UBasicModal, ULinkView},
    props: {
        uid: {type: String, required: true},
        rowKey: {type: String, default: 'id'},
    },
    render() {
        return <UBasicModal {...this.$attrs} uid={this.uid} v-slots={{
            ...this.$slots,
            default: () => <ULinkView uid={this.uid} rowKey={this.rowKey}>
                {this.$slots.default ? this.$slots.default() : []}
            </ULinkView>
        }}/>
    }
})
