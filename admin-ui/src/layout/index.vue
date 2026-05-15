<template>
    <a-layout class="iv-layout iv-primary">
      <!--右侧头栏-->
      <LayoutHeader />
      <!-- 视图页 -->
      <router-view />
    </a-layout>
</template>

<script>
import {provide, ref} from 'vue'
import { mapGetters, useStore } from "vuex";
import LayoutHeader from "@/layout/LayoutHeader.vue";
import {RowContextKey, ViewContextKey} from "@/utils/ProvideKeys";

export default {
    name: "index",
    components: { LayoutHeader },
    setup () {

        // 初始化后台菜单信息到路由
        useStore().dispatch('sys/initMenus')

        // 初始化当前登入的用户信息
        useStore().dispatch('sys/initUser')

        // 初始化系统配置信息
        useStore().dispatch('sys/initConfig')

        // 初始化菜单权限
        useStore().dispatch('sys/initPermissions')
        /**
         * URow组件将覆盖此对象, 使用在表单组件(表单组件使用了ACol)
         */
        provide(RowContextKey, {});

        /**
         * 视图组件使用的上下文
         * @see UView.jsx
         * @see IvzFuncView.vue
         * @see IvzMenuView.vue
         * @param value 必须为 null
         */
        provide(ViewContextKey, null);

      let aliveComponents = ref([]);
      return { aliveComponents };
    },

    computed: {
        ...mapGetters({
            taskBarData: 'sys/taskBarData',
        }),
        // 用于缓存视图页
        alive () {
            return this.taskBarData
                .filter(route => route.meta['keepAlive'] != null)
                .map(route => route.meta.keepAlive);
        }
    }
}
</script>

<style>
.iv-layout {
  width: 100%;
  height: 100%;
  --iv-layout-header-bg: var(--ant-color-bg-elevated);
}

body ::-webkit-scrollbar {
    width: 6px;
    height: 6px;
}
/*滚动滑块样式*/
body ::-webkit-scrollbar-thumb {
    border-radius: 3px;
    background-clip: padding-box;
    //border: 3px solid rgb(168 168 168 / 0);
  background: var(--iv-primary-color-bgt);
}

html.iv-theme-light ::-webkit-scrollbar-thumb,
html.iv-theme-dark ::-webkit-scrollbar-thumb {
  background: var(--ant-color-split);
}

/*滚动槽样式*/
body ::-webkit-scrollbar-track {
    border-radius: 3px;
  background: var(--iv-primary-color-bgt);
}

.iv-theme-light *::-webkit-scrollbar-track,
.iv-theme-dark *::-webkit-scrollbar-track {
  background: var(--ant-color-split);
}

/*动画*/
.slide-fade-enter-active {
    transition: all 0.5s ease-out;
}

.slide-fade-leave-active {
    transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from, .slide-fade-leave-to {
    transform: translateX(20px);
    opacity: 0;
}
</style>
