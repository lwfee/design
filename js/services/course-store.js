/**
 * ============================================
 * 设计模式：外观模式 (Facade Pattern)
 * ============================================
 * 为子系统中的一组接口提供统一的高层接口，简化使用。
 * 本项目应用场景：
 *   - CourseStore 封装 localStorage 操作
 *   - 对外提供简洁API，隐藏底层存储细节
 */

import Singleton from '../patterns/singleton.js';
import { PRESET_COURSES } from '../models/course.js';
import CourseFactory from '../patterns/factory.js';

const STORAGE_KEY_COURSES = 'smart_course_all';
const STORAGE_KEY_SELECTED = 'smart_course_selected';

/**
 * 课程数据仓库 — 外观模式 + 单例模式
 */
export class CourseStore extends Singleton {
  constructor() {
    super();
    /** @type {Object[]} 全部课程 */
    this._courses = [];
    /** @type {Set<string>} 已选课程ID集合 */
    this._selectedIds = new Set();
    this._init();
  }

  /**
   * 初始化 — 从localStorage加载或使用预置数据
   */
  _init() {
    // 加载课程库
    const stored = localStorage.getItem(STORAGE_KEY_COURSES);
    if (stored) {
      this._courses = JSON.parse(stored);
    } else {
      // 首次使用，用工厂模式创建课程并保存
      this._courses = CourseFactory.createCourses(PRESET_COURSES);
      this._saveCourses();
    }

    // 加载已选列表
    const selectedStored = localStorage.getItem(STORAGE_KEY_SELECTED);
    if (selectedStored) {
      this._selectedIds = new Set(JSON.parse(selectedStored));
    }

    // 每次启动同步必修课的默认选中状态
    this._syncRequiredCourses();
  }

  /**
   * 同步必修课默认选中状态
   */
  _syncRequiredCourses() {
    const requiredIds = this._courses
      .filter(c => c.type === 'required')
      .map(c => c.id);
    let changed = false;
    requiredIds.forEach(id => {
      if (!this._selectedIds.has(id)) {
        this._selectedIds.add(id);
        changed = true;
      }
    });
    if (changed) this._saveSelected();
  }

  /** 保存课程库到localStorage */
  _saveCourses() {
    localStorage.setItem(STORAGE_KEY_COURSES, JSON.stringify(this._courses));
  }

  /** 保存已选列表到localStorage */
  _saveSelected() {
    localStorage.setItem(STORAGE_KEY_SELECTED, JSON.stringify([...this._selectedIds]));
  }

  // ==================== 公开API（外观接口） ====================

  /**
   * 获取所有课程
   * @returns {Object[]}
   */
  getAllCourses() {
    return [...this._courses];
  }

  /**
   * 根据ID获取课程
   * @param {string} id
   * @returns {Object|undefined}
   */
  getCourseById(id) {
    return this._courses.find(c => c.id === id);
  }

  /**
   * 搜索课程（按名称、教师、ID模糊匹配）
   * @param {string} keyword
   * @returns {Object[]}
   */
  searchCourses(keyword) {
    const kw = keyword.toLowerCase();
    return this._courses.filter(c =>
      c.id.toLowerCase().includes(kw) ||
      c.name.toLowerCase().includes(kw) ||
      c.teacher.toLowerCase().includes(kw)
    );
  }

  /**
   * 获取可选课程（未满员的非必修课）
   * @returns {Object[]}
   */
  getAvailableCourses() {
    return this._courses.filter(c => c.enrolled < c.capacity && !this._selectedIds.has(c.id));
  }

  /**
   * 获取已选课程列表
   * @returns {Object[]}
   */
  getSelectedCourses() {
    return this._courses.filter(c => this._selectedIds.has(c.id));
  }

  /**
   * 判断课程是否已选
   * @param {string} id
   * @returns {boolean}
   */
  isSelected(id) {
    return this._selectedIds.has(id);
  }

  /**
   * 选课
   * @param {string} id
   */
  selectCourse(id) {
    const course = this.getCourseById(id);
    if (!course) return false;
    this._selectedIds.add(id);
    course.enrolled++;
    this._saveSelected();
    this._saveCourses();
    return true;
  }

  /**
   * 退课
   * @param {string} id
   */
  dropCourse(id) {
    const course = this.getCourseById(id);
    if (!course) return false;
    this._selectedIds.delete(id);
    if (course.enrolled > 0) course.enrolled--;
    this._saveSelected();
    this._saveCourses();
    return true;
  }

  /**
   * 获取已选课程总学分
   * @returns {number}
   */
  getTotalCredits() {
    return this.getSelectedCourses().reduce((sum, c) => sum + c.credits, 0);
  }

  /**
   * 按类型筛选课程
   * @param {string} type
   * @returns {Object[]}
   */
  getCoursesByType(type) {
    if (!type || type === 'all') return this.getAllCourses();
    return this._courses.filter(c => c.type === type);
  }

  /**
   * 重置所有数据（用于演示）
   */
  reset() {
    localStorage.removeItem(STORAGE_KEY_COURSES);
    localStorage.removeItem(STORAGE_KEY_SELECTED);
    this._selectedIds.clear();
    this._init();
  }
}

export default CourseStore;
