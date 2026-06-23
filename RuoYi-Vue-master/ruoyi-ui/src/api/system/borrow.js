import request from '@/utils/request'

// 查询借阅记录列表
export function listBorrow(query) {
  return request({
    url: '/system/borrow/list',
    method: 'get',
    params: query
  })
}

// 查询借阅记录详细
export function getBorrow(BorrowID) {
  return request({
    url: '/system/borrow/' + BorrowID,
    method: 'get'
  })
}

// 新增借阅记录
export function addBorrow(data) {
  return request({
    url: '/system/borrow/doBorrow',
    method: 'post',
    data: data
  })
}

// 修改借阅记录
export function updateBorrow(data) {
  return request({
    url: '/system/borrow',
    method: 'put',
    data: data
  })
}

// 删除借阅记录
export function delBorrow(BorrowID) {
  return request({
    url: '/system/borrow/' + BorrowID,
    method: 'delete'
  })
}
// 自助借书
export function selfBorrow(data) {
  return request({
    url: '/system/borrow/selfBorrow',
    method: 'post',
    data: data
  })
}

// 自助还书
export function selfReturn(data) {
  return request({
    url: '/system/borrow/selfReturn',
    method: 'post',
    data: data
  })
}
