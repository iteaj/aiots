import {defineComponent, Fragment, inject, onMounted, onUnmounted} from "vue";
import {LinkViewContextKey} from "@/utils/ProvideKeys";
import {TabContext} from "@/components/view/Context";
import UTabPane from "@/components/view/UTabPane.jsx";

/**
 * 展开插槽vnode(递归数组/Fragment): 与a-tabs内部flattenChildren同口径, 保证UTabPane识别与面板位置索引一致
 * @param vnodes {Array}
 * @return {Array}
 */
function flattenVNodes(vnodes) {
    let out = [];
    for(let vnode of vnodes) {
        if(Array.isArray(vnode)) {
            out.push(...flattenVNodes(vnode));
        } else if(vnode && vnode.type === Fragment) {
            out.push(...flattenVNodes(vnode.children || []));
        } else if(vnode) {
            out.push(vnode);
        }
    }
    return out;
}

/**
 * 标签页容器(UTabs): 页面/弹框内多面板页签的U化封装
 * 职责边界: 受控组件模式(props down + events up) + 上下文生命周期(挂载注册/卸载注销)
 * 1. activeKey受控: 切换上抛update:activeKey/change, 父回写后a-tabs随之高亮(激活态唯一真相在props, TabContext构造时持有props现取);
 * 2. 挂载时将TabContext(构造即绑定props: uid/activeKey现取, 面板声明paneDecls由渲染期同步)注册入所属linkContext, 卸载对称注销;
 *    注册条件=有面板声明(paneDecls, UTabs渲染期统一收集); 面板声明须首渲染可见(v-for异步晚到/条件渲染请改用items通道);
 * 3. 面板内容惰性渲染: 未访问面板未挂载(内容组件不注册), 访问过切走仅隐藏保持挂载, 切换后重查焦点视图获取面板内容
 * 4. 面板声明双通道(互斥, items优先; 生效通道声明统一收集进TabContext.paneDecls——自描述与switch_tab切换目标的唯一数据源):
 *    a) items prop声明(推荐): 面板内容经#contentRender插槽按item.key分发(面板内容归属需业务自行包UChildView);
 *    b) 默认插槽UTabPane声明(仅items未声明时启用; 默认插槽仅识别UTabPane, 原生ATabPane声明请直接用a-tabs): UTabPane vnode → a-tabs items声明(key/label/disabled),
 *       面板容器完整uid(tabsUid_面板key)改写进vnode.key, items.content直接渲染该vnode——UTabPane组件内即LinkView面板容器,
 *       直挂组件注册进面板容器与宿主主体及其他面板隔离(焦点视图按容器树输出, fullUid带容器前缀如 modelTabs_attr:search),
 *       无需业务包UChildView;
 * 5. 建议v-model:activeKey双向绑定; 未绑定时(null)底层自管理默认激活首个面板。
 */
