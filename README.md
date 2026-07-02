# 🎓 智能选课系统

> 《设计模式》课程期末作业 — 基于设计模式的智能选课系统

## 项目简介

一个纯前端的智能选课系统，集成AI聊天助手（模拟），用户可以通过自然语言对话完成课程搜索、选课、退课、课表查询等操作。系统在设计上充分运用了多种 GoF 设计模式，代码结构清晰，易于扩展。

## 快速开始

直接用浏览器打开 `index.html` 即可运行，无需安装任何依赖。

```
设计模式/
└── index.html   ← 双击打开即可
```

## 项目结构

```
设计模式/
├── index.html                  # 主页面
├── css/
│   └── style.css               # ChatGPT风格蓝白配色
├── js/
│   ├── main.js                 # 系统入口，初始化各模块
│   ├── patterns/               # 设计模式层
│   │   ├── singleton.js        # 单例模式
│   │   ├── observer.js         # 观察者模式（事件总线）
│   │   ├── command.js          # 命令模式（6种具体命令）
│   │   ├── factory.js          # 工厂模式（课程创建）
│   │   └── strategy.js         # 策略模式（3种推荐策略）
│   ├── models/
│   │   └── course.js           # 课程数据模型 + 14门预置课程
│   ├── services/
│   │   ├── ai-assistant.js     # AI助手核心（NLU意图识别+命令调度）
│   │   ├── course-store.js     # 课程数据仓库（外观模式封装）
│   │   └── scheduler.js        # 排课冲突检测
│   └── ui/
│       ├── chat-panel.js       # 聊天面板组件
│       ├── course-panel.js     # 选课面板组件
│       └── toasts.js           # Toast通知组件
└── README.md                   # 本文档
```

## 设计模式应用

### 1. 单例模式 (Singleton) 🏆

| 应用类 | 文件 | 说明 |
|--------|------|------|
| `CourseStore` | [js/services/course-store.js](js/services/course-store.js) | 全局唯一数据仓库，管理课程数据和 localStorage |
| `Scheduler` | [js/services/scheduler.js](js/services/scheduler.js) | 全局唯一排课器，检测时间冲突 |
| `AIAssistant` | [js/services/ai-assistant.js](js/services/ai-assistant.js) | 全局唯一AI助手实例 |

```javascript
// 使用方式：通过 getInstance() 获取唯一实例
const store = CourseStore.getInstance();
const assistant = AIAssistant.getInstance();
```

### 2. 观察者模式 (Observer) 👀

**EventBus**（[js/patterns/observer.js](js/patterns/observer.js)）实现了发布-订阅机制：

- 课程数据变更 → 自动刷新课程列表UI
- 选课/退课成功 → 自动推送消息到聊天面板
- AI搜索结果 → 自动高亮课程卡片

```javascript
// AI助手发布事件
eventBus.emit('course_changed', { action: 'select', courseId: 'CS301' });

// UI组件订阅事件
eventBus.on('course_changed', (data) => this.render());
```

### 3. 命令模式 (Command) ⚡

**6种具体命令**（[js/patterns/command.js](js/patterns/command.js)）：

| 命令类 | 功能 | 支持撤销 |
|--------|------|----------|
| `SearchCourseCommand` | 搜索课程 | ❌ |
| `SelectCourseCommand` | 选课（含冲突检测） | ✅ |
| `DropCourseCommand` | 退课 | ✅ |
| `RecommendCommand` | 推荐课程 | ❌ |
| `QuerySelectedCommand` | 查看已选 | ❌ |
| `QueryScheduleCommand` | 查看课表 | ❌ |
| `CourseDetailCommand` | 查看详情 | ❌ |

`CommandInvoker` 管理命令执行和历史，支持撤销操作。

### 4. 工厂模式 (Factory) 🏭

**CourseFactory**（[js/patterns/factory.js](js/patterns/factory.js)）根据课程类型创建对象：

- `required` → 必修课（红色标签，默认选中，不可退选）
- `elective` → 选修课（蓝色标签）
- `public` → 公选课（绿色标签）

```javascript
// 批量创建课程，自动根据类型添加属性
const courses = CourseFactory.createCourses(PRESET_COURSES);
```

### 5. 策略模式 (Strategy) 🎯

**3种推荐策略**（[js/patterns/strategy.js](js/patterns/strategy.js)）：

| 策略 | 算法 | 适用场景 |
|------|------|----------|
| `PopularFirstStrategy` | 按评分降序 | 默认推荐，选热门课程 |
| `CreditBalanceStrategy` | 按学分升序 | 学习压力大时，选轻量课程 |
| `TimeMatchStrategy` | 优先不冲突 | 课表较满时，避开时间冲突 |

`RecommendContext` 管理策略切换，AI助手根据用户意图自动切换。

### 6. 外观模式 (Facade) 🏠

**CourseStore**（[js/services/course-store.js](js/services/course-store.js)）封装了 localStorage 的 CRUD 操作：

```javascript
// 简洁的外观API
store.getAllCourses()       // 获取所有课程
store.searchCourses('设计')  // 模糊搜索
store.selectCourse('CS301') // 选课
store.dropCourse('CS301')   // 退课
store.getSelectedCourses()  // 已选课程
store.getTotalCredits()     // 总学分
```

## AI助手能力

| 功能 | 示例输入 |
|------|----------|
| 推荐课程 | "推荐课程"、"选什么课好" |
| 搜索课程 | "搜索设计"、"有没有AI相关的课" |
| 选课 | "选课CS402"、"帮我选人工智能" |
| 退课 | "退课CS402"、"取消设计模式" |
| 查看已选 | "我选了哪些课"、"我的课程" |
| 课表查询 | "我的课表"、"周一有什么课" |
| 课程详情 | "CS301详情"、"设计模式怎么样" |
| 帮助 | "帮助"、"你能做什么" |

## 预置课程

共14门课程，覆盖计算机科学与技术专业：

- **必修课 5门**：数据结构与算法、操作系统、计算机网络、设计模式、数据库原理
- **选修课 5门**：人工智能导论、Web前端开发、云计算技术、软件测试、移动应用开发
- **公选课 4门**：大学英语（高级）、创新与创业、心理学与生活、摄影艺术

## 技术栈

- 纯前端：HTML5 + CSS3 + Vanilla JavaScript (ES6 Modules)
- 数据持久化：localStorage
- 无需构建工具，无需服务器，浏览器直接打开即用

## 浏览器兼容性

支持所有现代浏览器（Chrome、Firefox、Edge、Safari），需要支持 ES6 Module。
