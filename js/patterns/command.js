/**
 * ============================================
 * 设计模式：命令模式 (Command Pattern)
 * ============================================
 * 将请求封装为对象，实现请求的参数化、队列化和可撤销。
 * 本项目应用场景：
 *   - AI助手意图解析后，封装为命令对象执行
 *   - 支持撤销操作（退课 = 撤销选课命令）
 *   - 命令历史记录
 */

/**
 * 命令基类 — 所有具体命令的抽象
 */
export class Command {
  /** 命令名称 */
  name = 'Command';

  /**
   * 执行命令
   * @returns {{ success: boolean, message: string, data?: any }}
   */
  execute() {
    throw new Error('子类必须实现 execute() 方法');
  }

  /**
   * 撤销命令（可选实现）
   * @returns {{ success: boolean, message: string }}
   */
  undo() {
    return { success: false, message: '该命令不支持撤销' };
  }
}

// ===================== 具体命令实现 =====================

/**
 * 搜索课程命令
 */
export class SearchCourseCommand extends Command {
  name = '搜索课程';

  /**
   * @param {import('../services/course-store.js').CourseStore} store
   * @param {string} keyword
   */
  constructor(store, keyword) {
    super();
    this.store = store;
    this.keyword = keyword;
  }

  execute() {
    const results = this.store.searchCourses(this.keyword);
    if (results.length === 0) {
      return { success: true, message: `未找到与"${this.keyword}"相关的课程，请尝试其他关键词。`, data: [] };
    }
    const list = results.map(c => `📚 ${c.id} ${c.name} - ${c.teacher} | ${c.credits}学分 | ${c.schedule}`).join('\n');
    return {
      success: true,
      message: `找到 ${results.length} 门相关课程：\n${list}`,
      data: results
    };
  }
}

/**
 * 选课命令
 */
export class SelectCourseCommand extends Command {
  name = '选课';

  /**
   * @param {import('../services/course-store.js').CourseStore} store
   * @param {import('../services/scheduler.js').Scheduler} scheduler
   * @param {string} courseId
   */
  constructor(store, scheduler, courseId) {
    super();
    this.store = store;
    this.scheduler = scheduler;
    this.courseId = courseId;
  }

  execute() {
    const course = this.store.getCourseById(this.courseId);
    if (!course) {
      return { success: false, message: `课程 ${this.courseId} 不存在，请检查课程编号。` };
    }

    // 检查是否已选
    if (this.store.isSelected(this.courseId)) {
      return { success: false, message: `你已经选过《${course.name}》了，无需重复选择。` };
    }

    // 检查名额
    if (course.enrolled >= course.capacity) {
      return { success: false, message: `《${course.name}》已满员（${course.enrolled}/${course.capacity}），请选择其他课程。` };
    }

    // 检查时间冲突
    const conflict = this.scheduler.checkConflict(this.courseId);
    if (conflict) {
      return {
        success: false,
        message: `《${course.name}》(${course.schedule})与已选课程《${conflict.name}》(${conflict.schedule})时间冲突！`
      };
    }

    // 执行选课
    this.store.selectCourse(this.courseId);
    return {
      success: true,
      message: `✅ 成功选课：《${course.name}》！\n📅 ${course.schedule} | 🏫 ${course.classroom} | 👨‍🏫 ${course.teacher} | 📊 ${course.credits}学分`,
      data: course
    };
  }

  undo() {
    this.store.dropCourse(this.courseId);
    return { success: true, message: `已撤销选课：${this.courseId}` };
  }
}

/**
 * 退课命令
 */
export class DropCourseCommand extends Command {
  name = '退课';

  /**
   * @param {import('../services/course-store.js').CourseStore} store
   * @param {string} courseId
   */
  constructor(store, courseId) {
    super();
    this.store = store;
    this.courseId = courseId;
  }

  execute() {
    const course = this.store.getCourseById(this.courseId);
    if (!this.store.isSelected(this.courseId)) {
      return { success: false, message: `你尚未选择课程 ${this.courseId}，无法退课。` };
    }
    this.store.dropCourse(this.courseId);
    return {
      success: true,
      message: `已成功退选《${course ? course.name : this.courseId}》。`,
      data: course
    };
  }

  undo() {
    this.store.selectCourse(this.courseId);
    return { success: true, message: `已恢复选课：${this.courseId}` };
  }
}

/**
 * 推荐课程命令
 */
export class RecommendCommand extends Command {
  name = '推荐课程';

  /**
   * @param {import('../services/course-store.js').CourseStore} store
   * @param {import('./strategy.js').RecommendStrategy} strategy
   */
  constructor(store, strategy) {
    super();
    this.store = store;
    this.strategy = strategy;
  }

  execute() {
    const available = this.store.getAvailableCourses();
    if (available.length === 0) {
      return { success: true, message: '所有可选课程都已选满，暂时没有可推荐的课程。' };
    }
    const recommended = this.strategy.recommend(available);
    const list = recommended.map((c, i) =>
      `${i + 1}. 📚 ${c.id} 《${c.name}》- ${c.teacher} | ${c.credits}学分 | ${c.schedule} | ⭐${c.popularity}`
    ).join('\n');
    return {
      success: true,
      message: `💡 为你推荐以下 ${recommended.length} 门课程（${this.strategy.name}）：\n${list}`,
      data: recommended
    };
  }
}

