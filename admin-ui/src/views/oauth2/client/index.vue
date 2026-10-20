<script>
import {useRoute, useRouter} from "vue-router";
import CoreConsts from "@/components/CoreConsts.js";
import SysUtils from "@/utils/SysUtils.js";
import {POST} from "@/utils/request.js";
import {ref} from "vue";

export default {
  name: "OAuth2Client",
  setup() {
    // header存在token, 将他保存到Storage
    let route = useRoute();
    let router = useRouter();
    let query = route.params;
    let config = {};
    let result = ref({status: 'info', title: '系统授权中...', subTitle: '授权成功, 即将跳转...'});
    config[CoreConsts.CancelRespResolver] = true;
    POST("/oauth2/client/login", query, config).then(({data: {code, message}, headers}) => {
      if(code == CoreConsts.SuccessCode) {
        // header存在token, 将他保存到Storage
        let accessToken = headers[CoreConsts.AccessToken];
        if(accessToken) {
          localStorage.setItem(CoreConsts.AccessToken, accessToken)
          SysUtils.setCookie(CoreConsts.AccessToken, accessToken, "/")
        } else { // 移除存储在本地的token
          localStorage.removeItem(CoreConsts.AccessToken);
          accessToken = SysUtils.getCookie(CoreConsts.AccessToken);
        }
        result.value = {status: 'success', title: '授权成功', subTitle: '系统已授权成功, 即将跳转到首页...'};
        const userInfo = {userToken: accessToken, tokenName: CoreConsts.AccessToken}
        const systemStore = {}; systemStore[CoreConsts.UserInfo] = userInfo;
        localStorage.setItem(CoreConsts.SystemStore, JSON.stringify(systemStore))

        router.push("/");
      } else {
        result.value = {status: '500', title: '授权登录失败', subTitle: message};
      }
    })
    let redirectLogin = () => {
      router.push("/login");
    }

    return {result, redirectLogin}
  }
}
</script>

<template>
  <a-result :status="result.status" :title="result.title" :sub-title="result.subTitle">
    <template #extra>
      <a-button v-if="result.status === 'error'" type="danger" @click="redirectLogin" >
        重新授权
      </a-button>
    </template>
  </a-result>
</template>

<style scoped>

</style>