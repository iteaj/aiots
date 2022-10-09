package com.iteaj.iboot.msn.iot.collect;

import com.iteaj.iboot.msn.iot.collect.action.CollectAction;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CollectActionFactory implements InitializingBean {

    @Autowired(required = false)
    private List<CollectAction> actions;

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
     * 返回所有动作名称
     * @return
     */
    public Set<String> getNames() {
        return this.actionMap.keySet();
    }

    /**
     * 返回所有的采集动作
     * @return
     */
    public Collection<CollectAction> getActions() {
        return this.actionMap.values();
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

    /**
     * 注册采集动作
     * @param collectAction
     * @return
     */
    public CollectAction register(CollectAction collectAction) {
        return actionMap.putIfAbsent(collectAction.getName(), collectAction);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!CollectionUtils.isEmpty(actions)) {
            this.actions.forEach(item -> {
                if(!this.isExists(item.getName())) {
                    this.register(item);
                } else {
                    throw new BeanInitializationException("存在相同的采集动作["+item.getName()+"]");
                }
            });
        }
    }
}
