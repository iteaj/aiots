<template>
  <a-layout-header class="iv-header u-header-bar">
    <ARow class="iv-header-row" justify="space-between">
      <!--头部菜单-->
      <ACol class="u-header-col u-header-col-left" :xs="0" :sm="0" :md="6" :lg="6" :xl="6">
        <div class="u-logo">
          <a-avatar :size="36" :src="sysLogo"></a-avatar>
          <span class="u-logo-title">{{sysName}}</span>
        </div>
      </ACol>
      <ACol class="u-header-col" :xs="0" :sm="14" :md="14" :lg="10" :xl="10">
        <!-- 顶部一级导航菜单 -->
        <a-menu mode="horizontal" :selectedKeys="[activeTopMenu?.id]" :items="topMenuItems"
          @select="handleTopMenuSelect" class="u-top-nav-menu" />
      </ACol>
      <ACol class="u-header-col ivz-header-col-right" flex="auto" :xs="12" :sm="10" :md="6" :lg="8" :xl="8">
        <ASpace :size="0">
          <AModal class="iv-global-search" v-model:open="searchStatus" :closable="false" :footer="null">
            <template #title>
              <div style="font-size: 14px">
                <span>全局搜索</span>
                <span style="font-size: 12px; color: #d1d1d1; float: right; font-weight: normal;">快捷键(shift + shift)</span>
              </div>
            </template>
            <AInput placeholder="请输入要搜索的关键字" @change="searchKeywordHandle" autocomplete="off" />
            <div class="iv-search-content">
              <template v-for="item in searchResultModel" :key="item.key">
                <div class="iv-context-item" @click.native="() => searchRecordClickHandle(item)">
                  <span>{{item.label}}</span>
                  <span style="float: right; color: #959595; font-size: 12px">{{item.filter.name}}</span>
                </div>
              </template>
            </div>
            <a-divider dashed>
              <span style="font-size: 12px; color: #d1d1d1">搜索历史</span>
            </a-divider>
            <div class="iv-search-history">

            </div>
          </AModal>
          <a-tooltip title="全局搜索">
            <a-button @click="() => searchStatus = true" type="text">
              <template #icon>
                <SearchOutlined :style="{fontSize: '14px'}" />
              </template>
            </a-button>
          </a-tooltip>
          <a-tooltip title="全屏">
            <a-button @click="fullScreen" type="text">
              <template #icon>
                <FullscreenOutlined :style="{fontSize: '14px'}"/>
              </template>
            </a-button>
          </a-tooltip>
          <a-dropdown :menu="{items: themeMenus, multiple: true}" placement="bottom" @menuClick="switchTheme">
            <a-button type="text">
              <template #icon>
                <svg width="1em" height="1em" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true" focusable="false" class="">
                  <title>主题</title>
                  <g fill-rule="evenodd">
                    <g fill-rule="nonzero">
                      <path d="M7.02 3.635l12.518 12.518a1.863 1.863 0 010 2.635l-1.317 1.318a1.863 1.863 0 01-2.635 0L3.068 7.588A2.795 2.795 0 117.02 3.635zm2.09 14.428a.932.932 0 110 1.864.932.932 0 010-1.864zm-.043-9.747L7.75 9.635l9.154 9.153 1.318-1.317-9.154-9.155zM3.52 12.473c.514 0 .931.417.931.931v.932h.932a.932.932 0 110 1.864h-.932v.931a.932.932 0 01-1.863 0l-.001-.931h-.93a.932.932 0 010-1.864h.93v-.932c0-.514.418-.931.933-.931zm15.374-3.727a1.398 1.398 0 110 2.795 1.398 1.398 0 010-2.795zM4.385 4.953a.932.932 0 000 1.317l2.046 2.047L7.75 7 5.703 4.953a.932.932 0 00-1.318 0zM14.701.36a.932.932 0 01.931.932v.931h.932a.932.932 0 010 1.864h-.933l.001.932a.932.932 0 11-1.863 0l-.001-.932h-.93a.932.932 0 110-1.864h.93v-.931a.932.932 0 01.933-.932z"></path>
                    </g>
                  </g>
                </svg>
              </template>
            </a-button>
            <template #extraRender="item">
              <span v-if="themeConfig.themeType.indexOf(item.key) > -1" style="display: inline-block; width: 6px; height: 6px; border-radius: 50%; background-color: rgb(22, 119, 255);"></span>
            </template>
          </a-dropdown>
          <div class="u-opera-col">
            <a-dropdown placement="bottom">
              <a-avatar :src="$apiUrl + user.avatar" :size="32">
                <template #icon><UserOutlined /></template>
              </a-avatar>
              <template #popupRender>
                <a-menu @click="quickOpera">
                  <AMenuItem style="text-align: center; cursor: auto;">
                    <span class="iv-header-username">{{user.name}}</span>
                  </AMenuItem>
                  <a-menu-divider />
                  <a-menu-item key="profile">
                    <UIcon type="iz-icon-profile" style="font-size: 15px"/><span>&nbsp;个人资料</span>
                  </a-menu-item>
                  <a-menu-item key="pwd">
                    <LockFilled style="font-size: 14px"/><span>&nbsp;修改密码</span>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout">
                    <LogoutOutlined style="font-size: 14px" /><span>&nbsp;退出登录</span>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </ASpace>
      </ACol>
    </ARow>
  </a-layout-header>
