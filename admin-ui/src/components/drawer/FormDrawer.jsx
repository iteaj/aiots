import {defineComponent, inject, provide, ref} from "vue";
import {FuncContextKey, LinkViewContextKey, LinkViewPosition, PositionConst, VisibleContextKey} from "@/utils/ProvideKeys";
import {EditContext} from "@/components/view/Context";
import MixinsEditItem from "@/components/edit/MixinsEditItem";

const FooterColFuncConfig = {xs: 12, ms: 12, md: 12}
const FooterColEmptyConfig = {xs: 0, ms: 0, md: 6}
export default defineComponent({
    name: 'UFormDrawer',
    props: {
        title: String,
        centered: Boolean,
        afterClose: Function,
        zIndex: {default: 1000},
        keyboard: {default: true},
        closable: {default: true},
        size: {default: 'default'},
        forceRender: {default: false},
        placement: {default: 'right'},
        maskClosable: {default: true},
        afterOpenChange: Function,
        destroyOnHidden: {default: false},
        uid: {type: String, required: true},
        loading: {type: Boolean, default: false},
        rootStyle: {type: Object, default: () => { return {position: 'absolute'}}},
        rootClass: {type: String, default: 'iv-drawer iv-form-drawer'},
        styles: {type: Object, default: () => { return {
                footer: {textAlign: 'center'},
            }
        }},
    },
    mixins: [MixinsEditItem],
    setup(props, {attrs, slots}) {
        let formRef = ref(null);
        let visible = ref(false);
        let spinning = ref(false);
        let routerCall = () => null;
        let spinTip = ref("");

        /**
         * @type {LinkContext}
         */
        let linkContext = inject(LinkViewContextKey);
        let editContext = new EditContext(linkContext);
        if(linkContext) {
            editContext.uid = props.uid;
            linkContext.addChildrenContext(editContext);
        }

        let slotProxy = {
            title: () => {
                let func = editContext.openType;
                let model = formRef.value ? formRef.value.getEditModel() : {};
                return slots.title ? slots.title({model, func}) : props.title
            },
            // extra: () => slots.extra ? slots.extra() : null,
            // closeIcon: () => slots.closeIcon ? slots.closeIcon() : null,
        }
        if(slots.footer) {
            slotProxy.footer = () => {
                let model = formRef.value ? formRef.value.getEditModel() : {};
                return <ARow>
                    <ACol {...FooterColEmptyConfig}></ACol>
                    <ACol {...FooterColFuncConfig}>{slots.footer({model})}</ACol>
                    <ACol {...FooterColEmptyConfig}></ACol>
                </ARow>
            }
        }

        slots.extra ? slotProxy.extra = slots.extra : null;
        slots.closeIcon ? slotProxy.closeIcon = slots.closeIcon : null;

        provide(FuncContextKey, editContext);
        // 合体容器(EditContext兼任LinkContext, 容器面见Context.js EditContext): 插槽内无ULinkView包裹直挂的组件(UTabs/UTable等)
        // 经LinkViewContextKey注册进弹框自身childContextMaps而非穿透外层容器——组件与弹框同域, 未打开时随弹框整体过滤不泄漏;
        // 面板容器(ULinkView)注入源变为editContext不影响收编: 其parent经下方Drawer位置分支统一覆盖为editContext并挂childLinkContext
        provide(LinkViewContextKey, editContext);
        provide(LinkViewPosition, PositionConst.Drawer);
        // 弹框内容域收编: 弹框内UChildView面板容器(ULinkView)经LinkView.vue挂载规则(position≠Page且VisibleContextKey可用)
        // 自动挂editContext.childLinkContext → 焦点弹框序列化经containers输出面板内容, 页面主体层遍历自动排除(不泄漏)
        provide(VisibleContextKey, editContext);

        // 暴露EditContext
        if(attrs['onUpdate:context'] instanceof Function) {
            attrs['onUpdate:context'](editContext);
        }
        return {formRef, spinning, spinTip, visible, slotProxy, editContext, routerCall, linkContext}
    },
    render() {
        // 同步标题到上下文(页面骨架自描述: 编辑抽屉标题可能动态变化, 每次渲染保持最新)
        this.editContext.title = this.title;
        let model = {}, context = this.editContext;

        if(this.formRef) {
            model =  this.formRef.getEditModel();
        } else {
            this.formRef = this.$refs['iemFormRef'];
        }

        return(<ADrawer v-model={[this.visible, 'open', ["modifier"]]}
                {...this.$props} v-slots={this.slotProxy} getContainer=".u-page-view" ref="ADrawerRef">
            <ASpin size="small" description={this.spinTip} spinning={this.spinning}>
                <UForm {...this.$attrs} ref="iemFormRef">
                    {this.$slots.default ? this.$slots.default({model, context}) : []}
                </UForm>
            </ASpin>
        </ADrawer>)
    },
    mounted() {
        this.routerCall = this.$router.beforeEach((to, from, next) => {
            if(this.visible) {
                this.visible = false;
            }
            next();
        })
    },
    unmounted() {
        this.visible = false;
    }
})

