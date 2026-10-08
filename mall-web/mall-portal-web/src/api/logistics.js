import request from '@/utils/request'

/**
 * 查询订单物流
 * GET /api/oms/logistics/order/{orderId}
 */
export function getLogisticsByOrderId(orderId) {
  return request({
    url: `/api/oms/logistics/order/${orderId}`,
    method: 'get'
  })
}
