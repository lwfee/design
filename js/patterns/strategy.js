/**
 * ============================================
 * 设计模式：策略模式 (Strategy Pattern)
 * ============================================
 * 定义一系列算法，把它们封装起来，使它们可以相互替换。
 * 本项目应用场景：
 *   - AI助手根据用户不同需求切换推荐策略
 *   - 热度优先 / 学分均衡 / 时间分散 三种策略
 */

/**
 * 推荐策略抽象基类
 */
export class RecommendStrategy {
  name = '基础推荐';

  /**
   * 执行推荐
   * @param {Object[]} courses - 可选课程列表
   * @returns {Object[]} 推荐结果（前5门）
   */
  recommend(courses) {
    throw new Error('子类必须实现 recommend() 方法');
  }
}

/**
 * 热度优先策略 — 按课程评分从高到低推荐
 */
export class PopularFirstStrategy extends RecommendStrategy {
  name = '热度优先策略';

  recommend(courses) {
    return [...courses]
      .sort((a, b) => b.popularity - a.popularity)
      .slice(0, 5);
  }
}

/**
 * 学分均衡策略 — 按学分从低到高推荐（帮助学生平衡学业负担）
 */
export class CreditBalanceStrategy extends RecommendStrategy {
  name = '学分均衡策略';

  recommend(courses) {
    return [...courses]
      .sort((a, b) => a.credits - b.credits || b.popularity - a.popularity)
      .slice(0, 5);
  }
}

/**
 * 时间匹配策略 — 优先推荐时间不冲突的课程
 */
export class TimeMatchStrategy extends RecommendStrategy {
  name = '时间匹配策略';

  /**
   * @param {Object[]} courses
   * @param {Object[]} [selectedCourses] - 已选课程（用于检测冲突）
   */
  recommend(courses, selectedCourses = []) {
    const busySlots = new Set(
      selectedCourses.map(c => `${c.weekday}-${c.timeSlot}`)
    );

    // 不冲突的排前面
    return [...courses]
      .map(c => ({
        ...c,
        _conflict: busySlots.has(`${c.weekday}-${c.timeSlot}`)
      }))
      .sort((a, b) => {
        // 不冲突 > 冲突
        if (a._conflict !== b._conflict) return a._conflict ? 1 : -1;
        // 冲突相同时按热度排
        return b.popularity - a.popularity;
      })
      .slice(0, 5);
  }
}

/**
 * 策略上下文 — 管理当前使用的推荐策略
 */
export class RecommendContext {
  constructor() {
    /** @type {RecommendStrategy} */
    this._strategy = new PopularFirstStrategy();
  }

  /**
   * 设置策略
   * @param {RecommendStrategy} strategy
   */
  setStrategy(strategy) {
    this._strategy = strategy;
  }

  /**
   * 获取当前策略名称
   */
  getStrategyName() {
    return this._strategy.name;
  }

  /**
   * 执行推荐
   * @param {Object[]} courses
   * @param {Object[]} [selectedCourses]
   * @returns {Object[]}
   */
  recommend(courses, selectedCourses = []) {
    return this._strategy.recommend(courses, selectedCourses);
  }
}

export default RecommendContext;