export default defineComponent({
    name: "UTabs",
    props: {
        // 标签容器标识(所属容器内唯一): 页面自描述定位(tabs项fullUid)与switch_tab动作目标
        uid: {type: String, required: true},
        // 当前激活标签key(建议v-model:activeKey双向绑定; 未绑定时默认激活首个面板)
        activeKey: {type: String, default: null},
        // 面板声明(key必填/label标题/disabled禁用); 未传items时可改用默认插槽UTabPane声明
        items: {type: Array, default: null}
    },
    emits: ['update:activeKey', 'change'],
    setup(props, {emit}) {
        /**
         * @type {LinkContext|null}
         */
        let linkContext = inject(LinkViewContextKey, null);

        /**
         * @type {TabContext}
         */
        // 构造即绑定组件props: uid/activeKey现取均在TabContext内部, 面板声明由渲染期syncPaneDecls同步, 组件仅剩模板与生命周期壳
        let tabContext = new TabContext(linkContext, props);
        if(linkContext) {
            // 切换回调(供switch_tab动作驱动): 模拟a-tabs原生点击事件序列(update:activeKey→父v-model回写, change→业务监听)
            tabContext.switcher = (key) => {
                emit('update:activeKey', key);
                emit('change', key);
            };
        }

        // 挂载注册/卸载注销: 注册条件=有面板声明(items或默认插槽UTabPane, 首渲染先于onMounted故paneDecls已同步)
        let registered = false;
        let register = () => {
            if(registered || linkContext == null) {
                return;
            }
            let hasPanes = Object.keys(tabContext.paneDecls).length > 0;
            if(hasPanes) {
                linkContext.addChildrenContext(tabContext);
                registered = true;
            }
        };
        onMounted(register);
        onUnmounted(() => {
            if(linkContext) {
                linkContext.removeChildrenContext(tabContext);
            }
            registered = false;
        });

        // a-tabs原生事件转发(点击时a-tabs先发update:activeKey再发change, 各转发一次给父组件)
        let keyUpdate = (key) => {
            emit('update:activeKey', key);
        };
        let paneChange = (key) => {
            emit('change', key);
        };
        // 完整路径uid(父上下文:自身), 用于data-uid脚本光标DOM定位
        let fullUid = linkContext ? `${linkContext.uid}:${props.uid}` : props.uid;
        return {keyUpdate, paneChange, tabContext, fullUid}
    },
    render() {
        let paneDecls = {};
        let mergedItems = null;
        // 面板声明统一收集(供TabContext自描述/switch_tab切换目标): items声明翻译为paneDecls口径(key/tab/disabled)
        if(this.items && this.items.length > 0) {
            for(let item of this.items) {
                let itemKey = String(item.key);
                // label支持字符串或函数(#tab具名插槽自定义标题), tab字段直接取label原值(函数时序列化前自行求值)
                paneDecls[itemKey] = {key: itemKey, tab: item.label, disabled: !!item.disabled};
            }
        } else {
            // 默认插槽UTabPane声明通道(仅items未声明时启用): UTabPane vnode → a-tabs items声明(key/label/disabled),
            // 面板内容经items.content惰性渲染原UTabPane vnode(激活/访问后挂载)——vnode.key承载面板容器完整uid(tabsUid_面板key),
            // UTabPane组件内渲染LinkView实现内容与宿主隔离; 同时收集声明(key/tab/disabled)供TabContext自描述
            let vnodes = this.$slots.default ? flattenVNodes(this.$slots.default()) : [];
            if(vnodes.length > 0) {
                mergedItems = [];
                for(let vnode of vnodes) {
                    if(vnode.type === UTabPane) {
                        // 面板key=模板key(Vue3中key同步进vnode.key, 不落props); 缺失直接报错(切换/定位/自描述均依赖key, 兜底掩盖声明错误)
                        let paneKey = vnode.key == null ? null : String(vnode.key);
                        if(paneKey == null) {
                            throw new Error(`标签容器[${this.uid}]的UTabPane缺少面板key声明(如 <UTabPane key="attr" tab="属性">)`);
                        }
                        let paneProps = vnode.props || {};
                        let paneChildren = vnode.children || null;
                        paneDecls[paneKey] = {key: paneKey, tab: paneProps.tab != null ? paneProps.tab : '', disabled: !!paneProps.disabled};
                        // 面板容器完整uid(tabsUid_面板key)改写进vnode.key承载(面板项key仍用paneKey, 两者解耦):
                        // vnode未被渲染过(插槽调用产物, UTabs每次渲染新建), content直接渲染该vnode,
                        // UTabPane实例以instance.vnode.key作为LinkView容器uid(页面级注册唯一; 序列化按此前缀识别面板归属并仅输出激活面板)
                        vnode.key = this.uid + '_' + paneKey;
                        mergedItems.push({
                            key: paneKey,
                            // label支持字符串(tab)或函数(#tab具名插槽自定义标题, 与ATabPane口径一致)
                            label: (paneChildren && paneChildren.tab) || paneProps.tab,
                            disabled: !!paneProps.disabled,
                            // 面板内容: a-tabs激活/访问后惰性渲染UTabPane vnode(组件内包LinkView面板容器)
                            content: () => vnode
                        });
                    }
                }
            }
        }
        // 渲染期整体同步声明(每次渲染重建保持与模板声明一致; paneDecls非响应式, 仅上下文读取用)
        this.tabContext.syncPaneDecls(paneDecls);
        let tabSlots = {...this.$slots};
        // 面板内容已并入items(UTabPane通道), 默认插槽不再透传a-tabs避免legacy双通道; items通道的contentRender等具名插槽照常透传
        delete tabSlots.default;
        return <ATabs {...this.$attrs} data-uid={this.fullUid} activeKey={this.activeKey} items={mergedItems || this.items}
                      onUpdate:activeKey={this.keyUpdate} onChange={this.paneChange} v-slots={tabSlots}/>
    }
})
