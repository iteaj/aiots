// 注意： 本文接口均为 ICC 接口， 接口需要三方后端转发使用，每个方法对请求进行改写，固定格式请看注释


// 跟平台相关的业务JS
import {POST} from "@/utils/request";
import CoreConsts from "@/components/CoreConsts";
import {msgError} from "@/utils/message";

const API = {
    getRealmonitor(realmonitorParam) {
        return new Promise((resolve,reject) => {
            API.ajaxRequest({ // 发起请求
                url: '/evo-apigw/admin/API/MTS/Video/StartVideo', // 请求后端的接口url
                data: realmonitorParam,
            }).then(res => {
                resolve(res.data)
                // res.data = {
                //     "minRate": null,
                //     "protocol": null,
                //     "ip": null,
                //     "port": null,
                //     "stunEnable": null,
                //     "stunPort": null,
                //     "url": "rtsp://124.160.33.135:4079/dss/monitor/param/cameraid=1003085%242%26substream=2",
                //     "urlList": null,
                //     "connectType": null,
                //     "session": "843",
                //     "token": "843",
                //     "trackId": "",
                //     "stream": null,
                //     "innerIp": "124.160.33.135",
                //     "compress": false,
                //     "reachable": null,
                //     "wssDirect": 1
                // }
            }).catch(res => {
                reject(res)
            })
        })
    },
    /**
     * 获取对讲的rtsp
     * @returns Promise<>
    */
    getTalkRtsp(talkPram) {
        return new Promise((resolve,reject) => {
            API.ajaxRequest({
                url: '/evo-apigw/admin/API/MTS/Audio/StartTalk',
                data: talkPram,
            }).then(res => {
                resolve(res.data)
                // res.data = {
                //     "deviceCode": "1003085",
                //     "url": "rtsp://124.160.33.135:4079/talk/pu/17740",
                //     "token": "14",
                //     "session": "17740",
                //     "audioType": "1",
                //     "audioBit": "16",
                //     "sampleRate": "8000",
                //     "protocolType": "2",
                //     "innerIp": "124.160.33.135",
                //     "stream": 524288,
                //     "reachable": null,
                //     "wssDirect": 0,
                //     "gbDevice": 0
                // },
            }).catch(res => {
                reject(res)
            })
        })
    },
    /**
     * 根据时间查询设备上的录像
     * @param recordByTimeParam.recordType 录像类型：1=一般录像，2=报警录像
     * @param recordByTimeParam.recordSource 录像来源：1=全部，2=设备，3=中心
     * @param recordByTimeParam.streamType 码流类型：1=主码流, 2=辅码流 使用辅码流 码率低更加流畅
     * @param recordByTimeParam.channelId 通道code
     * @param recordByTimeParam.startTime 开始时间
     * @param recordByTimeParam.endTime 结束时间
     */
    getRecordRtspByTime(recordByTimeParam) {
        return new Promise((resolve,reject) => {
            API.ajaxRequest({
                url: '/evo-apigw/admin/API/SS/Playback/StartPlaybackByTime',
                data: recordByTimeParam,
            }).then(res => {
                resolve(res.data)
                // res.data = {
                //     "url": "rtsp://124.160.33.135:9322/playback/pu/125",
                //     "session": "125",
                //     "protocol": "0",
                //     "token": "125",
                //     "stream": null,
                //     "innerIp": "124.160.33.135",
                //     "compress": false,
                //     "reachable": null,
                //     "wssDirect": 1,
                //     "fromDevice": ""
                // },
            }).catch(res => {
                reject(res)
            })
        })
    },
    stopTalk(recordByTimeParam) {
        return new Promise((resolve,reject) => {
            API.ajaxRequest({
                url: '/evo-apigw/admin/API/MTS/Audio/StopTalk',
                data: recordByTimeParam,
            }).then(res => {
                resolve(res.data)
            }).catch(res => {
                reject(res)
            })
        })
    },

    /**
     * 根据文件形式回放录像
     * @param recordByFileParam.recordType 录像类型：1=一般录像，2=报警录像
     * @param recordByFileParam.streamType 码流类型：1=主码流, 2=辅码流 使用辅码流 码率低更加流畅
     * @param recordByFileParam.recordSource 录像来源：1=全部，2=设备，3=中心
     * @param recordByFileParam.channelId 通道code
     * @param recordByFileParam.startTime 开始时间 timestamp到秒
     * @param recordByFileParam.endTime 结束时间 timestamp到秒
    */
    getRecordRtspByFile(recordByFileParam) {
        return new Promise((resolve, reject) => {
            API.ajaxRequest({
                url: '/evo-apigw/admin/API/SS/Playback/StartPlaybackByFile',
                data: recordByFileParam,
            }).then(res => {
                resolve(res.data)
                // res.data = {
                //     "url": "rtsp://124.160.33.135:9322/playback/pu/125",
                //     "session": "125",
                //     "protocol": "0",
                //     "token": "125",
                //     "stream": null,
                //     "innerIp": "124.160.33.135",
                //     "compress": false,
                //     "reachable": null,
                //     "wssDirect": 1,
                //     "fromDevice": ""
                // },
            }).catch(res => {
                reject(res)
            })
        })
    },
    /**
     *  查询中心录像上的录像文件信息
     * @param recordsParam.recordType 录像类型：1=一般录像，2=报警录像
     * @param recordsParam.streamType 码流类型：1=主码流, 2=辅码流 使用辅码流 码率低更加流畅
     * @param recordsParam.recordSource 录像来源：1=全部，2=设备，3=中心
     * @param recordsParam.channelId 通道code
     * @param recordsParam.startTime 开始时间 timestamp到秒
     * @param recordsParam.endTime 结束时间 timestamp到秒
     */
    getRecords(recordsParam) {
        return new Promise((resolve,reject) => {
            API.ajaxRequest({
                url: '/evo-apigw/admin/API/SS/Record/QueryRecords',
                data: recordsParam,
            }).then(res => {
                resolve(res.data)
                // res.data = {
                //     接口返回的 records 数组
                //     records: [
                //         {...}
                //     ]
                // }
            }).catch(res => {
                reject(res)
            })
        })
    },
    /**
     * 云台方向控制
     * @param directionParam
     * @returns {Promise<unknown>}
     */
    setPtzDirection(directionParam) {
        return new Promise((resolve,reject) => {
            API.ajaxRequest({
                url: '/evo-apigw/admin/API/DMS/Ptz/OperateDirect',
                data: directionParam,
            }).then(res => {
                resolve(res.data)
            }).catch(res => {
                reject(res)
            })
        })
    },
    /**
     * 云台镜头控制
     * @param cameraParam
     * @returns {Promise<unknown>}
     */
    setPtzCamera(cameraParam) {
        return new Promise((resolve,reject) => {
            API.ajaxRequest({
                url: '/evo-apigw/admin/API/DMS/Ptz/OperateCamera',
                data: cameraParam,
            }).then(res => {
                resolve(res.data)
            }).catch(res => {
                reject(res)
            })
        })
    },
    /**
     * 云台三维定位接口
     * @param positionParam

     * @returns Promise<>
    */
    controlSitPosition(positionParam) {
        return new Promise((resolve,reject) => {
            API.ajaxRequest({
                url: '/evo-apigw/admin/API/DMS/Ptz/SitPosition',
                data: positionParam,
            }).then(res => {
                resolve(res.data)
            }).catch(res => {
                reject(res)
            })
        })
    },
    /**
     * 请求
     * @param {*} opt
     * @param {*} opt.url 接口地址
     * @param {*} opt.data 参数

     * 集成后，该方法请删除
     */
    ajaxRequest(opt) {
        return POST("/iot/dahua/getStream", opt).then(resp => {
            if(resp.code != CoreConsts.SuccessCode) {
                msgError(resp.message);
            }
            return resp;
        });
    },
}
export default API
