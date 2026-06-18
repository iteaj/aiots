<template>
  <a-config-provider :locale="zhCN" :theme="theme">
    <ThemeProvider>
      <a-app class="iv-theme-var iv-app">
        <router-view />
      </a-app>
    </ThemeProvider>
  </a-config-provider>
</template>

<script>
import './theme/index.css'
import 'antdv-next/dist/reset.css'
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
import ThemeConfig, {useIVTheme} from './theme/index.js'
import zhCN from 'antdv-next/locale/zh_CN';
import {computed, provide, reactive} from "vue";
import {ThemeProvider} from "antdv-style"
import {GlobalConfigKey, ThemeConfigKey} from "@/utils/ProvideKeys";
import {useStore} from "vuex";
import SysUtils from "@/utils/SysUtils.js";

dayjs.locale('zh-cn');

export default {
  name: 'App',
  components: {ThemeProvider},
  setup() {
    let theme = useIVTheme()
    /**
     * ivzone全局配置
     */
    let globalConfig = reactive({
      // 刷新功能
      reload (router, route) {
        let keepAlive = route.meta.keepAlive;
        route.meta.keepAlive = null;
        router.push('/refresh').then(() => {
          route.meta.keepAlive = keepAlive;
        })
      }
    });

    if(SysUtils.isLogin()) {
      // 初始化系统配置信息
      useStore().dispatch('sys/initConfig')
    }

    // 提供全局配置
    provide(GlobalConfigKey, globalConfig)

    // 提供主题配置
    provide(ThemeConfigKey, ThemeConfig)
    return {zhCN, theme, themeConfig: ThemeConfig}
  }
}
</script>

<style>
.iv-app {
  height: 100%;
}
.iv-theme-var {
  --header-height: 64px;
  --iv-color-tip: var(--ant-color-warning-active);
  --iv-color-split: var(--ant-line-width) var(--ant-line-type) var(--ant-color-split);
  --iv-theme-split: var(--ant-line-width) var(--ant-line-type) var(--ant-color-split);
  --divider-basic-color: var(--ant-color-split);
  --divider-color: var(--ant-color-bg-elevated);
  /*背景颜色为带透明的主颜色*/
  --iv-primary-color-bgt: color-mix(in srgb, var(--ant-color-primary), transparent 92%);
  --iv-color-bg-container: color-mix(in srgb, var(--ant-color-bg-container), transparent 20%);
  --iv-color-bgt-container: var(--ant-color-bg-elevated);
}
.iv-primary {
  --primary-radius: 8px;
  --primary-color: v-bind('themeConfig.colorPrimary');
  --primary-background-color: v-bind('themeConfig.colorBackgroundPrimary');
}

.iv-primary-divider {
  border-bottom: 1px solid var(--divider-color);
}

.iv-group-addon-padding-0 .ant-input-number-group-addon {
  padding: 0;
}

.ant-menu-inline-collapsed >.ant-menu-submenu>.ant-menu-submenu-title {
    padding-inline: calc(50% - 14px);
}
.iv-vls-device-group .ant-select-item-option-content{
  color: rgba(246, 247, 248, 0.6);
}
.iv-vls-device-group .ant-select-item-option-selected {
  background-color: rgba(252, 252, 253, 0.16) !important;
}
.ace_editor.ace_autocomplete {
  width: 500px;
}
#app {
  height: 100%;
  position: relative;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Drawer 打开时锁定容器滚动，关闭自动恢复, 防止抽屉打开时随下层容器滚动 */
.iv-task-container:has(.ant-drawer-open) {
  overflow: hidden !important;
}
</style>
