// 后台管理 Mock 数据
let mockProducts = [
  { id: 1, name: '华为 Mate 60 Pro', subtitle: '旗舰智能手机', pic: '', price: 6999, originalPrice: 7999, stock: 500, sale: 1200, productSn: 'P20260514001', publishStatus: 1, newStatus: 1, categoryId: 11, brandId: 1, unit: '台' },
  { id: 2, name: 'iPhone 15 Pro Max', subtitle: '苹果旗舰手机', pic: '', price: 9999, originalPrice: 10999, stock: 300, sale: 800, productSn: 'P20260514002', publishStatus: 1, newStatus: 1, categoryId: 11, brandId: 3, unit: '台' },
  { id: 3, name: '小米 14 Pro', subtitle: '徕卡影像旗舰', pic: '', price: 4999, originalPrice: 5499, stock: 800, sale: 2000, productSn: 'P20260514003', publishStatus: 1, newStatus: 0, categoryId: 11, brandId: 2, unit: '台' },
  { id: 4, name: 'MacBook Pro 14', subtitle: 'M3 Pro 芯片', pic: '', price: 14999, originalPrice: 16999, stock: 150, sale: 300, productSn: 'P20260514004', publishStatus: 1, newStatus: 1, categoryId: 21, brandId: 3, unit: '台' },
  { id: 5, name: '华为 MateBook X Pro', subtitle: '轻薄商务本', pic: '', price: 8999, originalPrice: 9999, stock: 400, sale: 500, productSn: 'P20260514005', publishStatus: 0, newStatus: 0, categoryId: 21, brandId: 1, unit: '台' },
  { id: 6, name: 'Nike Air Max', subtitle: '经典运动鞋', pic: '', price: 899, originalPrice: 1099, stock: 2000, sale: 3000, productSn: 'P20260514006', publishStatus: 1, newStatus: 0, categoryId: 6, brandId: 4, unit: '双' },
  { id: 7, name: 'Adidas Ultraboost', subtitle: '缓震跑鞋', pic: '', price: 1099, originalPrice: 1299, stock: 1500, sale: 1500, productSn: 'P20260514007', publishStatus: 1, newStatus: 0, categoryId: 6, brandId: 5, unit: '双' },
  { id: 8, name: '优衣库 HEATTECH', subtitle: '保暖内衣', pic: '', price: 99, originalPrice: 149, stock: 10000, sale: 5000, productSn: 'P20260514008', publishStatus: 1, newStatus: 0, categoryId: 32, brandId: 6, unit: '件' },
  { id: 9, name: 'iPad Pro 12.9', subtitle: 'M2 芯片平板', pic: '', price: 8999, originalPrice: 9999, stock: 350, sale: 400, productSn: 'P20260514009', publishStatus: 1, newStatus: 1, categoryId: 12, brandId: 3, unit: '台' },
  { id: 10, name: '小米平板 6 Pro', subtitle: '骁龙8+ 平板', pic: '', price: 2399, originalPrice: 2799, stock: 900, sale: 900, productSn: 'P20260514010', publishStatus: 1, newStatus: 1, categoryId: 12, brandId: 2, unit: '台' }
]

let mockCategories = [
  { id: 1, parentId: 0, name: '手机数码', icon: '', sort: 1, showStatus: 1, level: 0 },
  { id: 11, parentId: 1, name: '智能手机', icon: '', sort: 1, showStatus: 1, level: 1 },
  { id: 12, parentId: 1, name: '平板电脑', icon: '', sort: 2, showStatus: 1, level: 1 },
  { id: 2, parentId: 0, name: '电脑办公', icon: '', sort: 2, showStatus: 1, level: 0 },
  { id: 21, parentId: 2, name: '笔记本', icon: '', sort: 1, showStatus: 1, level: 1 },
  { id: 22, parentId: 2, name: '台式机', icon: '', sort: 2, showStatus: 1, level: 1 },
  { id: 3, parentId: 0, name: '服装服饰', icon: '', sort: 3, showStatus: 1, level: 0 },
  { id: 31, parentId: 3, name: '男装', icon: '', sort: 1, showStatus: 1, level: 1 },
  { id: 32, parentId: 3, name: '女装', icon: '', sort: 2, showStatus: 1, level: 1 },
  { id: 4, parentId: 0, name: '家居日用', icon: '', sort: 4, showStatus: 1, level: 0 },
  { id: 5, parentId: 0, name: '美妆护肤', icon: '', sort: 5, showStatus: 1, level: 0 },
  { id: 6, parentId: 0, name: '运动户外', icon: '', sort: 6, showStatus: 1, level: 0 }
]

