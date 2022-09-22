package com.iteaj.iboot.msn.iot.collect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectActionFactory {

    protected CollectActionFactory() { }

    private Map<String, CollectAction> actionMap = new ConcurrentHashMap<>(8);
    private static CollectActionFactory actionFactory = new CollectActionFactory();

    public static CollectActionFactory getInstance() {
        return actionFactory;
    }

    /**
     * 获取采集动作
     * @param actionName
     * @return
     */
    public CollectAction get(String actionName) {
        return actionMap.get(actionName);
    }

    /**
     * 是否有采集动作
     * @param actionName
     * @return
     */
    public boolean isExists(String actionName) {
        return actionMap.containsKey(actionName);
    }

    /**
     * 移除指定动作
     * @param actionName
     * @return
     */
    public CollectAction remove(String actionName) {
        return actionMap.remove(actionName);
    }

    /**
     * 注册采集动作
     * @param actionName
     * @param collectAction
     * @return
     */
    public CollectAction register(String actionName, CollectAction collectAction) {
        return actionMap.putIfAbsent(actionName, collectAction);
    }
}
