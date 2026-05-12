<template>
  <a-layout-header class="iv-header u-header-bar">
    <ARow class="iv-header-row" justify="space-between">
      <!--头部菜单-->
      <ACol class="u-header-col u-header-col-left" :xs="0" :sm="0" :md="8" :lg="6" :xl="8">
        <div class="u-logo">
          <a-avatar :size="36" :src="sysLogo"></a-avatar>
          <span class="u-logo-title">{{sysName}}</span>
        </div>
      </ACol>
      <ACol :xs="12" :sm="12" :md="8" :lg="9" :xl="10">
        <span>sdf</span>
      </ACol>
      <ACol class="u-header-col ivz-header-col-right" flex="auto" :xs="12" :sm="12" :md="8" :lg="10" :xl="8">
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
          <a-dropdown :menu="{items: themes, multiple: true}" placement="bottom" @menuClick="switchTheme">
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
          <a-tooltip title="风格配置" class="u-opera-col">
            <div class="u-opera-col" @click="themeConfig['switchOpen']">
              <LayoutOutlined style="font-size: 14px"/>
            </div>
          </a-tooltip>
        </ASpace>
      </ACol>
    </ARow>
    <a-drawer v-model:open="themeConfig['open']" placement="right" :closable="false">
      <a-divider dashed orientation="left">系统风格</a-divider>
      <div style="margin: 8px 0px 24px 0px; display: flex">
        <ATooltip title="纯白风格" class="iv-style-item iv-style-item-light">
          <div @click="() => themeConfig.switchStyle('light')">
            <CheckOutlined class="iv-style-item-checkbox-icon" v-show="themeConfig.style == 'light'"/>
          </div>
        </ATooltip>
        <ATooltip title="暗黑风格" class="iv-style-item iv-style-item-dark">
          <div @click="() => themeConfig.switchStyle('dark')">
            <CheckOutlined class="iv-style-item-checkbox-icon" v-show="themeConfig.style == 'dark'"/>
          </div>
        </ATooltip>
      </div>
      <a-divider dashed orientation="left">
        系统主题颜色
      </a-divider>
      <div>
        <a-segmented v-model:value="themeConfig.colorPrimary" :options="themeConfig.candidateColorPrimary" @change="colorPrimaryChange">
          <template #label="{value}">
            <span :style="{background: value, color: '#ffffff', padding: '2px', borderRadius: '3px'}">{{value}}</span>
          </template>
        </a-segmented>
        <h4 style="margin-top: 36px">
          顶栏应用主题色：
          <a-switch v-model:checked="themeConfig.headerColorPrimary" @change="headerColorPrimaryChange"/>
        </h4>
      </div>
    </a-drawer>
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
import {inject, reactive, ref} from "vue";
import {GlobalConfigKey, ThemeConfigKey} from "@/utils/ProvideKeys";

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
    })
  },
  components: {
    ReloadOutlined, LockFilled, LogoutOutlined, DownOutlined, CheckOutlined, UserOutlined
    , HomeOutlined, NotificationFilled, FullscreenOutlined, LayoutOutlined, SearchOutlined
    , SyncOutlined, SunOutlined, MoonOutlined},
  setup() {
    let msgCount = 0
    let waitCount = 0;
    let workMenu = {};
    let themes = [
      {key: 'auto', label: '跟随系统', icon: SyncOutlined},
      {key: 'light', label: '浅色主题', icon: SunOutlined},
      {key: 'dark', label: '暗色主题', icon: MoonOutlined},
      {type: 'divider'},
      {key: 'compact', label: '紧凑主题', icon: CompressOutlined},
    ]
    let searchStatus = ref(false);
    let themeConfig = inject(ThemeConfigKey);
    let colorData =themeConfig.candidateColorPrimary;

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
    return {workMenu, msgCount, waitCount, themeConfig, colorData, searchStatus, themes}
  },
  methods: {
    ...mapMutations({
      logout: 'sys/logout',
      removeTask: 'sys/removeTask',
      onGlobalSearch: 'sys/onGlobalSearch',
      toggleUserVisible: 'sys/toggleUserVisible',
      switchActiveViewTo: 'sys/switchActiveViewTo',
      pushAndSwitchTask: 'sys/pushAndSwitchTask',
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
    }
  }
}
</script>

<style scoped>
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
.iv-style-item-checkbox-icon {
  position: absolute;
  right: 8px;
  bottom: 8px;
  color: #1890ff;
  font-weight: 700;
  font-size: 14px;
  pointer-events: none;
}
.iv-style-item {
  position: relative;
  width: 44px;
  height: 36px;
  margin-right: 16px;
  overflow: hidden;
  background-color: #ebeef1;
  border-radius: 2px;
  box-shadow: 0 1px 2.5px #0000002e;
  cursor: pointer;
}
.iv-style-item:before {
  position: absolute;
  top: 0;
  left: 0;
  width: 33%;
  height: 100%;
  background-color: #fff;
  content: "";
}
.iv-style-item-dark {
  z-index: 1;
  content: "";
  background-color: #ebeef1;
}
.iv-style-item-dark:before {
  z-index: 1;
  background-color: #001529;
  content: "";
}
.iv-style-item-dark:after {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 25%;
  background-color: #fff;
  content: "";
}
.iv-style-item-light:before {
  content: "";
  background-color: #fff;
}
.iv-style-item-light:after {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 25%;
  background-color: #fff;
  content: "";
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
  right: 8px;
  text-align: right;
}
.ivz-header-col-right .anticon {
  font-size: 18px;
}
.ivz-header-col-right .u-opera-col {
  float: right;
}

</style>
