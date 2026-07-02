/**
 * ============================================
 * 智能选课系统 — 主入口
 * ============================================
 *
 * 初始化顺序：
 *   1. 数据层（CourseStore + Scheduler）
 *   2. AI助手服务
 *   3. UI组件（CoursePanel + ChatPanel + ToastManager）
 *
 * 设计模式应用总览：
 *   - 单例模式：CourseStore, Scheduler, AIAssistant 均为全局唯一
 *   - 观察者模式：EventBus 连接 AI助手 → UI组件
 *   - 命令模式：AI意图封装为 Command，由 CommandInvoker 执行
 *   - 工厂模式：CourseFactory 按类型创建课程对象
 *   - 策略模式：RecommendContext 动态切换推荐算法
 *   - 外观模式：CourseStore 封装 localStorage 操作
 */

import CourseStore from './services/course-store.js';
import Scheduler from './services/scheduler.js';
import AIAssistant from './services/ai-assistant.js';
import { CoursePanel } from './ui/course-panel.js';
import { ChatPanel } from './ui/chat-panel.js';
import ToastManager from './ui/toasts.js';

// ==================== 系统初始化 ====================

document.addEventListener('DOMContentLoaded', () => {
  console.log('🎓 智能选课系统启动中...');

  // 1. 初始化数据层（单例模式，自动从localStorage加载）
  const store = CourseStore.getInstance();
  const scheduler = Scheduler.getInstance();
  console.log(`   📚 课程库已加载：${store.getAllCourses().length} 门课程`);
  console.log(`   ✅ 已选课程：${store.getSelectedCourses().length} 门`);

  // 2. 初始化AI助手（单例模式）
  const assistant = AIAssistant.getInstance();
  console.log('   🤖 AI助手小智已就绪');

  // 3. 初始化UI组件
  const coursePanel = new CoursePanel();
  const chatPanel = new ChatPanel('chat-panel');
  const toastManager = new ToastManager();
  console.log('   🎨 UI组件初始化完成');

  // 4. 监听课程详情展示事件（点击课程卡片 → 聊天面板显示详情）
  window.addEventListener('show_detail', (e) => {
    chatPanel.addBotMessage(e.detail.reply);
  });

  // 5. 监听Toast事件
  window.addEventListener('show_toast', (e) => {
    const msg = e.detail.message;
    const firstLine = msg.split('\n')[0];
    toastManager.show(firstLine, e.detail.success ? 'success' : 'error');
  });

  // 6. 监听AI助手事件（选课成功后更新UI）
  assistant.getEventBus().on('course_changed', (data) => {
    coursePanel.render();
  });

  console.log('🎉 智能选课系统启动完成！');
  console.log('   💡 提示：在右侧聊天框输入消息，AI助手会帮你完成选课操作');
});
