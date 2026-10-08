// 前端 Mock 数据 - 用于后端未启动时的独立演示
const mockData = {
  // 分类
  'GET /api/pms/category/list': {
    code: 200,
    message: 'success',
    data: [
      { id: 1, parentId: 0, name: '手机数码', icon: '', sort: 1, showStatus: 1, children: [
        { id: 11, parentId: 1, name: '智能手机', icon: '', sort: 1, showStatus: 1, children: [] },
        { id: 12, parentId: 1, name: '平板电脑', icon: '', sort: 2, showStatus: 1, children: [] }
      ]},
      { id: 2, parentId: 0, name: '电脑办公', icon: '', sort: 2, showStatus: 1, children: [
        { id: 21, parentId: 2, name: '笔记本', icon: '', sort: 1, showStatus: 1, children: [] },
        { id: 22, parentId: 2, name: '台式机', icon: '', sort: 2, showStatus: 1, children: [] }
      ]},
      { id: 3, parentId: 0, name: '服装服饰', icon: '', sort: 3, showStatus: 1, children: [
        { id: 31, parentId: 3, name: '男装', icon: '', sort: 1, showStatus: 1, children: [] },
        { id: 32, parentId: 3, name: '女装', icon: '', sort: 2, showStatus: 1, children: [] }
      ]},
      { id: 4, parentId: 0, name: '家居日用', icon: '', sort: 4, showStatus: 1, children: [] },
      { id: 5, parentId: 0, name: '美妆护肤', icon: '', sort: 5, showStatus: 1, children: [] },
      { id: 6, parentId: 0, name: '运动户外', icon: '', sort: 6, showStatus: 1, children: [] }
    ]
  },

  // 品牌
  'GET /api/pms/brand/list': {
    code: 200,
    message: 'success',
    data: {
      total: 6,
      pageNum: 1,
      pageSize: 20,
      records: [
        { id: 1, name: '华为', firstLetter: 'H', logo: '', description: '华为技术有限公司', recommendStatus: 1, sort: 1 },
        { id: 2, name: '小米', firstLetter: 'X', logo: '', description: '小米科技有限责任公司', recommendStatus: 1, sort: 2 },
        { id: 3, name: '苹果', firstLetter: 'A', logo: '', description: 'Apple Inc.', recommendStatus: 1, sort: 3 },
        { id: 4, name: '耐克', firstLetter: 'N', logo: '', description: 'NIKE', recommendStatus: 1, sort: 4 },
        { id: 5, name: '阿迪达斯', firstLetter: 'A', logo: '', description: 'Adidas', recommendStatus: 0, sort: 5 },
        { id: 6, name: '优衣库', firstLetter: 'Y', logo: '', description: 'UNIQLO', recommendStatus: 1, sort: 6 }
      ]
    }
  },

  // 商品列表
  'GET /api/pms/product/list': (params) => {
    const allProducts = [
      { id: 1, name: '华为 Mate 60 Pro', subtitle: '旗舰智能手机', pic: '', price: 6999, originalPrice: 7999, sale: 1200, brandId: 1, categoryId: 11, newStatus: 1 },
      { id: 2, name: 'iPhone 15 Pro Max', subtitle: '苹果旗舰手机', pic: '', price: 9999, originalPrice: 10999, sale: 800, brandId: 3, categoryId: 11, newStatus: 1 },
      { id: 3, name: '小米 14 Pro', subtitle: '徕卡影像旗舰', pic: '', price: 4999, originalPrice: 5499, sale: 2000, brandId: 2, categoryId: 11, newStatus: 0 },
      { id: 4, name: 'MacBook Pro 14', subtitle: 'M3 Pro 芯片', pic: '', price: 14999, originalPrice: 16999, sale: 300, brandId: 3, categoryId: 21, newStatus: 1 },
      { id: 5, name: '华为 MateBook X Pro', subtitle: '轻薄商务本', pic: '', price: 8999, originalPrice: 9999, sale: 500, brandId: 1, categoryId: 21, newStatus: 0 },
      { id: 6, name: 'Nike Air Max', subtitle: '经典运动鞋', pic: '', price: 899, originalPrice: 1099, sale: 3000, brandId: 4, categoryId: 6, newStatus: 0 },
      { id: 7, name: 'Adidas Ultraboost', subtitle: '缓震跑鞋', pic: '', price: 1099, originalPrice: 1299, sale: 1500, brandId: 5, categoryId: 6, newStatus: 0 },
      { id: 8, name: '优衣库 HEATTECH', subtitle: '保暖内衣', pic: '', price: 99, originalPrice: 149, sale: 5000, brandId: 6, categoryId: 32, newStatus: 0 },
      { id: 9, name: 'iPad Pro 12.9', subtitle: 'M2 芯片平板', pic: '', price: 8999, originalPrice: 9999, sale: 400, brandId: 3, categoryId: 12, newStatus: 1 },
      { id: 10, name: '小米平板 6 Pro', subtitle: '骁龙8+ 平板', pic: '', price: 2399, originalPrice: 2799, sale: 900, brandId: 2, categoryId: 12, newStatus: 1 }
    ]
    let records = allProducts
    if (params?.categoryId) {
      records = records.filter(p => p.categoryId == params.categoryId || (p.categoryId + '').startsWith(params.categoryId))
    }
    if (params?.brandId) {
      records = records.filter(p => p.brandId == params.brandId)
    }
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      records = records.filter(p => p.name.toLowerCase().includes(kw) || p.subtitle.toLowerCase().includes(kw))
    }
    if (params?.sort === 'sale') {
      records = [...records].sort((a, b) => b.sale - a.sale)
    } else if (params?.sort === 'price') {
      records = [...records].sort((a, b) => a.price - b.price)
    } else if (params?.sort === 'createTime') {
      records = [...records].reverse()
    }
    const pageNum = parseInt(params?.pageNum) || 1
    const pageSize = parseInt(params?.pageSize) || 20
    const start = (pageNum - 1) * pageSize
    return {
      code: 200,
      message: 'success',
      data: { total: records.length, pageNum, pageSize, records: records.slice(start, start + pageSize) }
    }
  },

  // 商品详情
  'GET /api/pms/product/': (id) => {
    const products = {
      1: { id: 1, name: '华为 Mate 60 Pro', subtitle: '旗舰智能手机', categoryId: 11, brandId: 1, productSn: 'P20260514001', pic: '', pics: '', price: 6999, originalPrice: 7999, stock: 500, sale: 1200, unit: '台', description: '<p>华为 Mate 60 Pro，搭载麒麟芯片，支持卫星通话。</p>', publishStatus: 1, newStatus: 1, skuList: [
        { id: 1, skuCode: 'SKU001', price: 6999, spData: '[{"key":"颜色","value":"雅川青"},{"key":"内存","value":"12GB+512GB"}]', pic: '', stock: 200 },
        { id: 2, skuCode: 'SKU002', price: 7999, spData: '[{"key":"颜色","value":"雅川青"},{"key":"内存","value":"12GB+1TB"}]', pic: '', stock: 100 }
      ], categoryName: '智能手机', brandName: '华为' },
      2: { id: 2, name: 'iPhone 15 Pro Max', subtitle: '苹果旗舰手机', categoryId: 11, brandId: 3, productSn: 'P20260514002', pic: '', pics: '', price: 9999, originalPrice: 10999, stock: 300, sale: 800, unit: '台', description: '<p>iPhone 15 Pro Max，A17 Pro 芯片，钛金属设计。</p>', publishStatus: 1, newStatus: 1, skuList: [
        { id: 3, skuCode: 'SKU003', price: 9999, spData: '[{"key":"颜色","value":"原色钛金属"},{"key":"内存","value":"256GB"}]', pic: '', stock: 150 }
      ], categoryName: '智能手机', brandName: '苹果' }
    }
    const product = products[id] || { ...products[1], id: parseInt(id), name: `商品${id}` }
    return { code: 200, message: 'success', data: product }
  },

  // 搜索
  'GET /api/pms/search': (params) => mockData['GET /api/pms/product/list'](params),

  // 登录
  'POST /api/ums/login': () => ({
    code: 200, message: '登录成功',
    data: { token: 'mock_portal_token_' + Date.now(), tokenHead: 'Bearer ' }
  }),

  // 注册
  'POST /api/ums/register': () => ({
    code: 200, message: '注册成功',
    data: { token: 'mock_portal_token_' + Date.now(), tokenHead: 'Bearer ' }
  }),

  // 当前用户
  'GET /api/ums/currentUser': () => ({
    code: 200, message: 'success',
    data: { id: 1, phone: '13800138000', nickname: '测试用户', avatar: '', gender: 1, birthday: '1990-01-01', createTime: '2026-05-14 10:00:00' }
  }),

  // 更新用户信息
  'PUT /api/ums/update': () => ({ code: 200, message: '更新成功', data: null }),

  // 地址列表
  'GET /api/ums/address/list': () => ({
    code: 200, message: 'success',
    data: [
      { id: 1, name: '张三', phone: '13800138000', province: '北京市', city: '北京市', district: '朝阳区', detailAddress: '某某街道123号', defaultStatus: 1 },
      { id: 2, name: '李四', phone: '13900139000', province: '上海市', city: '上海市', district: '浦东新区', detailAddress: '世纪大道456号', defaultStatus: 0 }
    ]
  }),

  // 添加地址
  'POST /api/ums/address/add': () => ({ code: 200, message: '添加成功', data: null }),

  // 更新地址
  'PUT /api/ums/address/update': () => ({ code: 200, message: '更新成功', data: null }),

  // 删除地址
  'DELETE /api/ums/address/': () => ({ code: 200, message: '删除成功', data: null }),

  // 设置默认地址
  'PUT /api/ums/address/default/': () => ({ code: 200, message: '设置成功', data: null }),

  // 购物车列表
  'GET /api/oms/cart/list': () => ({
    code: 200, message: 'success',
    data: [
      { id: 1, productId: 1, skuId: 1, quantity: 1, price: 6999, productName: '华为 Mate 60 Pro', productPic: '', spData: '[{"key":"颜色","value":"雅川青"},{"key":"内存","value":"12GB+512GB"}]', productStatus: 1, stock: 200 },
      { id: 2, productId: 2, skuId: 3, quantity: 1, price: 9999, productName: 'iPhone 15 Pro Max', productPic: '', spData: '[{"key":"颜色","value":"原色钛金属"},{"key":"内存","value":"256GB"}]', productStatus: 1, stock: 150 }
    ]
  }),

  // 添加购物车
  'POST /api/oms/cart/add': () => ({ code: 200, message: '添加成功', data: null }),

  // 更新购物车
  'PUT /api/oms/cart/update': () => ({ code: 200, message: '更新成功', data: null }),

  // 删除购物车
  'DELETE /api/oms/cart/': () => ({ code: 200, message: '删除成功', data: null }),

  // 清空购物车
  'DELETE /api/oms/cart/clear': () => ({ code: 200, message: '清空成功', data: null }),

  // 订单确认
  'POST /api/oms/order/generateConfirm': () => ({
    code: 200, message: 'success',
    data: {
      addressList: [
        { id: 1, name: '张三', phone: '13800138000', province: '北京市', city: '北京市', district: '朝阳区', detailAddress: '某某街道123号', defaultStatus: 1 }
      ],
      cartItemList: [
        { id: 1, productId: 1, skuId: 1, quantity: 1, price: 6999, productName: '华为 Mate 60 Pro', productPic: '', spData: '[{"key":"颜色","value":"雅川青"},{"key":"内存","value":"12GB+512GB"}]' }
      ],
      totalAmount: 6999, freightAmount: 0, payAmount: 6999
    }
  }),

  // 创建订单
  'POST /api/oms/order/create': () => ({
    code: 200, message: '创建成功',
    data: { orderId: 1, orderSn: '20260515100000001' }
  }),

  // 订单列表
  'GET /api/oms/order/list': (params) => {
    const allOrders = [
      { id: 1, orderSn: '20260515100000001', totalAmount: 6999, payAmount: 6999, status: 0, createTime: '2026-05-15 10:00:00', orderItemList: [{ productName: '华为 Mate 60 Pro', productPic: '', productPrice: 6999, productQuantity: 1, spData: '[{"key":"颜色","value":"雅川青"}]' }] },
      { id: 2, orderSn: '20260515100000002', totalAmount: 9999, payAmount: 9999, status: 1, createTime: '2026-05-15 09:00:00', orderItemList: [{ productName: 'iPhone 15 Pro Max', productPic: '', productPrice: 9999, productQuantity: 1, spData: '[{"key":"颜色","value":"原色钛金属"}]' }] },
      { id: 3, orderSn: '20260515100000003', totalAmount: 4999, payAmount: 4999, status: 2, createTime: '2026-05-14 15:00:00', orderItemList: [{ productName: '小米 14 Pro', productPic: '', productPrice: 4999, productQuantity: 1, spData: '[{"key":"颜色","value":"黑色"}]' }] },
      { id: 4, orderSn: '20260515100000004', totalAmount: 899, payAmount: 899, status: 3, createTime: '2026-05-13 10:00:00', orderItemList: [{ productName: 'Nike Air Max', productPic: '', productPrice: 899, productQuantity: 1, spData: '[]' }] }
    ]
    let records = allOrders
    if (params?.status !== undefined && params?.status !== '' && params?.status !== null) {
      records = records.filter(o => o.status == params.status)
    }
    return {
      code: 200, message: 'success',
      data: { total: records.length, pageNum: params?.pageNum || 1, pageSize: params?.pageSize || 10, records }
    }
  },

  // 订单详情
  'GET /api/oms/order/': (id) => ({
    code: 200, message: 'success',
    data: {
      id: parseInt(id), orderSn: '20260515100000001', status: 1, totalAmount: 6999, freightAmount: 0, payAmount: 6999,
      payType: 1, note: '尽快发货', receiverName: '张三', receiverPhone: '13800138000',
      receiverProvince: '北京市', receiverCity: '北京市', receiverDistrict: '朝阳区', receiverDetailAddress: '某某街道123号',
      payTime: '2026-05-15 10:05:00', deliveryTime: null, receiveTime: null, createTime: '2026-05-15 10:00:00',
      orderItemList: [{ productId: 1, productName: '华为 Mate 60 Pro', productPic: '', productSn: 'P20260514001', productPrice: 6999, productQuantity: 1, skuId: 1, spData: '[{"key":"颜色","value":"雅川青"}]' }]
    }
  }),

  // 取消订单
  'PUT /api/oms/order/cancel/': () => ({ code: 200, message: '取消成功', data: null }),

  // 确认收货
  'PUT /api/oms/order/confirmReceive/': () => ({ code: 200, message: '确认成功', data: null }),

  // 支付
  'POST /api/oms/pay/alipay': () => ({
    code: 200, message: 'success',
    data: { payUrl: 'https://sandbox.alipay.com/pay?mock=true' }
  }),

  // 支付状态
  'GET /api/oms/pay/status': () => ({ code: 200, message: 'success', data: { status: 1, message: '支付成功' } })
}

export function getMockResponse(config) {
  const method = config.method.toUpperCase()
  // Extract path from full URL (vite proxy merges baseURL)
  let url = config.url
  try {
    if (url.startsWith('http')) {
      url = new URL(url).pathname
    }
  } catch {}
  const key = `${method} ${url}`

  // 精确匹配
  if (mockData[key]) {
    const handler = mockData[key]
    return typeof handler === 'function' ? handler(config.params || {}) : handler
  }

  // 前缀匹配（用于带 ID 的路径）
  for (const mockKey of Object.keys(mockData)) {
    const parts = mockKey.split(' ')
    const mockMethod = parts[0]
    const mockPath = parts.slice(1).join(' ')
    if (mockMethod === method && mockPath && url.startsWith(mockPath.replace(/\/$/, ''))) {
      const id = url.replace(mockPath, '')
      const handler = mockData[mockKey]
      return typeof handler === 'function' ? handler(id) : handler
    }
  }

  return null
}
