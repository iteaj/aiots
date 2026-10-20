import {defineComponent, inject, ref} from "vue";
import {LinkViewContextKey} from "@/utils/ProvideKeys";
import {FuncConfig, VisibleContext} from "@/components/view/Context";
import CoreConsts from "@/components/CoreConsts";
import MixinsVisible from "@/components/mixins/MixinsVisible";

/**
 * 操作确认弹框(UConfirm): 页面级统一确认弹框, 由UView自动内置(页面根容器下常驻注册, uid见CoreConsts.DefaultConfirmUid)
 * 职责边界: 删除/带confirm子功能ajax等需要用户确认的动作, 不再弹antd命令式Modal.confirm(不进组件树/不注册上下文,
 * AI焦点视图不可见也无法操作), 而是经页面根上下文定位本组件后由ask()打开——弹框进入焦点栈(焦点视图),
 * AI可见标题/内容与[确认/取消]按钮, 可经exec_func(CONFIRM/CANCEL, 目标fullUid=UView:UVConfirm)或用户点击执行原确认/取消逻辑;
 * 触发方(UView未挂载本组件/异常)找不到本组件时回退antd静态确认弹框, 不影响功能可用性
 */
export default defineComponent({
    name: 'UConfirm',
    props: {
        // 弹框uid(默认全局统一, 触发方按 PrimaryUid:DefaultConfirmUid 定位; 页面需多实例时显式传入)
        uid: {type: String, default: CoreConsts.DefaultConfirmUid}
    },
    mixins: [MixinsVisible],
    setup(props) {
        /**
         * @type {LinkContext}
         */
        let linkContext = inject(LinkViewContextKey, null);

        let visible = ref(false);
        // 当前待决确认请求(标题/内容/回调): 由触发方经上下文ask()设置, 每次打开覆盖前次未决请求
        let request = ref(null);
        // 确认/取消按钮文本(与原antd静态确认弹框一致, 可经ask的okText/cancelText定制)
        let okText = ref('确认');
        let cancelText = ref('取消');

        /**
         * @type {VisibleContext}
         */
        let visibleContext = new VisibleContext(linkContext);
        if(linkContext) {
            visibleContext.uid = props.uid;
            linkContext.addChildrenContext(visibleContext);
        }

        // 处理确认请求: 关闭弹框(焦点回退下层)后执行原确认回调
        let doOk = () => {
            let req = request.value;
            if(!req) {
                return; // 无待决请求(未打开/已处理)
            }
            finish();
            if(req.onOk instanceof Function) {
                return req.onOk(); // 透传原确认回调结果(删除/请求类回调返回Promise时供exec_func同步等待)
            }
        };
        // 取消处理(点取消按钮/X/Esc/close_dialog动作): 关闭后执行原取消回调
        let doCancel = () => {
            let req = request.value;
            if(!req) {
                return;
            }
            finish();
            if(req.onCancel instanceof Function) {
                return req.onCancel(); // 透传原取消回调结果(与doOk同口径, 常规无返回)
            }
        };
        let finish = () => {
            request.value = null;
            visibleContext.title = null; // 同步清标题(页面骨架占位不再显示旧标题)
            visible.value = false; // 关闭经watch visible出焦点栈
        };

        if(linkContext) {
            // 打开确认弹框(触发方调用): 记录待决请求并置可见(入焦点栈, AI经焦点视图可见)
            visibleContext.ask = function ({title, content, okText: ok, cancelText: cancel, onOk, onCancel}) {
                request.value = {title, content, onOk, onCancel};
                if(ok) {
                    okText.value = ok;
                }
                if(cancel) {
                    cancelText.value = cancel;
                }
                visibleContext.title = title || CoreConsts.ConfirmTitle;
                visible.value = true;
            };
            // 覆写上下文cancel(供close_dialog动作关闭): 未显式决定(未点确认/取消)时关闭视为取消, 与Modal.confirm点X/Esc同语义
            visibleContext.cancel = () => doCancel();

            // 注册确认/取消功能按钮: 直接写入本上下文funcMetas(键=func, 与VisibleContext.getFunc键直查/焦点视图funcsOf同口径),
            // 不经registerFunc(无宿主组件上下文register, 按钮不参与容器查询按钮聚合)
            let registerBtn = (func, title) => {
                let config = new FuncConfig({name: title}, visibleContext, [func]);
                config.title = title; // 按钮显示文本(焦点视图功能按钮标题列)
                config.trigger = () => func == CoreConsts.ChildFuncNameMeta.CONFIRM ? doOk() : doCancel();
                visibleContext.funcMetas[func] = config;
            };
            registerBtn(CoreConsts.ChildFuncNameMeta.CONFIRM, '确认');
            registerBtn(CoreConsts.FuncNameMeta.CANCEL, '取消');
        }

        return {linkContext, visible, request, okText, cancelText, visibleContext, doOk, doCancel}
    },
    render() {
        // 弹框显示: 标题/内容取自当前待决请求(动态同步), 未打开(无请求)时组件常驻但不显示
        let req = this.request;
        return <AModal v-model={[this.visible, 'open', ["modifier"]]}
                       title={req && req.title || CoreConsts.ConfirmTitle}
                       okText={this.okText} cancelText={this.cancelText}
                       maskClosable={false} width={428}
                       onOk={this.doOk} onCancel={this.doCancel}
                       {...this.$attrs}>
            <div class="u-confirm-content" style="padding: 8px 0;">{req && req.content}</div>
        </AModal>
    }
})
