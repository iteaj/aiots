package com.iteaj.framework.spi.ai.vision.model;

/**
 * 通用二维坐标点
 */
public class Point {

    /** X 坐标（像素） */
    private float x;

    /** Y 坐标（像素） */
    private float y;

    public Point() {}

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 获取 X 坐标
     */
    public float getX() {
        return x;
    }

    public Point setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * 获取 Y 坐标
     */
    public float getY() {
        return y;
    }

    public Point setY(float y) {
        this.y = y;
        return this;
    }
}
