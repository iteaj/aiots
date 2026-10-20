import {computed, defineComponent, onMounted, onUnmounted, provide, useSlots} from "vue";
import {useRoute} from "vue-router";
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
import UConfirm from "@/components/modal/UConfirm";
import {emitPageEvent} from "@/utils/pageEvents";

export default defineComponent({
    name: "UView",
    props: {
        // 功能名称 比如 用户管理
        name: {type: String, default: ''},
        // 数据记录的唯一标识(用于编辑、删除、获取、设置等)
        rowKey: {type: String, default: CoreConsts.DefaultRowKey},
    },
    components: {ULinkView, UConfirm},
    setup(props) {
        const viewContext = new ViewContext(props);
        provide(LinkViewContextKey, null);
        provide(ViewContextKey, viewContext);
        provide(LinkViewPosition, PositionConst.Page);
        provide(EditableTableKey, false);

        // 页面挂载/卸载时触发页面事件:
        const route = useRoute();
        onMounted(() => {
            emitPageEvent('page:opened', {
                url: route.path,
                name: props.name,
                viewContext,
            });
        });
        onUnmounted(() => {
            emitPageEvent('page:closed', {url: route.path});
        });
        return {viewContext}
    },
    created() {
        let $parent = this.$parent;
        $parent.$view = new $View(this.viewContext);
    },
    render() {
        return <ULinkView {...this.$props} uid={CoreConsts.PrimaryUid} class="u-page-view">
            {this.$slots.default ? this.$slots.default() : []}
            {/* 页面级确认弹框(UConfirm): 常驻根容器, 供del/ajax等确认动作, 见组件注释 */}
            <UConfirm/>
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
 * UDrawerView = UDrawer(纯弹框) + 内容容器 组合组件
 * 用于在抽屉内提供独立子视图上下文(内容容器=default插槽包ULinkView, uid=弹框uid), 支持完整 CRUD
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
    setup() {
        let slots = useSlots();
        let drawerSlots = computed(() => {
          let result = {};
          Object.keys(slots).filter(key => key !== 'default').forEach(key => {
            result[key] = slots[key];
          });
          return result;
        });
        return {drawerSlots}
    },
    render() {
        // 内容容器=default插槽包ULinkView(uid=弹框uid): 弹框内组件注册该容器(经收编parent=visibleContext登记childLinkContext),
        // 与弹框壳(UBasicDrawer纯弹框, 不含ULinkView)职责分离
        return <UBasicDrawer {...this.$attrs} uid={this.uid} v-slots={this.drawerSlots} >
            <ULinkView uid={this.uid} rowKey={this.rowKey}>
                {this.$slots.default ? this.$slots.default() : []}
            </ULinkView>
        </UBasicDrawer>
    }
})

/**
 * UModalView = UModal(纯弹框) + 内容容器 组合组件
 * 用于在弹窗内提供独立子视图上下文(内容容器=default插槽包ULinkView, uid=弹框uid), 支持完整 CRUD
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
        // 数据记录的唯一标识(用于编辑、删除、获取、设置等)
        rowKey: {type: String, default: CoreConsts.DefaultRowKey},
    },
    setup() {
        let slots = useSlots();
        let modalSlots = computed(() => {
            let result = {};
            Object.keys(slots).forEach(key => {
                result[key] = slots[key];
            });
            return result;
        });
        return {modalSlots}
    },
    render() {
        // 内容容器=default插槽包ULinkView(uid=弹框uid): 弹框内组件注册该容器(经收编parent=visibleContext登记childLinkContext),
        // 与弹框壳(UBasicModal纯弹框, 不含ULinkView)职责分离
        return <UBasicModal {...this.$attrs} uid={this.uid} v-slots={this.modalSlots}>
            <ULinkView uid={this.uid} rowKey={this.rowKey}>
                {this.$slots.default ? this.$slots.default() : []}
            </ULinkView>
        </UBasicModal>
    }
})
