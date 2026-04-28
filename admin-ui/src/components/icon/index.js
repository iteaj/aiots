import {createFromIconfontCN} from '@ant-design/icons-vue';

let baseurl = import.meta.env.BASE_URL;
baseurl = baseurl.endsWith("/") ? baseurl.substring(0, baseurl.length - 1) : baseurl;
// 项目中使用到的图标
const UIcon = createFromIconfontCN({
    extraCommonProps: {style: {fontSize: '16px'}},
    scriptUrl: `${baseurl}/icon/iconfont.js`,
});

export default {
    install(app) {
        app.component("UIcon", UIcon);
    }
}

export {UIcon}
