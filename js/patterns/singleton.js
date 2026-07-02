/**
 * ============================================
 * 设计模式：单例模式 (Singleton Pattern)
 * ============================================
 * 确保一个类只有一个实例，并提供全局访问点。
 * 本项目应用场景：
 *   - EventBus: 全局唯一事件总线
 *   - CourseStore: 全局唯一数据仓库
 *   - AIAssistant: 全局唯一AI助手
 */

export class Singleton {
  /**
   * 获取单例实例（子类通过此方法获取唯一实例）
   * @returns {Singleton}
   */
  static getInstance() {
    if (!this._instance) {
      this._instance = new this();
    }
    return this._instance;
  }

  /**
   * 重置单例（主要用于测试）
   */
  static resetInstance() {
    this._instance = null;
  }
}

export default Singleton;
