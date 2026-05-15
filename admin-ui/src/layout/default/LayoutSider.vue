<template>
  <a-layout-sider class="iv-sider-menu" :width="238" breakpoint="lg" :trigger="null"
      v-model:collapsed="sys.collapsed" collapsible :collapsedWidth="68" :theme="themeConfig.theme.themeType">
    <!--侧边菜单-->
    <div class="u-sider-menu">
      <a-menu @select="selectMenu" :mode="menuMode" :openKeys="openKeys" :items="items" style="height: calc(100% - 0px); overflow: hidden auto;"
              v-model:selectedKeys="sys.selectedKeys" @openChange="openChange"
              :theme="themeConfig.theme.themeType" :getPopupContainer="getPopupContainer"/>
    </div>
  </a-layout-sider>
</template>

<script>
import {UIcon} from "@/components/icon";
import {computed, h, inject} from "vue";
import {mapGetters, mapMutations} from "vuex";
import {ThemeConfigKey} from "@/utils/ProvideKeys";
import store from "@/store";
import {MenuFoldOutlined, MenuUnfoldOutlined} from "@antdv-next/icons";

export default {
  name: "LayoutSider",
  setup() {
    let _store = store;
    let sys = store.state.sys;

    let resolveMenuItem = (menu) => {
      return {label: menu.name, key: menu['type'] == 'V' ? menu.url : menu.id, icon: () => h(UIcon, {type: menu.icon, class: "iv-menu-icon"})}
    }

    let resolveMenu = (menus, menuItems) => {
      menus.forEach((item, index) => {
        let menuItem = resolveMenuItem(item);
        if(item['type'] == 'M' || item['type'] == 'G') { // 目录或者分组
          menuItem['children'] = [];
          if(item['type'] == 'G') { // 如果是分组
            menuItem['type'] = "group";
          }

          if(item['children'] instanceof Array) {
            resolveMenu(item['children'], menuItem['children'])
          }
          menuItems.push(menuItem);
        } else if(item['type'] == 'V') { // 菜单
          menuItems.push(menuItem)
        }

        // 启用菜单项分割线
        if(index != menus.length - 1 && _store.state.menuDivider) {
          menuItems.push({type: 'divider', dashed: true})
        }
      })
    }
    let items = computed(() => {
      let menuItems = [];
      let menus = _store.getters['sys/menus'];
      if(menus && menus.length > 0) {
        resolveMenu(menus, menuItems);
      }
      return menuItems
    })
    let themeConfig = inject(ThemeConfigKey);
    return {themeConfig, sys, items}
  },
  components: {MenuFoldOutlined, MenuUnfoldOutlined},
  computed: {
    ...mapGetters({
      menus: 'sys/menus',
      sysName: 'sys/sysName',
      sysLogo: 'sys/sysLogo',
      menuMode: 'sys/menuMode',
      openKeys: 'sys/openKeys',
      collapsed: 'sys/collapsed',
      taskBarData: 'sys/taskBarData',
      sysMenuLevel: 'sys/sysMenuLevel',
      selectedKeys: 'sys/selectedKeys',
      urlMenuMaps: 'sys/urlMenuMaps'
    }),
  },
  methods: {
    ...mapMutations({
      pushAndSwitchTask: 'sys/pushAndSwitchTask',
      switchActiveMenuTo: 'sys/switchActiveMenuTo',
      switchOpenSubMenuTo: 'sys/switchOpenSubMenuTo'
    }),
    selectMenu(menu) {
      let urlMenu = this.urlMenuMaps[menu.key];
      if(urlMenu && urlMenu['target'] == '_blank') {
        let route = this.$router.resolve({path: menu.key});
        this.selectedKeys.length = 0;
        window.open(route.href, '_blank');
      } else {
        this.$router.push(menu.key).then(() => {
          this.switchActiveMenuTo(menu.key);
        }).catch(reason => console.error(`路由跳转失败[${reason}]`))
      }
    },
    getPopupContainer() {
      return document.getElementsByClassName('iv-sider-menu')[0];
    },
    openChange(openKeys) {
      if(this.sysMenuLevel == 'one') {
        const latestOpenKey = openKeys.find(key => this.openKeys.indexOf(key) === -1);
        if (!this.menus.find(item => item.id == latestOpenKey)) {
          this.switchOpenSubMenuTo(openKeys);
        } else {
          this.switchOpenSubMenuTo(latestOpenKey ? [latestOpenKey] : []);
        }
      } else {
        this.switchOpenSubMenuTo(openKeys);
      }
    },
  }
}
</script>

<style>
.iv-sider-menu {
  border-right: var(--iv-theme-split);
  background-color: var(--iv-layout-header-bg);
}
.iv-sider-menu .ant-menu-dark
, .iv-sider-menu .ant-menu-dark>.ant-menu
, .ant-menu-submenu.ant-menu-submenu-popup>.ant-menu {
  background-color: var(--iv-layout-header-bg);
}
.ant-menu-root.ant-menu-inline .ant-menu-sub.ant-menu-inline {
  background-color: var(--ant-menu-sub-menu-item-bg);
}
.ant-menu-root .ant-menu-item-selected {
  background-color: var(--iv-primary-color-bgt);
}
.ant-menu-root .ant-menu-item:hover {
  background-color: var(--ant-menu-item-hover-bg);
}
.iv-sider-menu .ant-layout-sider-children {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.iv-sider-menu .u-sider-menu {
  height: 100%;
}
/*侧边菜单栏滚动条样式*/
.iv-sider-menu .ant-layout-sider-collapsed .ant-menu-inline-collapsed {
  width: 72px;
}
</style>
