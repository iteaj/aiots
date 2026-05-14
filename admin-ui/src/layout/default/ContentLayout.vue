<template>
  <a-layout class="iv-layout-default">
    <!--侧边菜单栏-->
    <LayoutSider />
    <!--右侧视图页-->
    <a-layout-content class="u-main-container">
      <!--  任务栏  -->
      <TaskBar></TaskBar>
      <div class="u-main-task">
        <div class="iv-task-container">
          <router-view v-slot="{ Component }">
            <transition name="slide-fade" mode="out-in">
              <keep-alive :include="alive">
                <component :is="Component" />
              </keep-alive>
            </transition>
          </router-view>
        </div>
      </div>

      <!-- 用户中心 -->
      <UserCenter />

    </a-layout-content>
  </a-layout>
</template>
<script>
import LayoutSider from "@/layout/default/LayoutSider.vue";
import UserCenter from "@msn/main/UserCenter.vue";
import TaskBar from "@/layout/default/TaskBar.vue";
import {mapGetters} from "vuex";

export default {
  name: "DefaultLayout",
  components: {TaskBar, UserCenter, LayoutSider},
  computed: {
    ...mapGetters({
      theme: 'sys/theme',
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
<style scoped>

</style>