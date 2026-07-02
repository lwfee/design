/**
 * Toast通知组件
 * 用于显示操作结果的短暂提示
 */

export class ToastManager {
  constructor() {
    /** @type {HTMLElement} */
    this.container = document.getElementById('toast-container');
    if (!this.container) {
      this.container = document.createElement('div');
      this.container.id = 'toast-container';
      document.body.appendChild(this.container);
    }
    this._bindGlobalEvents();
  }

  /** 监听全局事件 */
  _bindGlobalEvents() {
    window.addEventListener('show_toast', (e) => {
      const { message, success } = e.detail;
      this.show(message, success ? 'success' : 'error');
    });

    // 课程详情展示
    window.addEventListener('show_detail', (e) => {
      const { reply } = e.detail;
      // 通过自定义事件转发给聊天面板
      window.dispatchEvent(new CustomEvent('append_bot_message', { detail: { text: reply } }));
    });
  }

  /**
   * 显示Toast
   * @param {string} message
   * @param {'success'|'error'|'info'} type
   * @param {number} [duration=3000]
   */
  show(message, type = 'info', duration = 3000) {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
      <span class="toast-icon">${type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️'}</span>
      <span class="toast-text">${message.replace(/\n/g, ' ').substring(0, 100)}</span>
    `;

    this.container.appendChild(toast);

    // 入场动画
    requestAnimationFrame(() => {
      toast.classList.add('show');
    });

    // 自动移除
    setTimeout(() => {
      toast.classList.remove('show');
      setTimeout(() => toast.remove(), 300);
    }, duration);
  }
}

export default ToastManager;