/**
 * 查看已选课程命令
 */
export class QuerySelectedCommand extends Command {
  name = '查看已选';

  /**
   * @param {import('../services/course-store.js').CourseStore} store
   */
  constructor(store) {
    super();
    this.store = store;
  }

  execute() {
    const selected = this.store.getSelectedCourses();
    if (selected.length === 0) {
      return { success: true, message: '你还没有选择任何课程。输入"推荐课程"让我帮你推荐吧~' };
    }
    const totalCredits = selected.reduce((sum, c) => sum + c.credits, 0);
    const list = selected.map((c, i) =>
      `${i + 1}. 📚 ${c.id} 《${c.name}》| ${c.schedule} | ${c.credits}学分 | ${c.teacher}`
    ).join('\n');
    return {
      success: true,
      message: `📋 你已选择 ${selected.length} 门课程，共 ${totalCredits} 学分：\n${list}\n\n📊 学分统计：${totalCredits}/25（毕业要求25学分）`,
      data: selected
    };
  }
}

/**
 * 查询课表命令
 */
export class QueryScheduleCommand extends Command {
  name = '课表查询';

  /**
   * @param {import('../services/course-store.js').CourseStore} store
   * @param {number|null} weekday
   */
  constructor(store, weekday = null) {
    super();
    this.store = store;
    this.weekday = weekday;
  }

  execute() {
    const selected = this.store.getSelectedCourses();
    if (selected.length === 0) {
      return { success: true, message: '你还没有选课，无法查看课表。' };
    }

    const weekNames = ['', '周一', '周二', '周三', '周四', '周五'];
    let courses = selected;

    if (this.weekday !== null) {
      courses = selected.filter(c => c.weekday === this.weekday);
      if (courses.length === 0) {
        return { success: true, message: `${weekNames[this.weekday]}没有课程安排~ 🎉` };
      }
      const list = courses
        .sort((a, b) => parseInt(a.timeSlot) - parseInt(b.timeSlot))
        .map(c => `📚 ${c.id} 《${c.name}》${c.timeSlot}节 | ${c.classroom} | ${c.teacher}`)
        .join('\n');
      return { success: true, message: `📅 ${weekNames[this.weekday]}课表：\n${list}` };
    }

    // 显示整周课表
    const byDay = {};
    for (let d = 1; d <= 5; d++) {
      byDay[d] = courses.filter(c => c.weekday === d)
        .sort((a, b) => parseInt(a.timeSlot) - parseInt(b.timeSlot));
    }

    let schedule = '';
    for (let d = 1; d <= 5; d++) {
      schedule += `\n📅 ${weekNames[d]}：`;
      if (byDay[d].length === 0) {
        schedule += ' 无课程';
      } else {
        byDay[d].forEach(c => {
          schedule += `\n   ${c.timeSlot}节 《${c.name}》${c.classroom}`;
        });
      }
    }
    return { success: true, message: `📅 你的本周课表：${schedule}` };
  }
}

/**
 * 课程详情命令
 */
export class CourseDetailCommand extends Command {
  name = '课程详情';

  /**
   * @param {import('../services/course-store.js').CourseStore} store
   * @param {string} courseId
   */
  constructor(store, courseId) {
    super();
    this.store = store;
    this.courseId = courseId;
  }

  execute() {
    const c = this.store.getCourseById(this.courseId);
    if (!c) {
      return { success: false, message: `未找到课程 ${this.courseId}。` };
    }
    const selected = this.store.isSelected(this.courseId);
    const remaining = c.capacity - c.enrolled;
    return {
      success: true,
      message: [
        `📚 ${c.id} 《${c.name}》`,
        `👨‍🏫 授课教师：${c.teacher}`,
        `📊 学分：${c.credits}`,
        `📅 时间：${c.schedule}`,
        `🏫 教室：${c.classroom}`,
        `👥 名额：${c.enrolled}/${c.capacity}（剩余${remaining}个）`,
        `⭐ 评分：${'⭐'.repeat(Math.floor(c.popularity))}${c.popularity}`,
        `📝 类型：${c.type === 'required' ? '必修' : c.type === 'elective' ? '选修' : '公选'}`,
        `📖 简介：${c.description}`,
        `📌 状态：${selected ? '✅ 已选' : '未选'}`,
      ].join('\n'),
      data: c
    };
  }
}

/**
 * 命令调用器 — 管理命令的执行和历史
 */
export class CommandInvoker {
  constructor() {
    /** @type {Command[]} 命令历史 */
    this._history = [];
    /** @type {Command[]} 撤销历史 */
    this._undoStack = [];
  }

  /**
   * 执行命令
   * @param {Command} command
   * @returns {{ success: boolean, message: string, data?: any }}
   */
  execute(command) {
    const result = command.execute();
    if (result.success) {
      this._history.push(command);
      this._undoStack = []; // 新命令清空重做栈
    }
    return result;
  }

  /**
   * 撤销最后一个命令
   * @returns {{ success: boolean, message: string }}
   */
  undo() {
    if (this._history.length === 0) {
      return { success: false, message: '没有可撤销的操作。' };
    }
    const command = this._history.pop();
    const result = command.undo();
    if (result.success) {
      this._undoStack.push(command);
    }
    return result;
  }

  /**
   * 获取命令历史
   */
  getHistory() {
    return [...this._history];
  }
}

export default Command;
