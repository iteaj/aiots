import {defineComponent, getCurrentInstance} from "vue";
import LinkView from "@/components/view/LinkView.vue";

/**
 * 标签面板(UTabPane): UTabs默认插槽内的面板内容容器组件(原生ATabPane的U化形式, 与UTabs配套)
 * 结构: UTabPane实例 = LinkView面板容器 包裹面板内容——面板内容天然与宿主主体及其他面板隔离
 * 机制(与UTabs联动, 无props无注册——身份由vnode.key承载):
 *   1. 声明通道: UTabs渲染期识别默认插槽中的UTabPane vnode → 面板key取模板key(或tabKey/位置索引)
 *      → 收集面板声明(key/tab/disabled)同步TabContext.paneDecls(参与自描述与switch_tab切换目标),
 *      并转为a-tabs items声明(key/label/disabled);
 *   2. 内容通道: UTabs把面板容器完整uid(tabsUid_面板key)改写进vnode.key后由items.content直接渲染该vnode
 *      (面板激活/访问后惰性挂载)——UTabPane实例以instance.vnode.key作为LinkView容器uid, 面板内直挂组件
 *      (搜索/表格等)注册进面板容器, 焦点视图按容器树输出(fullUid带容器前缀如 modelTabs_attr:search);
 *      未访问面板不渲染, 访问过切走仅隐藏保持挂载(激活才查询加载)
 * 用法(替代原生<ATabPane>声明通道以获得自动面板容器, 无需业务包UChildView):
 *     <UTabs uid="xxx" v-model:activeKey="activeKey">
 *       <UTabPane key="attr" tab="属性">面板内容(搜索/表格等)</UTabPane>
 *       <UTabPane key="func" tab="功能">面板内容</UTabPane>
 *     </UTabs>
 * 注意: 1) 仅支持在UTabs默认插槽内使用(items未声明时启用本通道); 2) tab标题支持#tab具名插槽自定义;
 *      3) 与items声明互斥(items优先, 混用/传contentRender插槽时本通道内容不生效);
 *      4) 面板切换需UTabs v-model:activeKey受控绑定; 模板只需写面板key, 容器完整uid由UTabs拼写注入。
 */
export default defineComponent({
    name: "UTabPane",
    // 无需props声明: 模板属性(tab/disabled等)由UTabs渲染期读取进items声明, 不落组件; 面板容器uid经vnode.key注入
    setup(_, {slots}) {
        let instance = getCurrentInstance();
        return () => {
            // vnode.key = UTabs注入的面板容器完整uid(tabsUid_面板key): LinkView容器标识, 面板内组件注册前缀
            return <LinkView uid={instance.vnode.key}>{slots.default ? slots.default() : null}</LinkView>
        }
    }
})