let mockBrands = [
  { id: 1, name: '华为', firstLetter: 'H', logo: '', description: '华为技术有限公司', recommendStatus: 1, sort: 1 },
  { id: 2, name: '小米', firstLetter: 'X', logo: '', description: '小米科技有限责任公司', recommendStatus: 1, sort: 2 },
  { id: 3, name: '苹果', firstLetter: 'A', logo: '', description: 'Apple Inc.', recommendStatus: 1, sort: 3 },
  { id: 4, name: '耐克', firstLetter: 'N', logo: '', description: 'NIKE', recommendStatus: 1, sort: 4 },
  { id: 5, name: '阿迪达斯', firstLetter: 'A', logo: '', description: 'Adidas', recommendStatus: 0, sort: 5 },
  { id: 6, name: '优衣库', firstLetter: 'Y', logo: '', description: 'UNIQLO', recommendStatus: 1, sort: 6 }
]

let mockOrders = [
  { id: 1, orderSn: '20260515100000001', totalAmount: 6999, payAmount: 6999, status: 0, createTime: '2026-05-15 10:00:00', payType: 1, note: '', receiverName: '张三', receiverPhone: '13800138000', receiverProvince: '北京市', receiverCity: '北京市', receiverDistrict: '朝阳区', receiverDetailAddress: '某某街道123号', orderItemList: [{ productName: '华为 Mate 60 Pro', productPic: '', productPrice: 6999, productQuantity: 1 }] },
  { id: 2, orderSn: '20260515100000002', totalAmount: 9999, payAmount: 9999, status: 1, createTime: '2026-05-15 09:00:00', payType: 1, note: '', receiverName: '李四', receiverPhone: '13900139000', receiverProvince: '上海市', receiverCity: '上海市', receiverDistrict: '浦东新区', receiverDetailAddress: '世纪大道456号', orderItemList: [{ productName: 'iPhone 15 Pro Max', productPic: '', productPrice: 9999, productQuantity: 1 }] },
  { id: 3, orderSn: '20260515100000003', totalAmount: 4999, payAmount: 4999, status: 2, createTime: '2026-05-14 15:00:00', payType: 1, note: '', receiverName: '王五', receiverPhone: '13700137000', receiverProvince: '广州市', receiverCity: '广州市', receiverDistrict: '天河区', receiverDetailAddress: '体育西路789号', orderItemList: [{ productName: '小米 14 Pro', productPic: '', productPrice: 4999, productQuantity: 1 }] },
  { id: 4, orderSn: '20260515100000004', totalAmount: 899, payAmount: 899, status: 3, createTime: '2026-05-13 10:00:00', payType: 1, note: '', receiverName: '赵六', receiverPhone: '13600136000', receiverProvince: '深圳市', receiverCity: '深圳市', receiverDistrict: '南山区', receiverDetailAddress: '科技园101号', orderItemList: [{ productName: 'Nike Air Max', productPic: '', productPrice: 899, productQuantity: 1 }] },
  { id: 5, orderSn: '20260515100000005', totalAmount: 1099, payAmount: 1099, status: 4, createTime: '2026-05-12 10:00:00', payType: 1, note: '', receiverName: '钱七', receiverPhone: '13500135000', receiverProvince: '杭州市', receiverCity: '杭州市', receiverDistrict: '西湖区', receiverDetailAddress: '文三路222号', orderItemList: [{ productName: 'Adidas Ultraboost', productPic: '', productPrice: 1099, productQuantity: 1 }] },
  { id: 6, orderSn: '20260515100000006', totalAmount: 14999, payAmount: 14999, status: 5, createTime: '2026-05-11 10:00:00', payType: 1, note: '', receiverName: '孙八', receiverPhone: '13400134000', receiverProvince: '成都市', receiverCity: '成都市', receiverDistrict: '高新区', receiverDetailAddress: '天府大道333号', orderItemList: [{ productName: 'MacBook Pro 14', productPic: '', productPrice: 14999, productQuantity: 1 }] },
  { id: 7, orderSn: '20260515100000007', totalAmount: 2399, payAmount: 2399, status: 6, createTime: '2026-05-10 10:00:00', payType: 1, note: '', receiverName: '周九', receiverPhone: '13300133000', receiverProvince: '武汉市', receiverCity: '武汉市', receiverDistrict: '洪山区', receiverDetailAddress: '珞喻路444号', orderItemList: [{ productName: '小米平板 6 Pro', productPic: '', productPrice: 2399, productQuantity: 1 }] }
]