</template>

<script>
import {logout} from "@/api";
import screenfull from 'screenfull'
import {msgError} from "@/utils/message";
import {mapGetters, mapMutations} from "vuex";
import {
  SyncOutlined,
  SunOutlined,
  MoonOutlined,
  CompressOutlined,
  UserOutlined,
  CheckOutlined,
  DownOutlined,
  FullscreenOutlined,
  HomeOutlined,
  LockFilled,
  LogoutOutlined,
  NotificationFilled,
  ReloadOutlined,
  LayoutOutlined,
  SearchOutlined,
} from '@antdv-next/icons'
import {inject, ref, h} from "vue";
import {UIcon} from "@/components/icon";
import {createStyles} from 'antdv-style'
import {ThemeConfigKey} from "@/utils/ProvideKeys";
import {themeMenus, registerThemeConfig, cartoon, illustration, useCartoonTheme, useIllustrationTheme} from "@/theme/index.js";

export default {
  name: "LayoutHeader",
  computed: {
    ...mapGetters({
      user: 'sys/user',
      sysName: 'sys/sysName',
      sysLogo: 'sys/sysLogo',
      taskBarData: 'sys/taskBarData',
      idMenuMaps: 'sys/idMenuMaps',
      selectedKeys: 'sys/selectedKeys',
      activityMenu: 'sys/activityMenu',
      activityView: 'sys/activityView',
      breadcrumbRoutes: 'sys/breadcrumbRoutes',
      searchResultModel: 'sys/getSearchResultModel',
      topMenus: 'sys/topMenus',
      activeTopMenu: 'sys/activeTopMenu',
    }),
    // 顶部导航菜单项
    topMenuItems() {
      return this.topMenus.map(menu => ({
        label: menu.name,
        key: menu.id,
        icon: menu.icon ? () => h(UIcon, {type: menu.icon, class: "iv-top-nav-icon"}) : undefined
      }));
    }
  },
  components: {
    ReloadOutlined, LockFilled, LogoutOutlined, DownOutlined, CheckOutlined, UserOutlined
    , HomeOutlined, NotificationFilled, FullscreenOutlined, LayoutOutlined, SearchOutlined
    , SyncOutlined, SunOutlined, MoonOutlined},
  setup() {
    let msgCount = 0
    let waitCount = 0;
    let workMenu = {};
    let searchStatus = ref(false);
    let themeConfig = inject(ThemeConfigKey);
    let colorData =themeConfig.candidateColorPrimary;

    // 注册其他主题
    const useStyles = createStyles(({ cssVar, css }) => {
      const sharedBorder = {
        border: `${cssVar.lineWidth}px ${cssVar.lineType} ${cssVar.colorBorder}`,
      }

      return {
        sharedBorder: css(sharedBorder),
        progressTrack: css({
          ...sharedBorder,
          marginInlineStart: `calc(-1 * ${cssVar.lineWidth}px)`,
          marginBlockStart: `calc(-1 * ${cssVar.lineWidth}px)`,
        }),
      }
    })
    const useIllustrationStyles = createStyles(({ css, cssVar }) => {
      const illustrationBorder = {
        border: `${cssVar.lineWidth} solid ${cssVar.colorBorder}`,
      }

      const illustrationBox = {
        ...illustrationBorder,
        boxShadow: `4px 4px 0 ${cssVar.colorBorder}`,
      }

      return {
        illustrationBorder: css(illustrationBorder),
        illustrationBox: css(illustrationBox),
        buttonRoot: css({
          ...illustrationBox,
          fontWeight: 600,
          textTransform: 'uppercase',
          letterSpacing: '0.5px',
        }),
        modalContainer: css({
          ...illustrationBox,
        }),
        tooltipRoot: css({
          padding: cssVar.padding,
        }),
        popupBox: css({
          ...illustrationBox,
          borderRadius: cssVar.borderRadiusLG,
          backgroundColor: cssVar.colorBgContainer,
        }),
        progressRail: css({
          border: `${cssVar.lineWidth} solid ${cssVar.colorBorder}`,
          boxShadow: `2px 2px 0 ${cssVar.colorBorder}`,
        }),
        progressTrack: css({
          border: 'none',
        }),
        inputNumberActions: css({
          width: '12px',
        }),
      }
    })
    registerThemeConfig(cartoon, useCartoonTheme(useStyles()));
    registerThemeConfig(illustration, useIllustrationTheme(useIllustrationStyles()));

    // 监听shift + shift
    let shiftCountRef = ref(0);
    document.addEventListener('keyup',(e)=>{
      if(e.key == 'Shift'){
        shiftCountRef.value = shiftCountRef.value + 1;
        if(shiftCountRef.value == 2) {
          searchStatus.value = true;
        } else {
          let timeout = setTimeout(() => {
            shiftCountRef.value = 0;
            clearTimeout(timeout);
          }, 250);
        }
      }
    })
    return {workMenu, msgCount, waitCount, themeConfig, colorData, searchStatus, themeMenus}
  },
  methods: {
    ...mapMutations({
      logout: 'sys/logout',
      removeTask: 'sys/removeTask',
      onGlobalSearch: 'sys/onGlobalSearch',
      toggleUserVisible: 'sys/toggleUserVisible',
      switchActiveViewTo: 'sys/switchActiveViewTo',
      pushAndSwitchTask: 'sys/pushAndSwitchTask',
      switchActiveTopMenu: 'sys/switchActiveTopMenu',
    }),
    switchTheme(menu) {
      this.themeConfig.switchTheme(menu.key)
    },
    fullScreen() {
      screenfull.toggle()
    },
    /**
     * 搜索关键字处理
     * @param value
     */
    searchKeywordHandle(value) {
      this.onGlobalSearch(value.target.value);
    },
    /**
     * 搜索结果记录单机处理
     * @param model {SearchResultModel}
     */
    searchRecordClickHandle(model) {
      model.filter.onClick(model).then(() => {
        this.searchStatus = false
      }).catch(reason => reason);
    },
    quickOpera({key}) {
      if(key == 'logout') {
        // 注销动作
        logout().then(({code, message}) => {
          if(code == 200) {
            // 跳转到登录页面
            this.$router.push('/login').then(resp=>{
              this.logout(); // 注销成功回调处理
            });
          } else {
            msgError(message);
          }
        }).catch(reason => console.error(reason))
        // 其他快速操作
        // this.toggleUserVisible({visible: true, key});
      } else if(key == 'profile' || key == 'pwd'){
        this.$router.push({path: '/profile', query: {key}});
      }
    },

    colorPrimaryChange(value) {
      this.themeConfig.switchColorPrimary(value)
    },

    headerColorPrimaryChange(value) {
      this.themeConfig.switchHeaderColorPrimary(value);
    },

    // 处理顶部菜单选择
    handleTopMenuSelect({key}) {
      const topMenu = this.topMenus.find(menu => menu.id === key);
      if (topMenu) {
        this.switchActiveTopMenu(topMenu);
      }
    }
  }
}
</script>

