/**
 * AI助手服务 — 智能选课助手的核心
 *
 * 职责：
 *   1. NLU自然语言理解（关键词意图识别）
 *   2. 命令模式调度执行
 *   3. 生成自然语言回复
 */

import Singleton from '../patterns/singleton.js';
import EventBus from '../patterns/observer.js';
import {
  CommandInvoker,
  SearchCourseCommand,
  SelectCourseCommand,
  DropCourseCommand,
  RecommendCommand,
  QuerySelectedCommand,
  QueryScheduleCommand,
  CourseDetailCommand,
} from '../patterns/command.js';
import {
  RecommendContext,
  PopularFirstStrategy,
  CreditBalanceStrategy,
  TimeMatchStrategy,
} from '../patterns/strategy.js';
import CourseStore from './course-store.js';
import Scheduler from './scheduler.js';

// ==================== NLU 意图识别规则 ====================
// 每条规则: { intent, keywords, replyHint }
const INTENT_RULES = [
  {
    intent: 'recommend',
    keywords: ['推荐', '建议', '选什么', '有什么课', '推荐课程', '帮我选课', '选哪', '哪个好'],
    replyHint: null
  },
  {
    intent: 'search',
    keywords: ['搜索', '查找', '有没有', '找一下', '搜'],
    replyHint: '请输入关键词，如"搜索设计模式"'
  },
  {
    intent: 'select',
    keywords: ['选课', '选', '帮我选', '选择', '我要选', '我要上'],
    replyHint: '请输入课程编号，如"选课CS301"'
  },
  {
    intent: 'drop',
    keywords: ['退课', '退选', '退', '删除', '取消选课', '不选', '去掉', '移除'],
    replyHint: '请输入课程编号，如"退课CS301"'
  },
  {
    intent: 'query_selected',
    keywords: ['已选', '我的课程', '选了哪些', '我选的', '已选课程', '查看已选', '我的课表'],
    replyHint: null
  },
  {
    intent: 'schedule',
    keywords: ['课表', '时间表', '星期', '周', '课表查询', '课程表', '哪天'],
    replyHint: null
  },
  {
    intent: 'detail',
    keywords: ['详情', '介绍', '是什么', '怎么样', '学分', '详细'],
    replyHint: '请输入课程编号，如"CS301详情"'
  },
  {
    intent: 'help',
    keywords: ['帮助', '能做什么', '功能', '怎么用', 'help', '你好', 'hi', 'hello', '嗨', '在吗'],
    replyHint: null
  },
];

/**
 * 辅助回复库 — 让对话更自然
 */
const GREETINGS = [
  '你好！我是选课助手小智 🤖，有什么可以帮你的吗？',
  '嗨~ 我是你的选课助手小智！想选什么课呢？😊',
  '你好呀！需要我帮你推荐课程、搜索课程，还是查看已选课程呢？',
];

const HELP_TEXT = `🤖 **我是选课助手小智，可以帮你：**

📚 **推荐课程** — 输入"推荐课程"或"有什么好课"
🔍 **搜索课程** — 输入"搜索+关键词"，如"搜索设计"
✅ **选课** — 输入"选课+课程编号"，如"选课CS402"
❌ **退课** — 输入"退课+课程编号"，如"退课CS402"
📋 **查看已选** — 输入"我选了哪些课"
📅 **查看课表** — 输入"我的课表"或"周一有什么课"
📖 **课程详情** — 输入"课程编号+详情"，如"CS301详情"

试试对我说"推荐课程"吧~`;

const UNKNOWN_REPLIES = [
  '抱歉，我没有理解你的意思 😅\n你可以试试输入"帮助"查看我能做什么~',
  '嗯...我不太确定你想做什么。输入"帮助"看看我的能力列表？',
  '没太明白~ 试试说"推荐课程"或"搜索XX"？',
];

/**
 * AI助手 — 单例模式 + 命令模式调度
 */
export class AIAssistant extends Singleton {
  constructor() {
    super();
    /** @type {CourseStore} */
    this.store = CourseStore.getInstance();
    /** @type {Scheduler} */
    this.scheduler = Scheduler.getInstance();
    /** @type {EventBus} */
    this.eventBus = new EventBus(); // AI助手有独立的事件总线，对外通信
    /** @type {CommandInvoker} */
    this.invoker = new CommandInvoker();
    /** @type {RecommendContext} */
    this.recommendCtx = new RecommendContext();

    // 对话上下文
    this._pendingIntent = null; // 待确认的意图（多轮对话用）
  }

