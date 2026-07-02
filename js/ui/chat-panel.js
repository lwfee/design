/**
 * 聊天面板UI组件
 *
 * 负责：
 *   - 渲染聊天消息列表
 *   - 处理用户输入
 *   - 显示AI回复（支持Markdown简单渲染）
 */

import AIAssistant from '../services/ai-assistant.js';

export class ChatPanel {
  constructor(containerId) {
    /** @type {HTMLElement} */
    this.container = document.getElementById(containerId);
    /** @type {HTMLElement} */
    this.messageList = this.container.querySelector('.chat-messages');
    /** @type {HTMLInputElement} */
    this.input = this.container.querySelector('.chat-input input');
    /** @type {HTMLElement} */
    this.sendBtn = this.container.querySelector('.chat-input .send-btn');
    /** @type {AIAssistant} */
    this.assistant = AIAssistant.getInstance();

    this._bindEvents();
    this._addWelcomeMessage();
  }

  /** 绑定事件 */
  _bindEvents() {
    // 发送按钮
    this.sendBtn.addEventListener('click', () => this._sendMessage());

    // 回车发送
    this.input.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        this._sendMessage();
      }
    });

    // 快捷操作按钮
    this.container.querySelectorAll('.quick-action').forEach(btn => {
      btn.addEventListener('click', () => {
        this.input.value = btn.dataset.text;
        this._sendMessage();
      });
    });
  }

  /** 添加欢迎消息 */
  _addWelcomeMessage() {
    this.addBotMessage(
      `你好！我是选课助手**小智** 🤖\n\n` +
      `我可以帮你推荐课程、搜索课程、选课退课，还可以查看课表~\n` +
      `试试下面的快捷操作，或直接输入你想做的事情吧！`
    );
  }

  /** 发送用户消息 */
  _sendMessage() {
    const text = this.input.value.trim();
    if (!text) return;

    // 显示用户消息
    this.addUserMessage(text);
    this.input.value = '';
    this.input.focus();

    // 处理消息
    const result = this.assistant.processMessage(text);

    // 模拟延迟，更像真实对话
    setTimeout(() => {
      this.addBotMessage(result.reply);
    }, 300 + Math.random() * 500);
  }

  /**
   * 添加用户消息
   * @param {string} text
   */
  addUserMessage(text) {
    const msg = this._createMessage('user', text);
    this.messageList.appendChild(msg);
    this._scrollToBottom();
  }

  /**
   * 添加机器人消息（支持简单Markdown）
   * @param {string} text
   */
  addBotMessage(text) {
    const msg = this._createMessage('bot', text);
    this.messageList.appendChild(msg);
    this._scrollToBottom();
  }

  /**
   * 创建消息DOM
   * @param {'user'|'bot'} role
   * @param {string} text
   * @returns {HTMLElement}
   */
  _createMessage(role, text) {
    const div = document.createElement('div');
    div.className = `chat-message ${role}`;

    const avatar = document.createElement('div');
    avatar.className = 'chat-avatar';
    avatar.textContent = role === 'bot' ? '🤖' : '👤';

    const bubble = document.createElement('div');
    bubble.className = 'chat-bubble';
    bubble.innerHTML = this._renderMarkdown(text);

    div.appendChild(avatar);
    div.appendChild(bubble);
    return div;
  }

  /**
   * 简单Markdown渲染
   * @param {string} text
   * @returns {string}
   */
  _renderMarkdown(text) {
    return text
      // 粗体
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      // 换行
      .replace(/\n/g, '<br>')
      // emoji增强
      .replace(/✅/g, '<span class="emoji-success">✅</span>')
      .replace(/❌/g, '<span class="emoji-error">❌</span>');
  }

  /** 滚动到底部 */
  _scrollToBottom() {
    setTimeout(() => {
      this.messageList.scrollTop = this.messageList.scrollHeight;
    }, 50);
  }
}

export default ChatPanel;
