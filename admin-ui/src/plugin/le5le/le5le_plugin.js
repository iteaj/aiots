import {message, GET, POST, Env, CoreConsts} from '@/plugin/common/common_plugin.js'
const msgWarn = message.warn
const msgError = message.error
const msgSuccess = message.success
const msgLoading = message.loading
let reconnectTimer;
let reconnectInterval = 1000; // 初始重连间隔为 1 秒
const maxReconnectInterval = 60000; // 最大重连间隔为 60 秒

function getLe5LeConfig() {
    let le5leConfig = window['le5le_config'];
    if(!le5leConfig) {
        console.error("未初始化乐吾乐插件[执行init_le5le_plugin或init_le5le_plugin_2d]"); return {};
    }

    return le5leConfig;
}

function handleEchartsData(meta2d, data, echarts, pen, config, type) {
    let datum = data[0];
    if (datum) {
        // 获取图表类型
        let type = echarts.option.series[0].type;
        if(type == 'pie') { // 饼图
            let value = [];
            data.forEach((item, index) => {
                let itemValue = item['data'][0].value;
                value[index] = {name: item.field, value: itemValue};
            })

            meta2d.setValue({id: pen.id, 'echarts.option.series.0.data': value});
        } else if(type == 'gauge') { // 仪表盘
            // 仪表盘不做处理
        } else {
            let timeItem = datum.data.map(valueItem => valueItem['category']);
            if (echarts.option.xAxis.type == 'category') {
                meta2d.setValue({id: pen.id, 'echarts.option.xAxis.data': timeItem});
            } else if (echarts.option.yAxis.type == 'category') {
                meta2d.setValue({id: pen.id, 'echarts.option.yAxis.data': timeItem});
            }

            echarts.option.series.forEach((item, index) => {
                let valueKey = `echarts.option.series.${index}.data`;
                let value = {id: pen.id};
                if (data[index]) {
                    value[valueKey] = data[index].data.map(valueItem => valueItem['value']);
                    meta2d.setValue(value)
                } else {
                    console.warn(``);
                }
            })
        }
    } else {
        console.error("没有返回任何数据")
    }
}

function fetchEchartsData(meta2d, pen, echarts, config) {
    let apiUrl = getLe5LeConfig().url || Env.http.getApiUrl();
    if(config.type == "data") { // 统计采集的数据
        POST(`${apiUrl}/iot/le5le/api/data`, config).then(({data, message, code}) => {
            if(code == CoreConsts.SuccessCode) {
                handleEchartsData(meta2d, data, echarts, pen, config);
            } else {
                console.error(message);
            }
        });
    } else if(config.type == "warn") { // 统计告警数据
        POST(`${apiUrl}/iot/le5le/api/warn`, config).then(({data, message, code}) => {
            if(code == CoreConsts.SuccessCode) {
                handleEchartsData(meta2d, data, echarts, pen, config);
            } else {
                console.error(message);
            }
        });
    } else if(config.type == "status") {
        POST(`${apiUrl}/iot/le5le/api/status`, config).then(({data, message, code}) => {
            if(code == CoreConsts.SuccessCode) {
                handleEchartsData(meta2d, data, echarts, pen, config);
            } else {
                console.error(message);
            }
        });
    } else {
        console.error("不支持的统计类型["+config.type+"]");
    }
}

function initRealtimeDataConnect(meta2d, bind, le5leConfig) {
    let wsURL = le5leConfig.wsUrl || Env.http.getWsBaseURI();
    let webSocket = new WebSocket(wsURL + "/ws/iot/scada");

    // 连接成功后的回调函数
    webSocket.onopen = function (params) {
        reconnectInterval = 1000;
        if(reconnectTimer) {
            clearTimeout(reconnectTimer);
        }

        let keys = Object.keys(bind);
        // 发送注册信息
        webSocket.send(keys.join(','));
    };

    // 监听连接关闭事件
    webSocket.onclose = function (event) {
        // 使用指数退避算法设置重连间隔
        reconnectInterval = Math.min(reconnectInterval * 2, maxReconnectInterval);
        console.error('WebSocket 连接已关闭, 尝试重连...', event.code, event.reason, `重连时间${reconnectInterval}秒`);
        reconnectTimer = setTimeout(() => initRealtimeDataConnect(meta2d, bind, le5leConfig), reconnectInterval);
    };

    webSocket.onerror = function (e) {
        webSocket.close(); // 关闭连接以触发重连
    }

    webSocket.onmessage = function ({data}) {
        let parse = JSON.parse(data);
        if(parse instanceof Array && parse.length > 0) {
            parse.forEach(value => {
                value['dataId'] = value['id']
                if(meta2d['canvasType'] !== '2d') {
                    meta2d.setValue(value, {render: false});
                } else {
                    meta2d.setValue({dataId: value.id, value: value.value}, {render: false});
                }
            })

            meta2d.render();
        }
    }
}

