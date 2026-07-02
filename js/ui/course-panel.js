/**
 * 选课面板UI组件
 *
 * 职责：
 *   - 渲染课程列表（卡片形式）
 *   - 渲染已选课程列表
 *   - 搜索和筛选功能
 *   - 选课/退课按钮交互
 *   - 统计栏更新
 *
 * 使用观察者模式：订阅AI助手事件，自动更新
 */

import CourseStore from '../services/course-store.js';
import Scheduler from '../services/scheduler.js';
import AIAssistant from '../services/ai-assistant.js';

export class CoursePanel {
  constructor() {
    /** @type {CourseStore} */
    this.store = CourseStore.getInstance();
    /** @type {Scheduler} */
    this.scheduler = Scheduler.getInstance();
    /** @type {AIAssistant} */
    this.assistant = AIAssistant.getInstance();

    // DOM引用
    this.courseListEl = document.getElementById('course-list');
    this.selectedListEl = document.getElementById('selected-list');
    this.searchInput = document.getElementById('course-search');
    this.typeFilter = document.getElementById('type-filter');
    this.statCredits = document.getElementById('stat-credits');
    this.statCount = document.getElementById('stat-count');

    // 搜索状态
    this._searchResults = null;
    this._searchMode = false;

    this._bindEvents();
    this._subscribeToEvents();
    this.render();
  }

  /** 绑定UI事件 */
  _bindEvents() {
    // 搜索框
    this.searchInput.addEventListener('input', () => {
      this._searchMode = !!this.searchInput.value.trim();
      this.render();
    });

    // 类型筛选
    this.typeFilter.addEventListener('change', () => {
      this.render();
    });

    // 重置按钮
    document.getElementById('btn-reset')?.addEventListener('click', () => {
      if (confirm('确定要重置所有数据吗？这将清除选课记录并恢复初始状态。')) {
        this.store.reset();
        this.render();
      }
    });
  }

  /**
   * 订阅AI助手事件（观察者模式）
   * 当选课/退课操作发生后，自动刷新UI
   */
  _subscribeToEvents() {
    const eventBus = this.assistant.getEventBus();

    // 监听课程变更事件
    eventBus.on('course_changed', (data) => {
      this.render();
      if (data.action === 'select') {
        this._highlightCourse(data.courseId);
      }
    });

    // 监听搜索结果事件（AI聊天触发搜索时高亮结果）
    eventBus.on('search_result', (data) => {
      if (data.courses && data.courses.length > 0) {
        this._searchResults = data.courses;
        this._searchMode = true;
        this.searchInput.value = data.keyword || '';
        this.render();
      }
    });
  }

  /** 高亮指定课程 */
  _highlightCourse(courseId) {
    setTimeout(() => {
      const card = document.querySelector(`[data-course-id="${courseId}"]`);
      if (card) {
        card.classList.add('highlight');
        card.scrollIntoView({ behavior: 'smooth', block: 'center' });
        setTimeout(() => card.classList.remove('highlight'), 2000);
      }
    }, 100);
  }

  /**
   * 获取当前应该显示的课程列表
   * @returns {Object[]}
   */
  _getDisplayCourses() {
    let courses;

    // 搜索模式
    if (this._searchMode && this._searchResults) {
      courses = this._searchResults;
    } else if (this._searchMode) {
      const kw = this.searchInput.value.trim().toLowerCase();
      courses = this.store.searchCourses(kw);
    } else {
      courses = this.store.getAllCourses();
    }

    // 类型筛选
    const typeFilterVal = this.typeFilter.value;
    if (typeFilterVal && typeFilterVal !== 'all') {
      courses = courses.filter(c => c.type === typeFilterVal);
    }

    return courses;
  }

  /** 主渲染方法 */
  render() {
    this._renderCourseList();
    this._renderSelectedList();
    this._renderStats();
  }

