import {defineComponent, inject, provide, ref} from "vue";
import {FuncContextKey, LinkViewContextKey, LinkViewPosition, PositionConst, ViewContextKey, VisibleContextKey} from "@/utils/ProvideKeys";
import MixinsEditItem from "@/components/edit/MixinsEditItem";
import {EditContext} from "@/components/view/Context";
import CoreConsts from "@/components/CoreConsts";

export default defineComponent({
  name: 'UFormModal',
  props: {
    title: String,
    span: {type: Array}, // labelCol 和wrapperCol简写 如：[6, 18]
    afterClose: Function,
    width: {default: 520},
    classes: {type: Object},
    styles: {type: Object},
    mask: {default: true},
    zIndex: {default: 1000},
    getContainer: {type: Function},
    maskClosable: {default: true},
    afterOpenChange: {type: Function},
    centered: {type: Boolean, default: true},
    closable: {type: Boolean, default: false},
    forceRender: {type: Boolean, default: false},
    destroyOnHidden: {type: Boolean, default: false},
    wrapClassName: {type: String, default: 'u-form-modal'},
    uid: {type: String, required: true, default: CoreConsts.DefaultEditUid}
  },
  mixins: [MixinsEditItem],
  setup(props, {attrs, slots}) {
    let refs = ref(null);
    let formRef = ref(null);
    let visible = ref(false);
    let spinning = ref(false);
    let spinTip = ref("数据处理中...");

    let labelCol = attrs.labelCol, wrapperCol = attrs.wrapperCol;
    if(props.span) {
      if(!labelCol) {
        labelCol = {span: props.span[0]};
      }

      if(!wrapperCol) {
        wrapperCol = {span: props.span[1]}
      }
    }

    /**
     * @type {LinkContext}
     */
    let linkContext = inject(LinkViewContextKey);
    let editContext = new EditContext(linkContext);
    if(linkContext) {
      editContext.uid = props.uid;
      linkContext.addChildrenContext(editContext)
    }

    provide(FuncContextKey, editContext);
    // 合体容器(EditContext兼任LinkContext, 容器面见Context.js EditContext): 插槽内无ULinkView包裹直挂的组件(UTabs/UTable等)
    // 经LinkViewContextKey注册进弹框自身childContextMaps而非穿透外层容器——组件与弹框同域, 未打开时随弹框整体过滤不泄漏(如UViewModal插槽内标签页);
    // 面板容器(ULinkView)注入源变为editContext不影响收编: 其parent经下方Modal位置分支统一覆盖为editContext并挂childLinkContext
    provide(LinkViewContextKey, editContext);
    provide(LinkViewPosition, PositionConst.Modal);
    // 弹框内容域收编: 弹框内UChildView面板容器(ULinkView)经LinkView.vue挂载规则(position≠Page且VisibleContextKey可用)
    // 自动挂editContext.childLinkContext → 焦点弹框序列化经containers输出面板内容, 页面主体层遍历自动排除(不泄漏)
    provide(VisibleContextKey, editContext);

    // 暴露EditContext
    if(attrs['onUpdate:context'] instanceof Function) {
      attrs['onUpdate:context'](editContext);
    }
    return {formRef, refs, spinning, spinTip, visible, labelCol, wrapperCol, editContext, linkContext}
  },
  render() {
    // 同步标题到上下文(页面骨架自描述: 编辑弹框标题可能动态变化, 每次渲染保持最新)
    this.editContext.title = this.title;
    let model = {}, context = this.editContext;

    if(this.formRef) {
      model =  this.formRef.getEditModel();
    }

    let slots = {
      title: () => {
        let func = context.openType;
        return this.$slots.title ? this.$slots.title({model, func}) : this.title
      },
      footer: () => this.$slots.footer ? this.$slots.footer({model, context}) : null
    }

    return <AModal v-model={[this.visible, 'open', ["modifier"]]} {...this.$props} v-slots={slots} ref="iemRef">
      <ASpin size="small" description={this.spinTip} spinning={this.spinning}>
        <UForm {...this.$attrs} labelCol={this.labelCol} wrapperCol={this.wrapperCol} ref="iemFormRef">
          {this.$slots.default ? this.$slots.default({model, context}) : []}
        </UForm>
      </ASpin>
    </AModal>
  }
})
