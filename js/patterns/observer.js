/**
 * ============================================
 * 设计模式：观察者模式 (Observer Pattern)
 * ============================================
 * 定义一对多依赖关系，当主题状态变化时，自动通知所有观察者。
 * 本项目应用场景：
 *   - 课程数据变更 → 自动刷新课程列表UI
 *   - 选课/退课成功 → 自动推送消息到聊天面板
 *   - 学分变化 → 自动更新底部状态栏
 */

/**
 * 事件总线 — 全局发布-订阅系统
 * 充当观察者模式中的「主题(Subject)」角色
 */
export class EventBus {
  constructor() {
    /** @type {Map<string, Set<Function>>} */
    this._listeners = new Map();
  }

  /**
   * 订阅事件
   * @param {string} event - 事件名称
   * @param {Function} callback - 回调函数
   * @returns {Function} 取消订阅函数
   */
  on(event, callback) {
    if (!this._listeners.has(event)) {
      this._listeners.set(event, new Set());
    }
    this._listeners.get(event).add(callback);

    // 返回取消订阅函数
    return () => this.off(event, callback);
  }

  /**
   * 取消订阅
   * @param {string} event - 事件名称
   * @param {Function} callback - 回调函数
   */
  off(event, callback) {
    const listeners = this._listeners.get(event);
    if (listeners) {
      listeners.delete(callback);
    }
  }

  /**
   * 发布事件（通知所有观察者）
   * @param {string} event - 事件名称
   * @param {*} data - 事件数据
   */
  emit(event, data) {
    const listeners = this._listeners.get(event);
    if (listeners) {
      listeners.forEach(callback => {
        try {
          callback(data);
        } catch (error) {
          console.error(`[EventBus] 事件 "${event}" 回调执行出错:`, error);
        }
      });
    }
  }

  /**
   * 订阅一次性事件（触发后自动取消订阅）
   * @param {string} event
   * @param {Function} callback
   */
  once(event, callback) {
    const wrapper = (data) => {
      callback(data);
      this.off(event, wrapper);
    };
    this.on(event, wrapper);
  }
}

export default EventBus;
