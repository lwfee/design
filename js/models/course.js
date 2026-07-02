/**
 * 课程数据模型与预置数据
 */

/**
 * 课程类型元数据 — 用于工厂模式
 */
export const COURSE_META = {
  required: { label: '必修', color: '#e74c3c' },
  elective: { label: '选修', color: '#3498db' },
  public:   { label: '公选', color: '#2ecc71' },
};

/**
 * 预置课程数据 — 模拟学校教务系统课程库
 * 覆盖计算机科学与技术专业常见课程
 */
export const PRESET_COURSES = [
  // ===== 必修课 (required) =====
  {
    id: 'CS101', name: '数据结构与算法', type: 'required',
    teacher: '李教授', credits: 4, schedule: '周一 1-2节',
    weekday: 1, timeSlot: '1-2', capacity: 80, enrolled: 60,
    popularity: 4.8, classroom: '教学楼A201',
    description: '学习线性表、树、图等经典数据结构，以及排序、搜索等算法设计思想。计算机科学核心课程。'
  },
  {
    id: 'CS102', name: '操作系统', type: 'required',
    teacher: '王教授', credits: 4, schedule: '周二 3-4节',
    weekday: 2, timeSlot: '3-4', capacity: 70, enrolled: 55,
    popularity: 4.5, classroom: '教学楼A301',
    description: '进程管理、内存管理、文件系统和I/O系统，理解计算机系统的核心工作原理。'
  },
  {
    id: 'CS103', name: '计算机网络', type: 'required',
    teacher: '赵教授', credits: 3, schedule: '周三 1-2节',
    weekday: 3, timeSlot: '1-2', capacity: 75, enrolled: 50,
    popularity: 4.6, classroom: '教学楼B101',
    description: 'TCP/IP协议栈、HTTP协议、路由算法、网络安全基础，互联网技术的理论基石。'
  },
  {
    id: 'CS301', name: '设计模式', type: 'required',
    teacher: '张教授', credits: 3, schedule: '周四 5-6节',
    weekday: 4, timeSlot: '5-6', capacity: 60, enrolled: 45,
    popularity: 4.9, classroom: '教学楼A301',
    description: '学习23种经典设计模式（GoF），包括创建型、结构型和行为型模式，掌握可复用面向对象软件设计方法。'
  },
  {
    id: 'CS201', name: '数据库原理', type: 'required',
    teacher: '刘教授', credits: 3, schedule: '周五 3-4节',
    weekday: 5, timeSlot: '3-4', capacity: 80, enrolled: 65,
    popularity: 4.4, classroom: '教学楼C201',
    description: '关系模型、SQL语言、事务管理、索引优化，数据库系统的核心理论基础。'
  },

  // ===== 选修课 (elective) =====
  {
    id: 'CS401', name: '人工智能导论', type: 'elective',
    teacher: '陈教授', credits: 3, schedule: '周一 5-6节',
    weekday: 1, timeSlot: '5-6', capacity: 50, enrolled: 35,
    popularity: 4.7, classroom: '科技楼502',
    description: '搜索算法、知识表示、机器学习基础、神经网络入门，AI领域的通识课程。'
  },
  {
    id: 'CS402', name: 'Web前端开发', type: 'elective',
    teacher: '周老师', credits: 2, schedule: '周二 7-8节',
    weekday: 2, timeSlot: '7-8', capacity: 45, enrolled: 40,
    popularity: 4.3, classroom: '科技楼301',
    description: 'HTML5、CSS3、JavaScript、React框架，构建现代响应式Web应用的实用课程。'
  },
  {
    id: 'CS403', name: '云计算技术', type: 'elective',
    teacher: '吴教授', credits: 2, schedule: '周三 5-6节',
    weekday: 3, timeSlot: '5-6', capacity: 40, enrolled: 20,
    popularity: 4.2, classroom: '科技楼401',
    description: '虚拟化、容器技术(Docker)、Kubernetes编排、微服务架构，云计算核心技术栈。'
  },
  {
    id: 'CS404', name: '软件测试', type: 'elective',
    teacher: '郑老师', credits: 2, schedule: '周四 3-4节',
    weekday: 4, timeSlot: '3-4', capacity: 45, enrolled: 25,
    popularity: 3.8, classroom: '教学楼B202',
    description: '单元测试、集成测试、自动化测试、TDD方法论，保障软件质量的关键技能。'
  },
  {
    id: 'CS405', name: '移动应用开发', type: 'elective',
    teacher: '孙教授', credits: 3, schedule: '周五 5-6节',
    weekday: 5, timeSlot: '5-6', capacity: 40, enrolled: 30,
    popularity: 4.1, classroom: '科技楼503',
    description: 'Android/iOS开发基础、Flutter跨平台开发、移动UI设计，进入移动开发领域的敲门砖。'
  },

  // ===== 公选课 (public) =====
  {
    id: 'GE101', name: '大学英语（高级）', type: 'public',
    teacher: 'Helen老师', credits: 2, schedule: '周一 3-4节',
    weekday: 1, timeSlot: '3-4', capacity: 100, enrolled: 80,
    popularity: 4.0, classroom: '外语楼101',
    description: '学术英语读写、英语演讲与辩论，提升国际学术交流能力。'
  },
  {
    id: 'GE102', name: '创新与创业', type: 'public',
    teacher: '马教授', credits: 2, schedule: '周三 7-8节',
    weekday: 3, timeSlot: '7-8', capacity: 120, enrolled: 90,
    popularity: 4.3, classroom: '综合楼201',
    description: '创业思维培养、商业模式设计、商业计划书撰写，激发创新精神与创业能力。'
  },
  {
    id: 'GE103', name: '心理学与生活', type: 'public',
    teacher: '林教授', credits: 2, schedule: '周四 7-8节',
    weekday: 4, timeSlot: '7-8', capacity: 100, enrolled: 70,
    popularity: 4.6, classroom: '综合楼301',
    description: '认知心理学、社会心理学、情绪管理，用科学心理学指导日常生活。'
  },
  {
    id: 'GE104', name: '摄影艺术', type: 'public',
    teacher: '黄老师', credits: 1, schedule: '周五 7-8节',
    weekday: 5, timeSlot: '7-8', capacity: 30, enrolled: 28,
    popularity: 4.5, classroom: '艺术楼201',
    description: '构图法则、光影运用、后期处理，用镜头发现生活中的美。名额紧张！'
  },
];

export default PRESET_COURSES;
