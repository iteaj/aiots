package com.iteaj.framework.script;

/**
 * 脚本自动补全提示内容
 */
public class ScriptComplete {

    /**
     * 提示内容
     */
    private String value;

    /**
     * 文字说明
     */
    private String meta;

    /**
     * 提示权重
     */
    private int score;

    public ScriptComplete(String value, String meta, int score) {
        this.value = value;
        this.meta = meta;
        this.score = score;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
