/**
 * 页面事件总线
 * <p>UView组件挂载/卸载时触发页面打开/关闭事件;
 * 本工具为页面基础设施, 不依赖任何业务模块, 无订阅方时事件静默丢弃, 不影响系统运行</p>
 */

/** 事件监听器注册表: event -> Set<fn> */
const listeners = new Map();

/**
 * 订阅页面事件
 * @param event {String} 事件名(如 page:opened / page:closed)
 * @param fn {Function} 监听回调, 入参为emit时传入的载荷
 */
export function onPageEvent(event, fn) {
    let set = listeners.get(event);
    if(!set) {
        set = new Set();
        listeners.set(event, set);
    }
    set.add(fn);
}

/**
 * 取消订阅
 * @param event {String} 事件名
 * @param fn {Function} 订阅时传入的回调
 */
export function offPageEvent(event, fn) {
    const set = listeners.get(event);
    if(set) {
        set.delete(fn);
    }
}

/**
 * 触发页面事件(无订阅方时静默丢弃)
 * @param event {String} 事件名
 * @param payload {*}
 */
export function emitPageEvent(event, payload) {
    const set = listeners.get(event);
    if(!set) {
        return;
    }
    set.forEach(fn => {
        try {
            fn(payload);
        } catch (e) {
            console.error(`页面事件[${event}]监听执行异常`, e);
        }
    });
}
