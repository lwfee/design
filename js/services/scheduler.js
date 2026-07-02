/**
 * 排课服务 — 时间冲突检测
 */

import Singleton from '../patterns/singleton.js';
import CourseStore from './course-store.js';

/**
 * 排课器 — 单例模式
 * 负责检测课程时间冲突
 */
export class Scheduler extends Singleton {
  constructor() {
    super();
    /** @type {CourseStore} */
    this.store = CourseStore.getInstance();
  }

  /**
   * 检查某门课是否与已选课程时间冲突
   * @param {string} courseId - 待检查的课程ID
   * @returns {Object|null} 冲突的课程对象，无冲突返回null
   */
  checkConflict(courseId) {
    const course = this.store.getCourseById(courseId);
    if (!course) return null;

    const selected = this.store.getSelectedCourses();
    for (const sc of selected) {
      if (sc.id === courseId) continue;
      if (sc.weekday === course.weekday && sc.timeSlot === course.timeSlot) {
        return sc;
      }
    }
    return null;
  }

  /**
   * 获取某天的课程安排
   * @param {number} weekday - 1-5（周一到周五）
   * @returns {Object[]}
   */
  getDaySchedule(weekday) {
    return this.store.getSelectedCourses()
      .filter(c => c.weekday === weekday)
      .sort((a, b) => parseInt(a.timeSlot) - parseInt(b.timeSlot));
  }

  /**
   * 获取整周课程安排
   * @returns {Object} {1: [...], 2: [...], ...}
   */
  getWeekSchedule() {
    const schedule = {};
    for (let d = 1; d <= 5; d++) {
      schedule[d] = this.getDaySchedule(d);
    }
    return schedule;
  }
}

export default Scheduler;
