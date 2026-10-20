export class QueryFileConfig {
    /**
     * @param startTime {dayjs} 开始时间，utc时间
     * @param endTime {dayjs} 结束时间，utc时间
     * @param alarm {String} 报警标志（bit0~bit31见JT/T 808-2011表18报警标志位定义；bit32~bit64见表13；全0表示无报警类型条件）
     * @param mediaType {Number} 音视频资源类型（0：音视频，1：音频，2：视频，3：视频或音视频）
     * @param streamType {Number} 码流类型（0：所有码流，1：主码流，2：子码流）
     * @param storageType {Number} 存储器类型（0：所有存储器，1：主存储器，2：灾备存储器）
     * @param dataSource {Number} 0自动 1设备 2服务端缓存
     * @param protocol 协议版本 0.808-2013 1.808-2019 2.GB28181
     */
    constructor(protocol, startTime, endTime, alarm='0', mediaType=0, streamType=0, storageType=0, dataSource=0) {
        this.alarm = alarm;
        this.endTime = endTime;
        this.protocol = protocol;
        this.startTime = startTime;
        this.mediaType = mediaType;
        this.streamType = streamType;
        this.storageType = storageType;
        this.dataSource = dataSource;
    }
}

/**
 * 播放实时视频接口配置
 */
export class RealTimeVideoConfig {
    /**
     * @param protocol 协议版本 0.808-2013 1.808-2019 2.GB28181
     * @param extra 额外的配置
     * @param streamType 主子码流 0 主码流 1 子码流
     * @param hasAudio 是否打开音频
     * @param playerMode 播放模式 0 自动 1 WASM软解(canvas+audioAPI) 2 js封装FMP4(h264Demuxer+audioAPI) 3 WASM封装FMP4(WASM2FMP4) 4 服务器推fmp4流 5 webrtc 6 hls 7 webrtc(srs) <hr>
     *     模式1 2 3已经停止更新，新版本可能存在不兼容情况，不推荐使用
     */
    constructor(protocol, extra = {}, streamType = 1, hasAudio = true, playerMode = 0) {
        this.extra = extra;
        this.hasAudio = hasAudio;
        this.protocol = protocol;
        this.streamType = streamType;
        this.playerMode = playerMode;
    }
}

export class PlaybackVideoConfig {
    /**
     * @param startTime 开始时间，utc时间
     * @param endTime 结束时间，utc时间
     * @param mediaType {Number} 音视频资源类型（0：音视频，1：音频，2：视频，3：视频或音视频）
     * @param streamType {Number} 码流类型（0：所有码流，1：主码流，2：子码流）
     * @param storageType {Number} 存储器类型（0：所有存储器，1：主存储器，2：灾备存储器）
     * @param playbackMode 回放模式（0：正常回放，1：快进回放，2：关键帧快退回放，3：关键帧播放，4：单帧上传）
     * @param multiple 倍数（0：无效，1：1倍，2：2倍，3：4倍，4：8倍，5：16倍）
     * @param playerMode 播放模式
     * @param dataSource {Number} 0自动 1设备 2服务端缓存
     * @param protocol 协议版本 0.808-2013 1.808-2019 2.GB28181
     */
    constructor(protocol, startTime, endTime, mediaType=0, streamType=0, storageType=0
                , playbackMode = 0, multiple = 1, playerMode = 0, dataSource=0, extra = {}) {
        this.extra = extra;
        this.endTime = endTime;
        this.protocol = protocol;
        this.multiple = multiple;
        this.startTime = startTime;
        this.mediaType = mediaType;
        this.playerMode = playerMode;
        this.streamType = streamType;
        this.storageType = storageType;
        this.playbackMode = playbackMode;
        this.dataSource = dataSource;
    }
}

/**
 * @param protocol 协议版本 0.808-2013 1.808-2019 2.GB28181
 * @param startTime 开始时间，utc时间
 * @param endTime 结束时间，utc时间
 * @param mediaType {Number} 音视频资源类型（0：音视频，1：音频，2：视频，3：视频或音视频）
 * @param streamType {Number} 码流类型（0：所有码流，1：主码流，2：子码流）
 * @param storageType {Number} 存储器类型（0：所有存储器，1：主存储器，2：灾备存储器）
 * @param playbackMode 回放模式（0：正常回放，1：快进回放，2：关键帧快退回放，3：关键帧播放，4：单帧上传）
 * @param multiple 倍数（0：无效，1：1倍，2：2倍，3：4倍，4：8倍，5：16倍）
 * @param dataSource {Number} 0自动 1设备 2服务端缓存
 * @param speed {Number} 下载倍速(必须大于0，1为正常倍速，支持小数)，仅28181有效
 */
export class DownloadFmp4Config {
    constructor(protocol, startTime, endTime, mediaType=0, streamType=0, storageType=0
                , playbackMode = 0, multiple = 1, dataSource=0, speed = 1) {
        this.endTime = endTime;
        this.protocol = protocol;
        this.multiple = multiple;
        this.startTime = startTime;
        this.mediaType = mediaType;
        this.streamType = streamType;
        this.storageType = storageType;
        this.playbackMode = playbackMode;
        this.dataSource = dataSource;
        this.speed = speed;
    }
}