  /**
   * 处理用户消息 — 主入口
   * @param {string} message - 用户输入
   * @returns {{ reply: string, event?: { type: string, data: any } }}
   */
  processMessage(message) {
    const msg = message.trim();
    if (!msg) return { reply: '请输入内容哦~' };

    // Step 1: 意图识别
    let intent = this._recognizeIntent(msg);
    let param = this._extractParam(msg, intent);

    // Step 2: 特殊意图处理
    if (intent === 'help' || intent === 'greeting') {
      if (intent === 'greeting' || /你好|hi|hello|嗨|在吗/.test(msg)) {
        return { reply: GREETINGS[Math.floor(Math.random() * GREETINGS.length)] };
      }
      return { reply: HELP_TEXT };
    }

    // Step 3: 没有明确意图时，尝试智能匹配
    if (intent === 'unknown') {
      // 尝试按课程ID搜索
      const course = this.store.getCourseById(msg.toUpperCase());
      if (course) {
        intent = 'detail';
        param = course.id;
      } else {
        // 尝试按名称搜索
        const results = this.store.searchCourses(msg);
        if (results.length > 0 && results.length <= 3) {
          intent = 'search';
          param = msg;
        } else {
          return { reply: UNKNOWN_REPLIES[Math.floor(Math.random() * UNKNOWN_REPLIES.length)] };
        }
      }
    }

    // Step 4: 策略切换（用户说"换种推荐"时切换策略）
    if (intent === 'recommend' && /均衡|学分/.test(msg)) {
      this.recommendCtx.setStrategy(new CreditBalanceStrategy());
    } else if (intent === 'recommend' && /时间|冲突|不冲突/.test(msg)) {
      this.recommendCtx.setStrategy(new TimeMatchStrategy());
    } else if (intent === 'recommend') {
      this.recommendCtx.setStrategy(new PopularFirstStrategy());
    }

    // Step 5: 创建命令并执行
    const command = this._createCommand(intent, param, msg);
    if (!command) {
      return { reply: `我理解你想${this._intentLabel(intent)}，但需要更多信息。${INTENT_RULES.find(r => r.intent === intent)?.replyHint || '请输入"帮助"查看用法。'}` };
    }

    const result = this.invoker.execute(command);

    // Step 6: 发布领域事件（观察者模式）
    if (result.success && (intent === 'select' || intent === 'drop')) {
      this.eventBus.emit('course_changed', {
        action: intent,
        courseId: param,
        course: result.data,
      });
    }
    if (intent === 'search' || intent === 'recommend') {
      this.eventBus.emit('search_result', {
        courses: result.data || [],
        keyword: param,
        intent: intent,
      });
    }

    return { reply: result.message };
  }

  /**
   * NLU意图识别 — 关键词匹配
   * @param {string} msg
   * @returns {string} intent名称
   */
  _recognizeIntent(msg) {
    for (const rule of INTENT_RULES) {
      for (const kw of rule.keywords) {
        if (msg.includes(kw)) {
          return rule.intent;
        }
      }
    }
    return 'unknown';
  }

  /**
   * 从消息中提取参数（课程ID等）
   * @param {string} msg
   * @param {string} intent
   * @returns {string|null}
   */
  _extractParam(msg, intent) {
    // 提取课程ID（如CS301、GE101等）
    const idMatch = msg.match(/[A-Za-z]{2,4}\d{2,4}/);
    if (idMatch) return idMatch[0].toUpperCase();

    // 提取星期几
    if (intent === 'schedule') {
      const weekMap = { '一': 1, '二': 2, '三': 3, '四': 4, '五': 5 };
      for (const [char, num] of Object.entries(weekMap)) {
        if (msg.includes(`周${char}`) || msg.includes(`星期${char}`)) return num;
      }
    }

    // 搜索关键词（去掉意图词）
    if (intent === 'search') {
      const kw = msg.replace(/搜索|查找|有没有|找一下|搜/g, '').trim();
      return kw || null;
    }

    return idMatch ? idMatch[0].toUpperCase() : null;
  }

  /**
   * 根据意图创建对应的命令对象
   * @param {string} intent
   * @param {string|null} param
   * @param {string} originalMsg
   * @returns {import('../patterns/command.js').Command|null}
   */
  _createCommand(intent, param, originalMsg) {
    switch (intent) {
      case 'search':
        return param ? new SearchCourseCommand(this.store, param) : null;

      case 'select':
        return param ? new SelectCourseCommand(this.store, this.scheduler, param) : null;

      case 'drop':
        return param ? new DropCourseCommand(this.store, param) : null;

      case 'recommend':
        return new RecommendCommand(
          this.store,
          this.recommendCtx,
        );

      case 'query_selected':
        return new QuerySelectedCommand(this.store);

      case 'schedule':
        return new QueryScheduleCommand(this.store, typeof param === 'number' ? param : null);

      case 'detail':
        return param ? new CourseDetailCommand(this.store, param) : null;

      default:
        return null;
    }
  }

  /** 意图中文标签 */
  _intentLabel(intent) {
    const map = {
      recommend: '推荐课程', search: '搜索课程', select: '选课',
      drop: '退课', query_selected: '查看已选', schedule: '查看课表',
      detail: '查看详情', help: '获取帮助',
    };
    return map[intent] || '执行操作';
  }

  /**
   * 获取事件总线（供其他模块订阅）
   * @returns {EventBus}
   */
  getEventBus() {
    return this.eventBus;
  }

  /**
   * 撤销上一步操作
   * @returns {string}
   */
  undo() {
    const result = this.invoker.undo();
    if (result.success) {
      this.eventBus.emit('course_changed', { action: 'undo' });
    }
    return result.message;
  }
}

export default AIAssistant;