  /** 渲染课程列表 */
  _renderCourseList() {
    const courses = this._getDisplayCourses();
    this.courseListEl.innerHTML = '';

    if (courses.length === 0) {
      this.courseListEl.innerHTML = '<div class="empty-state">📭 没有找到匹配的课程</div>';
      return;
    }

    courses.forEach(course => {
      const isSelected = this.store.isSelected(course.id);
      const conflict = !isSelected ? this.scheduler.checkConflict(course.id) : null;
      const remaining = course.capacity - course.enrolled;
      const isFull = remaining <= 0;

      const card = document.createElement('div');
      card.className = `course-card ${isSelected ? 'selected' : ''}`;
      card.dataset.courseId = course.id;

      card.innerHTML = `
        <div class="course-header">
          <span class="course-type-badge" style="background:${course.typeColor}">${course.typeLabel}</span>
          <span class="course-id">${course.id}</span>
          ${isFull ? '<span class="badge-full">已满</span>' : ''}
        </div>
        <h3 class="course-name">${course.name}</h3>
        <div class="course-info">
          <div class="info-row"><span class="info-icon">👨‍🏫</span> ${course.teacher}</div>
          <div class="info-row"><span class="info-icon">📅</span> ${course.schedule}</div>
          <div class="info-row"><span class="info-icon">🏫</span> ${course.classroom}</div>
          <div class="info-row">
            <span class="info-icon">📊</span> ${course.credits}学分
            <span style="margin-left:8px">👥 ${course.enrolled}/${course.capacity}</span>
            <span style="margin-left:8px">⭐ ${course.popularity}</span>
          </div>
        </div>
        <div class="course-actions">
          ${this._renderActionButton(course, isSelected, isFull, conflict)}
        </div>
      `;

      // 绑定按钮事件
      const btn = card.querySelector('button');
      if (btn) {
        btn.addEventListener('click', (e) => {
          e.stopPropagation();
          this._handleAction(course, isSelected);
        });
      }

      // 点击卡片查看详情
      card.addEventListener('click', () => {
        const result = this.assistant.processMessage(`${course.id}`);
        // 通过自定义事件通知聊天面板显示结果
        window.dispatchEvent(new CustomEvent('show_detail', {
          detail: { course, reply: result.reply }
        }));
      });

      this.courseListEl.appendChild(card);
    });
  }

  /**
   * 渲染操作按钮
   * @param {Object} course
   * @param {boolean} isSelected
   * @param {boolean} isFull
   * @param {Object|null} conflict
   * @returns {string}
   */
  _renderActionButton(course, isSelected, isFull, conflict) {
    if (isSelected) {
      if (course.type === 'required') {
        return '<button class="btn btn-disabled" disabled>必修课（不可退）</button>';
      }
      return '<button class="btn btn-drop">退选此课</button>';
    }
    if (isFull) {
      return '<button class="btn btn-disabled" disabled>已满员</button>';
    }
    if (conflict) {
      return `<button class="btn btn-conflict" title="与《${conflict.name}》时间冲突">时间冲突</button>`;
    }
    return '<button class="btn btn-select">选课</button>';
  }

  /**
   * 处理选课/退课操作
   * @param {Object} course
   * @param {boolean} isSelected
   */
  _handleAction(course, isSelected) {
    const msg = isSelected ? `退课${course.id}` : `选课${course.id}`;
    const result = this.assistant.processMessage(msg);
    this.render();

    // 通知聊天面板显示结果
    window.dispatchEvent(new CustomEvent('show_toast', {
      detail: { message: result.reply, success: result.reply.includes('✅') }
    }));
  }

  /** 渲染已选课程列表 */
  _renderSelectedList() {
    const selected = this.store.getSelectedCourses();
    this.selectedListEl.innerHTML = '';

    if (selected.length === 0) {
      this.selectedListEl.innerHTML = '<div class="empty-state">📋 还没有选课，试试让AI助手帮你推荐吧~</div>';
      return;
    }

    selected.forEach(course => {
      const item = document.createElement('div');
      item.className = 'selected-item';

      item.innerHTML = `
        <div class="selected-info">
          <span class="course-type-badge small" style="background:${course.typeColor}">${course.typeLabel}</span>
          <strong>${course.name}</strong>
          <span class="text-muted">${course.schedule} | ${course.credits}学分</span>
        </div>
        ${course.type !== 'required' ? `<button class="btn btn-drop small" data-id="${course.id}">退选</button>` : ''}
      `;

      const dropBtn = item.querySelector('.btn-drop');
      if (dropBtn) {
        dropBtn.addEventListener('click', (e) => {
          e.stopPropagation();
          this._handleAction(course, true);
        });
      }

      this.selectedListEl.appendChild(item);
    });
  }

  /** 渲染统计栏 */
  _renderStats() {
    const selected = this.store.getSelectedCourses();
    const totalCredits = this.store.getTotalCredits();
    this.statCount.textContent = selected.length;
    this.statCredits.textContent = `${totalCredits}/25`;
  }
}

export default CoursePanel;
