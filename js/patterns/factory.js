/**
 * ============================================
 * 设计模式：工厂模式 (Factory Pattern)
 * ============================================
 * 定义创建对象的接口，让子类决定实例化哪个类。
 * 本项目应用场景：
 *   - 根据课程类型（必修/选修/公选）创建不同的课程对象
 *   - 不同类型有不同默认属性和行为
 */

import { COURSE_META } from '../models/course.js';

/**
 * 课程工厂 — 根据类型创建课程对象
 */
export class CourseFactory {
  /**
   * 创建课程对象
   * @param {Object} data - 原始课程数据
   * @returns {Object} 加工后的课程对象
   */
  static createCourse(data) {
    const meta = COURSE_META[data.type] || COURSE_META['elective'];
    return {
      id: data.id,
      name: data.name,
      type: data.type,
      teacher: data.teacher,
      credits: data.credits,
      schedule: data.schedule,
      weekday: data.weekday,
      timeSlot: data.timeSlot,
      capacity: data.capacity,
      enrolled: data.enrolled,
      popularity: data.popularity,
      description: data.description,
      classroom: data.classroom,
      // 工厂根据类型添加额外属性
      typeLabel: meta.label,
      typeColor: meta.color,
      isRequired: data.type === 'required',
      canDrop: data.type !== 'required', // 必修课默认不可退
      defaultSelected: data.type === 'required', // 必修课默认选中
    };
  }

  /**
   * 批量创建课程
   * @param {Object[]} coursesData
   * @returns {Object[]}
   */
  static createCourses(coursesData) {
    return coursesData.map(data => this.createCourse(data));
  }
}

export default CourseFactory;
