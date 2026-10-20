/**
 * 高德地图配置
 * @param {AMap}
 * @constructor
 */
export function AMap({key, center, city, security}) {
    this.key = key; // 高德api key
    this.city = city;
    this.center = center; // 中心点
    this.security = security; // 高德api密钥
}

/**
 * http配置
 * @param {Http}
 * @param apiUrl 后端请求接口地址(正式环境 = httpUrl)
 * @param httpUrl 页面访问地址
 * @param viewUrl 大屏页面访问地址
 * @param websocketUrl
 * @constructor
 */
export function Http({httpUrl, websocketUrl, apiUrl, websocketPort, viewUrl}) {
    this.httpUrl = httpUrl;
    this.viewUrl = viewUrl;
    this.websocketUrl = websocketUrl;
    this.isDev = import.meta.env.DEV;
    this.isProd = !this.isDev;

    if(!httpUrl.startsWith('http')) {
        this.httpUrl = window.location.origin + httpUrl;
    }

    if(!websocketUrl) {
        let hostname = window.location.hostname;
        let port = websocketPort || window.location.port;
        let scheme = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        this.websocketUrl = `${scheme}//${hostname}:${port}`
    }

    this.apiUrl = apiUrl || this.httpUrl;
    this.getApiUrl = function () {
        return this.apiUrl;
    }

    this.getBaseURI = function () {
        return this.httpUrl;
    }

    this.getFullURL = function (url) {
        return this.getBaseURI() + url || "";
    }

    this.getWsBaseURI = function () {
        return this.websocketUrl;
    }

    this.getFullWsURL = function (url) {
        return this.getWsBaseURI() + url || "";
    }

    this.getViewUrl = function () {
        return this.viewUrl;
    }
}

/**
 * @param config {EnvConfig}
 * @constructor
 */
export function EnvConfig(config) {

    /**
     * 是否开发环境
     * @type {boolean}
     */
    this.isDev = import.meta.env.DEV

    /**
     * 高德地图配置信息
     * @type {AMap}
     */
    this.amap = config.amap;

    /**
     * http接口配置
     * @type {Http}
     */
    this.http = config.http;

    /**
     * rtvs流媒体配置
     * @type {{port: number, host: null, type: string}}
     */
    this.rtvs = config.rtvs;
}