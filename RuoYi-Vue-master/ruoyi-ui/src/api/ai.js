import request from '@/utils/request'

// 升级版：直接把前端的 chatList 数组发送给后端
export function sendAiChat(chatList) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data: chatList // 👈 直接把整个对话历史列表传过去
  })
}
