import request from '@/utils/request'

// 查询读者信息列表
export function listReader(query) {
  return request({
    url: '/system/reader/list',
    method: 'get',
    params: query
  })
}

// 查询读者信息详细
export function getReader(rdID) {
  return request({
    url: '/system/reader/' + rdID,
    method: 'get'
  })
}

// 新增读者信息
export function addReader(data) {
  return request({
    url: '/system/reader',
    method: 'post',
    data: data
  })
}

// 修改读者信息
export function updateReader(data) {
  return request({
    url: '/system/reader',
    method: 'put',
    data: data
  })
}

// 删除读者信息
export function delReader(rdID) {
  return request({
    url: '/system/reader/' + rdID,
    method: 'delete'
  })
}

  // 查询下一个读者证号
export function getNextCode(query) {
    return request({
      url: '/system/reader/getNextCode', // 这里的路径要和你 Controller 的路径匹配
      method: 'get',
      params: query
    })
  }

export function reissueReaderCard(data) {
  return request({
    url: '/system/reader/reissue', // 这里的路径要和后端 Controller 一致
    method: 'post',
    data: data
  })
}



