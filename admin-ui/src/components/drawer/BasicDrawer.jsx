import {defineComponent, inject, provide, ref} from "vue";
import {LinkViewContextKey, LinkViewPosition, PositionConst, VisibleContextKey} from "@/utils/ProvideKeys";
import {VisibleContext} from "@/components/view/Context";
import MixinsVisible from "@/components/mixins/MixinsVisible";

export default defineComponent({
    name: 'UDrawer',
    props: {
        title: String,
        centered: Boolean,
        afterClose: Function,
        size: {default: 680},
        zIndex: {default: 1000},
        keyboard: {default: true},
        closable: {default: true},
        forceRender: {default: false},
        placement: {default: 'right'},
        maskClosable: {default: false},
        getContainer: {default: '.u-page-view'},
        destroyOnHidden: {default: false},
        uid: {type: String, required: true},
        modelValue: {type: Object},
        'onUpdate:modelValue': {type: Function},
        rootClass: {type: String, default: 'iv-drawer iv-basic-drawer'},
        rootStyle: {type: Object, default: () => { return {position: 'absolute'}}},
        styles: {type: Object, default: () => { return {
                footer: {textAlign: 'center'},
            }
        }},
    },
    mixins: [MixinsVisible],
    setup(props, {attrs}) {
        let formRef = ref(null);
        let visible = ref(false);
        let spinning = ref(false);
        let routerCall = () => null;
        let spinTip = ref("");

        /**
         * @type {LinkContext}
         */
        let linkContext = inject(LinkViewContextKey);
        let visibleContext = new VisibleContext(linkContext);
        if(linkContext) {
            visibleContext.uid = props.uid;
            linkContext.addChildrenContext(visibleContext);
        }

        provide(VisibleContextKey, visibleContext);
        provide(LinkViewPosition, PositionConst.Drawer);

        // 暴露VisibleContext
        if(attrs['onUpdate:context'] instanceof Function) {
            attrs['onUpdate:context'](visibleContext);
        }

        // 完整路径uid(父上下文:自身), 用于data-uid脚本光标DOM定位
        let fullUid = linkContext ? `${linkContext.uid}:${props.uid}` : props.uid;
        return {formRef, spinning, spinTip, visible, routerCall, linkContext, visibleContext, fullUid}
    },
    render() {
        // 同步标题到上下文(页面骨架自描述: 抽屉标题可能动态变化, 每次渲染保持最新)
        this.visibleContext.title = this.title;
        // 纯弹框: 不承载ULinkView内容容器, 只提供可见性上下文(VisibleContext)与provide链;
        // 弹框内容注册域=所在视图容器(ULinkView, 由UView/UChildView/UModalView/UDrawerView提供),
        // 需要独立内容域时使用UModalView/UDrawerView(其default插槽内置ULinkView承载容器)
        return(<ADrawer v-model={[this.visible, 'open', ["modifier"]]} {...this.$props} data-uid={this.fullUid} v-slots={this.$slots}/>)
    },
    mounted() {
        this.routerCall = this.$router.beforeEach((from, to, next) => {
            if(this.visible) {
                this.visible = false;
            }

            next();
        })
    },
    unmounted() {
        this.routerCall();
    }
})