/**
 * 初始化乐吾乐插件
 * @param meta2d
 */
window.init_le5le_plugin = function (meta2d, config) {
    meta2d = meta2d || window.meta2d;
    let {bind, pens} = meta2d.store;
    Object.values(pens).forEach(pen => {
        let echarts = pen['echarts'];
        if(echarts && echarts['iboot']) {
            // 加载图表数据
            fetchEchartsData(pen, echarts, echarts['iboot']);
        }
    });

    let le5leConfig = window['le5le_config'] = {token: null, url: null, wsUrl: null};
    if(config instanceof Object) {
        Object.assign(le5leConfig, config);
    }

    // 初始化websocket连接
    initRealtimeDataConnect(meta2d, bind, le5leConfig);
}

window.init_le5le_plugin_2d = function (meta2d, config) {
    meta2d = meta2d || window.meta2d;
    meta2d['canvasType'] = '2d';
    let {bindDatas} = meta2d.store;

    let le5leConfig = window['le5le_config'] = {token: null, url: null, wsUrl: null};
    if(config instanceof Object) {
        Object.assign(le5leConfig, config);
    }

    // 初始化websocket连接
    initRealtimeDataConnect(meta2d, bindDatas, le5leConfig);
}

/**
 * 调用设备指令集接口
 * @param e
 * @param cmdCode 指令码
 */
window.invoke_device_cmd = function (e, cmdCode) {
    if(!cmdCode) {
        return alert("未指定调用指令码");
    }

    if(e['input'] === true || e['checked'] != undefined) { // 输入框类型
        let startIndex = cmdCode.indexOf("{");
        let endIndex = cmdCode.indexOf("}");
        if(startIndex > 0 && endIndex > startIndex) {
            cmdCode = cmdCode.replace(/\{value\}/, e.text)
        }
    }

    let uid = e['deviceId'];
    let messageType = msgLoading("指令执行中...", 0);
    let requestConfig = {};

    let le5leConfig = getLe5LeConfig();
    if(le5leConfig instanceof Object && le5leConfig.token) {
        requestConfig.headers = {};
        requestConfig.headers[CoreConsts.AccessToken] = le5leConfig.token;
    }

    let apiUrl = le5leConfig.url || Env.http.getApiUrl();
    POST(`${apiUrl}/iot/le5le/api/cmd`, {code: cmdCode, uid}, requestConfig).then(({code, message, data}) => {
        if(code !== CoreConsts.SuccessCode) {
            msgError(message).then(() => {});
        } else {
            msgSuccess(message).then(() => {});
        }
    }).catch(reason => {
        msgError("执行指令失败").then(() => {});
    }).finally(messageType)
}

/**
 * 更新画笔属性
 * @param pen
 * @param attrName
 * @param value
 */
window.update_pen_attr = function (pen, attrName, value) {
    let penValue = {id: pen.id};
    penValue[attrName] = value;
    window.meta2d.setValue(penValue);
}

/**
 * 更新画笔tag
 * @param tag
 * @param attrName
 * @param value
 */
window.update_pen_tag = function (tag, attrName, value) {
    let penValue = {tag};
    penValue[attrName] = value;
    window.meta2d.setValue(penValue);
}

/**
 * 更新物模型绑定
 * @param model 模型属性
 * @param value 属性值
 * @param type 图纸类型 2d,v
 */
window.update_pen_model = function (model, value, type) {
    if(type == '2d') {
        window.meta2d.setValue({dataId: model, value});
    } else if(type == 'v') {
        window.meta2d.setValue({id: model, value});
    }
}