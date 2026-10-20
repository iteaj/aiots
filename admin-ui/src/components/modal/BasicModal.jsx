import {defineComponent, inject, provide, ref} from "vue";
import {LinkViewContextKey, LinkViewPosition, PositionConst, VisibleContextKey} from "@/utils/ProvideKeys";
import {VisibleContext} from "@/components/view/Context";
import CoreConsts from "@/components/CoreConsts";
import MixinsVisible from "@/components/mixins/MixinsVisible";

export default defineComponent({
  name: 'UModal',
  props: {
    uid: {type: String, required: true}
  },
  mixins: [MixinsVisible],
  setup(props, {attrs, slots}) {
    let refs = ref(null);
    let visible = ref(false);

    /**
     * @type {LinkContext}
     */
    let linkContext = inject(LinkViewContextKey);
    let visibleContext = new VisibleContext(linkContext);
    if(linkContext) {
      visibleContext.uid = props.uid;
      linkContext.addChildrenContext(visibleContext)
    }
    provide(VisibleContextKey, visibleContext);
    provide(LinkViewPosition, PositionConst.Modal);

    // 暴露EditContext
    if(attrs['onUpdate:context'] instanceof Function) {
      attrs['onUpdate:context'](visibleContext);
    }

    // 完整路径uid(父上下文:自身), 用于data-uid脚本光标DOM定位
    let fullUid = linkContext ? `${linkContext.uid}:${props.uid}` : props.uid;
    return {refs, visible, visibleContext, fullUid}
  },
  render() {
    // 同步标题到上下文(页面骨架自描述: 弹框标题可能动态变化, 每次渲染保持最新)
    this.visibleContext.title = this.$attrs.title;
    // 纯弹框: 不承载ULinkView内容容器, 只提供可见性上下文(VisibleContext)与provide链;
    // 弹框内容注册域=所在视图容器(ULinkView, 由UView/UChildView/UModalView/UDrawerView提供),
    // 需要独立内容域时使用UModalView/UDrawerView(其default插槽内置ULinkView承载容器)
    return <AModal {...this.$attrs} wrapProps={{'data-uid': this.fullUid}} v-model={[this.visible, 'open', ["modifier"]]} v-slots={this.$slots} />
  }
})