let mockMembers = [
  { id: 1, phone: '13800138000', nickname: '张三', avatar: '', gender: 1, birthday: '1990-01-01', status: 1, createTime: '2026-05-01 10:00:00', loginTime: '2026-05-15 09:00:00' },
  { id: 2, phone: '13900139000', nickname: '李四', avatar: '', gender: 2, birthday: '1995-03-15', status: 1, createTime: '2026-05-02 11:00:00', loginTime: '2026-05-14 15:00:00' },
  { id: 3, phone: '13700137000', nickname: '王五', avatar: '', gender: 1, birthday: '1988-07-20', status: 1, createTime: '2026-05-03 12:00:00', loginTime: '2026-05-13 10:00:00' },
  { id: 4, phone: '13600136000', nickname: '赵六', avatar: '', gender: 2, birthday: '1992-11-08', status: 0, createTime: '2026-05-04 13:00:00', loginTime: '2026-05-12 08:00:00' },
  { id: 5, phone: '13500135000', nickname: '钱七', avatar: '', gender: 1, birthday: '1993-05-22', status: 1, createTime: '2026-05-05 14:00:00', loginTime: '2026-05-11 16:00:00' }
]

const mockData = {
  // 登录
  'POST /api/admin/login': (data) => {
    if (data.username === 'admin' && data.password === '123456') {
      return { code: 200, message: '登录成功', data: { token: 'mock_admin_token_' + Date.now(), tokenHead: 'Bearer ' } }
    }
    return { code: 401, message: '用户名或密码错误', data: null }
  },

  // 管理员信息
  'GET /api/admin/info': () => ({
    code: 200, message: 'success',
    data: { id: 1, username: 'admin', nickname: '管理员', avatar: '', roles: ['admin'] }
  }),

  // 登出
  'POST /api/admin/logout': () => ({ code: 200, message: '登出成功', data: null }),

  // 商品列表
  'GET /api/admin/product/list': (params) => {
    let records = [...mockProducts]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      records = records.filter(p => p.name.toLowerCase().includes(kw))
    }
    if (params?.categoryId) {
      records = records.filter(p => p.categoryId == params.categoryId)
    }
    if (params?.publishStatus !== undefined && params?.publishStatus !== '' && params?.publishStatus !== null) {
      records = records.filter(p => p.publishStatus == params.publishStatus)
    }
    const pageNum = parseInt(params?.pageNum) || 1
    const pageSize = parseInt(params?.pageSize) || 10
    const start = (pageNum - 1) * pageSize
    return {
      code: 200, message: 'success',
      data: { total: records.length, pageNum, pageSize, records: records.slice(start, start + pageSize) }
    }
  },

  // 创建商品
  'POST /api/admin/product/create': (data) => {
    const newProduct = { id: Date.now(), ...data, sale: 0 }
    mockProducts.push(newProduct)
    return { code: 200, message: '创建成功', data: newProduct.id }
  },

  // 更新商品
  'PUT /api/admin/product/update': (data) => {
    const idx = mockProducts.findIndex(p => p.id === data.id)
    if (idx >= 0) {
      mockProducts[idx] = { ...mockProducts[idx], ...data }
    }
    return { code: 200, message: '更新成功', data: null }
  },

  // 商品详情
  'GET /api/admin/product/': (id) => {
    const product = mockProducts.find(p => p.id == id)
    return { code: 200, message: 'success', data: product || null }
  },

  // 删除商品
  'DELETE /api/admin/product/': (id) => {
    mockProducts = mockProducts.filter(p => p.id != id)
    return { code: 200, message: '删除成功', data: null }
  },

  // 批量删除商品
  'DELETE /api/admin/product/batchDelete': (data) => {
    const ids = data.ids || []
    mockProducts = mockProducts.filter(p => !ids.includes(p.id))
    return { code: 200, message: '删除成功', data: null }
  },

  // 更新上架状态
  'PUT /api/admin/product/updatePublishStatus': (data) => {
    const ids = data.ids || (data.id ? [data.id] : [])
    ids.forEach(id => {
      const idx = mockProducts.findIndex(p => p.id === id)
      if (idx >= 0) mockProducts[idx].publishStatus = data.publishStatus
    })
    return { code: 200, message: '更新成功', data: null }
  },

  // 分类列表
  'GET /api/admin/category/list': () => ({
    code: 200, message: 'success', data: mockCategories
  }),

  // 创建分类
  'POST /api/admin/category/create': (data) => {
    const newCat = { id: Date.now(), ...data }
    mockCategories.push(newCat)
    return { code: 200, message: '创建成功', data: newCat.id }
  },

  // 更新分类
  'PUT /api/admin/category/update': (data) => {
    const idx = mockCategories.findIndex(c => c.id === data.id)
    if (idx >= 0) mockCategories[idx] = { ...mockCategories[idx], ...data }
    return { code: 200, message: '更新成功', data: null }
  },

  // 删除分类
  'DELETE /api/admin/category/': (id) => {
    mockCategories = mockCategories.filter(c => c.id != id && c.parentId != id)
    return { code: 200, message: '删除成功', data: null }
  },

  // 品牌列表
  'GET /api/admin/brand/list': (params) => {
    let records = [...mockBrands]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      records = records.filter(b => b.name.toLowerCase().includes(kw))
    }
    const pageNum = parseInt(params?.pageNum) || 1
    const pageSize = parseInt(params?.pageSize) || 10
    const start = (pageNum - 1) * pageSize
    return {
      code: 200, message: 'success',
      data: { total: records.length, pageNum, pageSize, records: records.slice(start, start + pageSize) }
    }
  },

  // 创建品牌
  'POST /api/admin/brand/create': (data) => {
    const newBrand = { id: Date.now(), ...data }
    mockBrands.push(newBrand)
    return { code: 200, message: '创建成功', data: newBrand.id }
  },

  // 更新品牌
  'PUT /api/admin/brand/update': (data) => {
    const idx = mockBrands.findIndex(b => b.id === data.id)
    if (idx >= 0) mockBrands[idx] = { ...mockBrands[idx], ...data }
    return { code: 200, message: '更新成功', data: null }
  },

  // 删除品牌
  'DELETE /api/admin/brand/': (id) => {
    mockBrands = mockBrands.filter(b => b.id != id)
    return { code: 200, message: '删除成功', data: null }
  },

  // 订单列表
  'GET /api/admin/order/list': (params) => {
    let records = [...mockOrders]
    if (params?.orderSn) {
      records = records.filter(o => o.orderSn.includes(params.orderSn))
    }
    if (params?.status !== undefined && params?.status !== '' && params?.status !== null) {
      records = records.filter(o => o.status == params.status)
    }
    const pageNum = parseInt(params?.pageNum) || 1
    const pageSize = parseInt(params?.pageSize) || 10
    const start = (pageNum - 1) * pageSize
    return {
      code: 200, message: 'success',
      data: { total: records.length, pageNum, pageSize, records: records.slice(start, start + pageSize) }
    }
  },

  // 订单详情
  'GET /api/admin/order/': (id) => {
    const order = mockOrders.find(o => o.id == id) || mockOrders[0]
    return { code: 200, message: 'success', data: order }
  },

  // 发货
  'POST /api/admin/order/deliver': (data) => {
    const idx = mockOrders.findIndex(o => o.id === data.orderId)
    if (idx >= 0) mockOrders[idx].status = 2
    return { code: 200, message: '发货成功', data: null }
  },

  // 退款
  'POST /api/admin/order/refund': (data) => {
    const idx = mockOrders.findIndex(o => o.id === data.orderId)
    if (idx >= 0) mockOrders[idx].status = 6
    return { code: 200, message: '退款成功', data: null }
  },

  // 会员列表
  'GET /api/admin/member/list': (params) => {
    let records = [...mockMembers]
    if (params?.keyword) {
      const kw = params.keyword.toLowerCase()
      records = records.filter(m => m.phone.includes(kw) || (m.nickname && m.nickname.toLowerCase().includes(kw)))
    }
    const pageNum = parseInt(params?.pageNum) || 1
    const pageSize = parseInt(params?.pageSize) || 10
    const start = (pageNum - 1) * pageSize
    return {
      code: 200, message: 'success',
      data: { total: records.length, pageNum, pageSize, records: records.slice(start, start + pageSize) }
    }
  },

  // 会员详情
  'GET /api/admin/member/': (id) => {
    const member = mockMembers.find(m => m.id == id) || mockMembers[0]
    return { code: 200, message: 'success', data: member }
  },

  // 订单统计
  'GET /api/admin/stat/order': (params) => {
    const type = params?.type || 'day'
    const trend = [
      { date: type === 'day' ? '05-10' : '第1周', count: 12, amount: 45000 },
      { date: type === 'day' ? '05-11' : '第2周', count: 18, amount: 62000 },
      { date: type === 'day' ? '05-12' : '第3周', count: 15, amount: 51000 },
      { date: type === 'day' ? '05-13' : '第4周', count: 22, amount: 78000 },
      { date: type === 'day' ? '05-14' : '第5周', count: 28, amount: 95000 },
      { date: type === 'day' ? '05-15' : '第6周', count: 35, amount: 120000 }
    ]
    return {
      code: 200, message: 'success',
      data: { orderCount: 130, totalAmount: 451000, refundRate: 0.05, trend }
    }
  },

  // 商品统计
  'GET /api/admin/stat/product': () => ({
    code: 200, message: 'success',
    data: {
      hotProducts: [
        { productName: '华为 Mate 60 Pro', sale: 1200, amount: 8398800 },
        { productName: '小米 14 Pro', sale: 2000, amount: 9998000 },
        { productName: 'Nike Air Max', sale: 3000, amount: 2697000 },
        { productName: 'iPhone 15 Pro Max', sale: 800, amount: 7999200 },
        { productName: '优衣库 HEATTECH', sale: 5000, amount: 495000 }
      ]
    }
  }),

  // 用户统计
  'GET /api/admin/stat/user': () => ({
    code: 200, message: 'success',
    data: {
      newUserCount: 45, activeUserCount: 320, totalUserCount: 1250,
      trend: [
        { date: '05-10', newCount: 8, activeCount: 200 },
        { date: '05-11', newCount: 12, activeCount: 220 },
        { date: '05-12', newCount: 10, activeCount: 250 },
        { date: '05-13', newCount: 15, activeCount: 280 },
        { date: '05-14', newCount: 18, activeCount: 300 },
        { date: '05-15', newCount: 22, activeCount: 320 }
      ]
    }
  }),

  // 图片上传
  'POST /api/upload/image': () => ({
    code: 200, message: '上传成功',
    data: { url: 'https://via.placeholder.com/300x300/DC2626/FFFFFF?text=Mock+Image' }
  })
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
    const payload = config.data ? (typeof config.data === 'string' ? JSON.parse(config.data) : config.data) : {}
    return typeof handler === 'function' ? handler(payload) : handler
  }

  // 前缀匹配（用于带 ID 的路径）
  for (const mockKey of Object.keys(mockData)) {
    const parts = mockKey.split(' ')
    const mockMethod = parts[0]
    const mockPath = parts.slice(1).join(' ')
    if (mockMethod === method && mockPath && url.startsWith(mockPath.replace(/\/$/, ''))) {
      const id = url.replace(mockPath, '')
      const handler = mockData[mockKey]
      const payload = config.data ? (typeof config.data === 'string' ? JSON.parse(config.data) : config.data) : {}
      return typeof handler === 'function' ? handler(id, payload) : handler
    }
  }

  return null
}