<style scoped>
.iv-header {
  //-webkit-backdrop-filter: blur(8px);
  //backdrop-filter: blur(8px);
  border-bottom: var(--iv-color-split);
  background-color: var(--iv-layout-header-bg);
}
.u-header-bar {
    height: var(--header-height);
    line-height: var(--header-height);
    padding-inline: 0;
}
.u-logo {
  height: var(--header-height);
  line-height: var(--header-height);
  padding: 0 13px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow:ellipsis;
  vertical-align: middle;
}
.u-logo-title {
  font-size: 18px;
  margin-left: 8px;
  vertical-align: bottom;
}
.iv-header-row {
  height: var(--header-height);
}

.iv-header-username {
  font-size: 15px!important;
}
.iv-global-search .iv-search-content {
  min-height: 80px;
  max-height: 180px;
  overflow-y: auto;
  margin-top: 8px;
  overflow-x: hidden;
}
.iv-global-search .iv-search-history {
  min-height: 120px;
}
.iv-search-content .iv-context-item {
  padding: 3px;
  cursor: pointer;
}
.iv-search-content .iv-context-item:hover {
  border-radius: 3px;
  background-color: #f9f9f9;
}
.u-opera-col {
  padding: 0px 12px;
}
.u-opera-col:hover {
  cursor: pointer;
  background-color: rgba(0, 0, 0, 0.06);
}
.u-header-col-left {
  text-align: left;
  max-width: 238px;
}
.ivz-header-col-right {
  right: 18px;
  text-align: right;
}
.ivz-header-col-right .anticon {
  font-size: 18px;
}
.ivz-header-col-right .u-opera-col {
  float: right;
}

/* 顶部导航栏样式 */
.u-header-col-center {
  display: flex;
  align-items: center;
  justify-content: center;
}
.u-top-nav-menu {
  background: transparent !important;
  border: none !important;
  line-height: var(--header-height) !important;
}
.u-top-nav-menu.ant-menu-horizontal {
  border-bottom: none !important;
}
.u-top-nav-menu .ant-menu-item {
  margin: 0 4px !important;
  padding: 0 16px !important;
  border: none !important;
}
.u-top-nav-menu .ant-menu-item-selected {
  background: transparent !important;
}
.u-top-nav-icon {
  font-size: 16px;
  margin-right: 6px;
}

</style>
