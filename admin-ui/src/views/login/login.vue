<template>
  <div class="iz-login iz-login-bg">
    <div class="iz-login-card">
      <a-card :bordered="false" @keyup.enter.native="submit()">
        <img slot="title" src="/img/login_logo.png" style="margin-bottom: 12px"/>
        <a-form layout="horizontal" ref="formRef" :model="user" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
          <a-form-item has-feedback name="username">
            <a-input placeholder="请输入用户名" size="large" v-model:value="user.username">
              <template #prefix>
                <UIcon type="iz-icon-account" />
              </template>
            </a-input>
          </a-form-item>
          <a-form-item has-feedback name="password">
            <a-input placeholder="请输入密码" type="password" size="large" v-model:value="user.password">
              <template #prefix>
                <UIcon type="iz-icon-password" />
              </template>
            </a-input>
          </a-form-item>
          <a-form-item name="captcha">
            <a-input placeholder="请输入验证码" size="large" v-model:value="user.captcha"
                     class="ivz-form-valid">
              <UIcon slot="prefix" type="iz-icon-validate"></UIcon>
            </a-input>
            <img v-if="loginModel.captchaImg" @click="clickImg" :src="loginModel.captchaImg" class="ivz-valid-img"/>
          </a-form-item>
          <a-form-item style="margin-bottom: 2px">
            <a-button type="primary" size="large" shape="round" block
                      @click="submit" :loading="loginModel.loading">
              登 录
            </a-button>
          </a-form-item>
        </a-form>
        <div style="text-align: center; padding-top: 3px">
          <span :class="loginModel.class">{{loginModel.errMsg}}</span>
        </div>
        <a-divider style="margin: 12px 0px">其他登录方式</a-divider>
        <a-row type="flex" justify="space-between" :gutter="3">
          <a-col span="6" style="text-align: right; cursor: pointer">
<!--            <a-icon type="wechat" :style="iconStyle" />-->
          </a-col>
          <a-col span="6" style="text-align: center; cursor: pointer">
            <UIcon type="iz-icon-gitee" style="font-size: 28px"
                      @click="oauth2Login('Gitee')"></UIcon>
          </a-col>
          <a-col span="6" style="text-align: left; cursor: pointer">
<!--            <a-icon type="alipay-circle" :style="iconStyle" />-->
          </a-col>
        </a-row>
      </a-card>

    </div>
    <div style="position: absolute; bottom: 0px; text-align: center; width: 100%">
      Copyright © 2020-2022 iteaj.com All Rights Reserved.
    </div>
  </div>
</template>
<script>
import {reactive, shallowRef} from 'vue'
import {getCode, login, oauth2, captchaUri} from '@/api'
import CoreConsts from "@/components/CoreConsts";
import SysUtils from "@/utils/SysUtils";

export default {
  name: 'login',
  data () {
    return {
      form: null,
      iconStyle: {fontSize: '36px', cursor: 'pointer'},
    }
  },
  setup() {
    let labelCol = {span: 0};
    let wrapperCol = {span: 24};
    const user = reactive({
      code: null,
      captcha: null,
      username: 'iteaj',
      password: 'iteaj123456'
    });
    const rules = reactive({
      captcha: [{type: 'string', required: true, message: ''}],
      username: [{type: 'string', required: true, message: ''}],
      password: [{type: 'string', required: true, message: ''}],
    });

    const loginModel = reactive({
      loading: false,
      errMsg: '',
      izCaptcha: false,
      captchaImg: null,
      class: 'iz-login-tip-error'
    });
    const formRef = shallowRef()
    const validate = () => {
      return formRef.value.validate();
    };
    const validateInfos = () => {
      return formRef.value.validateInfos;
    }
    const clickImg = () => {
      getCode().then(({message, code, data}) => {
        if(code == CoreConsts.SuccessCode) {
          user.code = data;
          loginModel.captchaImg = captchaUri + '?code=' + data;
        } else {
          loginModel.errMsg = message;
        }
      })
    }

    clickImg();
    return {user, labelCol, wrapperCol, validateInfos, loginModel, clickImg, validate, formRef, rules}
  },
  methods: {
    oauth2Login(type) {
      oauth2(type)
    },
    submit() {
      this.validate().then(res => {
        this.loginModel.loading = true;
        login(this.user).then(({data: {code, message}, headers})=>{
          if(code == CoreConsts.SuccessCode) {
            // header存在token, 将他保存到Storage
            let accessToken = headers[CoreConsts.AccessToken];
            if(accessToken) {
              localStorage.setItem(CoreConsts.AccessToken, accessToken)
            } else { // 移除存储在本地的token
              localStorage.removeItem(CoreConsts.AccessToken);
              accessToken = SysUtils.getCookie(CoreConsts.AccessToken);
            }

            // 兼容iotview
            const userInfo = {userToken: accessToken, tokenName: CoreConsts.AccessToken}
            const systemStore = {}; systemStore[CoreConsts.UserInfo] = userInfo;
            localStorage.setItem(CoreConsts.SystemStore, JSON.stringify(systemStore))
            let formUrl = this.$route.query['form'];
            if(formUrl) {
              if(formUrl.indexOf('#') > -1) {
                location.href = formUrl;
              } else {
                this.$router.push(formUrl).finally(() => { });
              }
            } else {
              this.$router.push("/").finally(() => { });
            }
          } else {
            this.clickImg();
            this.loginModel.errMsg = message;
          }
        }).catch(reason => {
          this.clickImg();
        }).finally(() => this.loginModel.loading = false);
      })
    }
  }
}
</script>
<style>
.iz-login-bg {
  width: 100%;
  height: 100%;
  overflow-y: hidden;
  background-attachment: fixed;
  background-repeat: no-repeat;
  background-size: cover;
  background-image: url("/img/login.jpg");
}
.iz-login-card {
  top: 18%;
  right: 10%;
  width: 342px;
  position: absolute;
}
.iz-login .ant-input {
  padding-left: 12px!important;
}
.iz-login .ant-input.ant-input-lg {
  border-radius: 20px;
}
.iz-login .ivz-form-valid {
  width: 158px;
  text-align: center;
}
.iz-login .ivz-valid-img {
  height: 40px;
  width: 118px;
  cursor: pointer;
  border-radius: 6px;
  margin-left: 16px;
  vertical-align: top;
}
.iz-login-card .ant-card {
  background:  color-mix(in srgb, var(--iv-color-bgt-container), transparent 68%);
  -webkit-backdrop-filter: blur(6px);
  backdrop-filter: blur(6px);                   /* 核心：背后区域模糊10像素 */
}
.iz-login-card .ant-card-body {
  padding: 8px 24px 16px;
  text-align: center;
  border-radius: 12px;
}
.iz-login-card .ant-card-body {

}
.iz-login-card .ant-card-head {
  line-height: 48px;
  background-color: #ffffffd4;
  border-radius: 16px 16px 0px 0px;
}
.iz-login-card .ant-card-head-title {
  padding: 0px;
  text-align: center;
}
.iz-login-tip {
  text-align: center;
}
.iz-login-tip-error {
  color: #ed4014;
}
.iz-login-tip-success {
  color: #19be6b;
}
</style>
