package com.iteaj.iboot.plugin.gbs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaInfo {

    private String type;

    private String host;

    private Integer port;

    public MediaInfo(String type, String host, Integer port) {
        this.type = type;
        this.host = host;
        this.port = port;
    }

    public static MediaInfo buildRtvs(String host, Integer port) {
        return new MediaInfo("rtvs", host, port);
    }

    public static MediaInfo buildWvp(String host, Integer port) {
        return new MediaInfo("wvp", host, port);
    }
}
