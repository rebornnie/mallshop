import request from '@/utils/request'

/**
 * 销售报表统计
 * GET /api/admin/report/sales?startTime=&endTime=
 */
export function getSalesReport(params) {
  return request({
    url: '/api/admin/report/sales',
    method: 'get',
    params
  })
}

/**
 * 商品销售排行
 * GET /api/admin/report/productRank?startTime=&endTime=&topN=10
 */
export function getProductRank(params) {
  return request({
    url: '/api/admin/report/productRank',
    method: 'get',
    params
  })
}

/**
 * 订单导出Excel
 * GET /api/admin/report/exportOrder?orderSn=&status=&receiverPhone=&startTime=&endTime=
 * 返回二进制流，需要设置 responseType: 'blob'
 */
export function exportOrder(params) {
  return request({
    url: '/api/admin/report/exportOrder',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
