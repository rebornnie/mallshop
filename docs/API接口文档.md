# MallShop API 接口文档

## 通用约定

### Base URL
- 前台商城：`http://localhost:8080`
- 后台管理：`http://localhost:8080`

### 认证方式
- Header：`Authorization: Bearer {token}`
- 前台Token有效期：7天
- 后台Token有效期：8小时

### 统一响应格式

**成功响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**分页响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 20,
    "records": []
  }
}
```

**错误码：** 401未认证 | 403无权限 | 500服务器错误

### 分页参数
- `pageNum`：页码，默认1
- `pageSize`：每页条数，默认20

### 时间格式
- `yyyy-MM-dd HH:mm:ss`

---

## 一、前台用户模块（/api/ums）

### 1.1 用户注册

**POST** `/api/ums/register`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码(8-20位字母+数字) |
| authCode | string | 是 | 短信验证码 |

**响应data：**
```json
{
  "token": "eyJhbGci...",
  "tokenHead": "Bearer "
}
```

---

### 1.2 用户登录

**POST** `/api/ums/login`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 是 | 手机号 |
| password | string | 是 | 密码 |

**响应data：**
```json
{
  "token": "eyJhbGci...",
  "tokenHead": "Bearer "
}
```

---

### 1.3 获取当前用户信息

**GET** `/api/ums/currentUser` 🔒

**响应data：**
```json
{
  "id": 1,
  "phone": "13800138000",
  "nickname": "用户昵称",
  "avatar": "https://xxx/avatar.jpg",
  "gender": 1,
  "birthday": "2000-01-01",
  "createTime": "2026-05-14 10:00:00"
}
```

---

### 1.4 更新用户信息

**PUT** `/api/ums/update` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | string | 否 | 昵称(2-20字符) |
| avatar | string | 否 | 头像URL |
| gender | integer | 否 | 性别 0未知 1男 2女 |
| birthday | string | 否 | 生日 |

---

### 1.5 修改密码

**PUT** `/api/ums/updatePassword` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| oldPassword | string | 是 | 原密码 |
| newPassword | string | 是 | 新密码(8-20位字母+数字) |

---

## 二、收货地址模块（/api/ums/address）

### 2.1 收货地址列表

**GET** `/api/ums/address/list` 🔒

**响应data：**
```json
[
  {
    "id": 1,
    "name": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detailAddress": "某某街道123号",
    "defaultStatus": 1
  }
]
```

---

### 2.2 新增收货地址

**POST** `/api/ums/address/add` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 收货人姓名 |
| phone | string | 是 | 收货人手机号 |
| province | string | 是 | 省 |
| city | string | 是 | 市 |
| district | string | 是 | 区 |
| detailAddress | string | 是 | 详细地址 |
| defaultStatus | integer | 否 | 是否默认 0否 1是 |

---

### 2.3 更新收货地址

**PUT** `/api/ums/address/update` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 地址ID |
| name | string | 否 | 收货人姓名 |
| phone | string | 否 | 收货人手机号 |
| province | string | 否 | 省 |
| city | string | 否 | 市 |
| district | string | 否 | 区 |
| detailAddress | string | 否 | 详细地址 |
| defaultStatus | integer | 否 | 是否默认 |

---

### 2.4 删除收货地址

**DELETE** `/api/ums/address/{id}` 🔒

---

### 2.5 设置默认地址

**PUT** `/api/ums/address/default/{id}` 🔒

---

## 三、商品模块（/api/pms）

### 3.1 商品列表

**GET** `/api/pms/product/list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认20 |
| categoryId | long | 否 | 分类ID |
| brandId | long | 否 | 品牌ID |
| keyword | string | 否 | 搜索关键词 |
| sort | string | 否 | 排序字段: sale/price/createTime |

**响应data.records项：**
```json
{
  "id": 1,
  "name": "商品名称",
  "subtitle": "副标题",
  "pic": "https://xxx/product.jpg",
  "price": 99.00,
  "originalPrice": 199.00,
  "sale": 100,
  "brandId": 1,
  "categoryId": 1
}
```

---

### 3.2 商品详情

**GET** `/api/pms/product/{id}`

**响应data：**
```json
{
  "id": 1,
  "name": "商品名称",
  "subtitle": "副标题",
  "categoryId": 1,
  "brandId": 1,
  "productSn": "P20260514001",
  "pic": "https://xxx/main.jpg",
  "pics": "url1,url2,url3",
  "price": 99.00,
  "originalPrice": 199.00,
  "stock": 500,
  "sale": 100,
  "unit": "件",
  "description": "<p>富文本商品详情</p>",
  "publishStatus": 1,
  "newStatus": 1,
  "skuList": [
    {
      "id": 1,
      "skuCode": "SKU001",
      "price": 99.00,
      "spData": "{\"颜色\":\"红色\",\"尺码\":\"M\"}",
      "pic": "https://xxx/sku.jpg",
      "stock": 50
    }
  ],
  "categoryName": "分类名称",
  "brandName": "品牌名称"
}
```

---

### 3.3 分类列表

**GET** `/api/pms/category/list`

**响应data：**
```json
[
  {
    "id": 1,
    "parentId": 0,
    "name": "服装",
    "icon": "https://xxx/icon.png",
    "sort": 1,
    "showStatus": 1,
    "children": [
      {
        "id": 2,
        "parentId": 1,
        "name": "男装",
        "icon": "",
        "sort": 1,
        "showStatus": 1,
        "children": []
      }
    ]
  }
]
```

---

### 3.4 品牌列表

**GET** `/api/pms/brand/list`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应data.records项：**
```json
{
  "id": 1,
  "name": "品牌名称",
  "firstLetter": "N",
  "logo": "https://xxx/logo.png",
  "description": "品牌描述",
  "recommendStatus": 1,
  "sort": 1
}
```

---

### 3.5 商品搜索

**GET** `/api/pms/search`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |
| categoryId | long | 否 | 分类ID |
| brandId | long | 否 | 品牌ID |
| sort | string | 否 | 排序: sale/price |

**响应data：** 同商品列表分页格式

---

## 四、购物车模块（/api/oms/cart）

### 4.1 添加购物车

**POST** `/api/oms/cart/add` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productId | long | 是 | 商品ID |
| skuId | long | 是 | SKU ID |
| quantity | int | 是 | 数量(1-99) |

---

### 4.2 购物车列表

**GET** `/api/oms/cart/list` 🔒

**响应data：**
```json
[
  {
    "id": 1,
    "productId": 1,
    "skuId": 1,
    "quantity": 2,
    "price": 99.00,
    "productName": "商品名称",
    "productPic": "https://xxx/product.jpg",
    "spData": "{\"颜色\":\"红色\"}",
    "productStatus": 1,
    "stock": 50
  }
]
```

> `productStatus`: 1正常 0下架；`stock`为当前SKU可用库存

---

### 4.3 更新购物车

**PUT** `/api/oms/cart/update` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 购物车项ID |
| quantity | int | 是 | 新数量 |

---

### 4.4 删除购物车项

**DELETE** `/api/oms/cart/{id}` 🔒

---

### 4.5 清空购物车

**DELETE** `/api/oms/cart/clear` 🔒

---

## 五、订单模块（/api/oms/order）

### 5.1 生成订单确认页信息

**POST** `/api/oms/order/generateConfirm` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| cartIds | long[] | 是 | 购物车项ID列表 |

**响应data：**
```json
{
  "addressList": [
    {
      "id": 1,
      "name": "张三",
      "phone": "13800138000",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detailAddress": "某某街道123号",
      "defaultStatus": 1
    }
  ],
  "cartItemList": [
    {
      "id": 1,
      "productId": 1,
      "skuId": 1,
      "quantity": 2,
      "price": 99.00,
      "productName": "商品名称",
      "productPic": "https://xxx/product.jpg",
      "spData": "{\"颜色\":\"红色\"}"
    }
  ],
  "totalAmount": 198.00,
  "freightAmount": 0.00,
  "payAmount": 198.00
}
```

---

### 5.2 创建订单

**POST** `/api/oms/order/create` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| addressId | long | 是 | 收货地址ID |
| cartIds | long[] | 是 | 购物车项ID列表 |
| payType | int | 是 | 支付方式 1支付宝 |
| note | string | 否 | 订单备注(≤200字) |

**响应data：**
```json
{
  "orderId": 1,
  "orderSn": "20260514100000001"
}
```

---

### 5.3 订单列表

**GET** `/api/oms/order/list` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |
| status | int | 否 | 订单状态筛选 |

**订单状态：** 0待支付 1已支付 2已发货 3已完成 4已取消 5退款中 6已退款

**响应data.records项：**
```json
{
  "id": 1,
  "orderSn": "20260514100000001",
  "totalAmount": 198.00,
  "payAmount": 198.00,
  "status": 1,
  "createTime": "2026-05-14 10:00:00",
  "orderItemList": [
    {
      "productName": "商品名称",
      "productPic": "https://xxx/product.jpg",
      "productPrice": 99.00,
      "productQuantity": 2,
      "spData": "{\"颜色\":\"红色\"}"
    }
  ]
}
```

---

### 5.4 订单详情

**GET** `/api/oms/order/{id}` 🔒

**响应data：**
```json
{
  "id": 1,
  "orderSn": "20260514100000001",
  "status": 1,
  "totalAmount": 198.00,
  "freightAmount": 0.00,
  "payAmount": 198.00,
  "payType": 1,
  "note": "尽快发货",
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "receiverProvince": "北京市",
  "receiverCity": "北京市",
  "receiverDistrict": "朝阳区",
  "receiverDetailAddress": "某某街道123号",
  "payTime": "2026-05-14 10:05:00",
  "deliveryTime": null,
  "receiveTime": null,
  "createTime": "2026-05-14 10:00:00",
  "orderItemList": [
    {
      "productId": 1,
      "productName": "商品名称",
      "productPic": "https://xxx/product.jpg",
      "productSn": "P20260514001",
      "productPrice": 99.00,
      "productQuantity": 2,
      "skuId": 1,
      "spData": "{\"颜色\":\"红色\"}"
    }
  ]
}
```

---

### 5.5 取消订单

**PUT** `/api/oms/order/cancel/{id}` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| reason | string | 否 | 取消原因 |

> 仅待支付状态可取消

---

### 5.6 确认收货

**PUT** `/api/oms/order/confirmReceive/{id}` 🔒

> 仅已发货状态可确认

---

## 六、支付模块（/api/oms/pay）

### 6.1 发起支付宝支付

**POST** `/api/oms/pay/alipay` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 订单ID |

**响应data：**
```json
{
  "payUrl": "https://openapi.alipay.com/gateway.do?..."
}
```

> 前端跳转至payUrl完成支付

---

### 6.2 支付宝异步回调

**POST** `/api/oms/pay/callback`

> 由支付宝服务器调用，非前端调用。验签后更新订单状态。

---

### 6.3 查询支付状态

**GET** `/api/oms/pay/status` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 订单ID |

**响应data：**
```json
{
  "status": 1,
  "message": "支付成功"
}
```

> status: 0待支付 1已支付

---

## 七、后台登录模块（/api/admin）

### 7.1 管理员登录

**POST** `/api/admin/login`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**响应data：**
```json
{
  "token": "eyJhbGci...",
  "tokenHead": "Bearer "
}
```

---

### 7.2 获取管理员信息+菜单

**GET** `/api/admin/info` 🔒

**响应data：**
```json
{
  "id": 1,
  "username": "admin",
  "nickname": "超级管理员",
  "avatar": "https://xxx/avatar.jpg",
  "roles": ["超级管理员"],
  "menuList": [
    {
      "id": 1,
      "parentId": 0,
      "name": "商品管理",
      "url": "/pms",
      "icon": "shopping",
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "name": "商品列表",
          "url": "/pms/product",
          "icon": "product",
          "children": []
        }
      ]
    }
  ]
}
```

---

## 八、后台商品管理（/api/admin/product）

### 8.1 商品列表

**GET** `/api/admin/product/list` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |
| keyword | string | 否 | 商品名称搜索 |
| categoryId | long | 否 | 分类ID |
| brandId | long | 否 | 品牌ID |
| publishStatus | int | 否 | 上下架 0下架 1上架 |

---

### 8.2 添加商品

**POST** `/api/admin/product/create` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | long | 是 | 分类ID |
| brandId | long | 是 | 品牌ID |
| name | string | 是 | 商品名称 |
| subtitle | string | 否 | 副标题 |
| productSn | string | 否 | 商品编码 |
| pic | string | 否 | 主图URL |
| pics | string | 否 | 图片URL列表(逗号分隔) |
| price | decimal | 是 | 零售价 |
| originalPrice | decimal | 否 | 划线价 |
| costPrice | decimal | 否 | 成本价 |
| stock | int | 是 | 总库存 |
| unit | string | 否 | 单位 |
| description | string | 否 | 商品详情(富文本) |
| publishStatus | int | 否 | 0下架 1上架 |
| newStatus | int | 否 | 新品标 |
| recommendStatus | int | 否 | 推荐标 |
| sort | int | 否 | 排序 |
| skuList | array | 否 | SKU列表 |

**skuList项结构：**
```json
{
  "skuCode": "SKU001",
  "price": 99.00,
  "spData": "{\"颜色\":\"红色\"}",
  "pic": "https://xxx/sku.jpg",
  "stock": 50
}
```

---

### 8.3 更新商品

**PUT** `/api/admin/product/update` 🔒

> 参数同添加商品，额外需要 `id` 字段

---

### 8.4 删除商品

**DELETE** `/api/admin/product/{id}` 🔒

---

### 8.5 更新上下架状态

**PUT** `/api/admin/product/updatePublishStatus` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | long[] | 是 | 商品ID列表 |
| publishStatus | int | 是 | 0下架 1上架 |

---

### 8.6 批量删除

**DELETE** `/api/admin/product/batchDelete` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | long[] | 是 | 商品ID列表 |

---

## 九、后台分类管理（/api/admin/category）

### 9.1 分类列表

**GET** `/api/admin/category/list` 🔒

**响应data：** 树形结构，同前台分类列表

---

### 9.2 添加分类

**POST** `/api/admin/category/create` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| parentId | long | 是 | 父分类ID(0为一级) |
| name | string | 是 | 分类名称 |
| icon | string | 否 | 分类图标 |
| sort | int | 否 | 排序值 |
| showStatus | int | 否 | 显示状态 0隐藏 1显示 |

---

### 9.3 更新分类

**PUT** `/api/admin/category/update` 🔒

> 参数同添加分类，额外需要 `id` 字段

---

### 9.4 删除分类

**DELETE** `/api/admin/category/{id}` 🔒

> 有商品关联时禁止删除

---

## 十、后台品牌管理（/api/admin/brand）

### 10.1 品牌列表

**GET** `/api/admin/brand/list` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |
| keyword | string | 否 | 品牌名称搜索 |

---

### 10.2 添加品牌

**POST** `/api/admin/brand/create` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 品牌名称 |
| firstLetter | string | 否 | 首字母 |
| logo | string | 否 | 品牌Logo URL |
| description | string | 否 | 品牌描述 |
| recommendStatus | int | 否 | 推荐状态 0否 1是 |
| sort | int | 否 | 排序值 |

---

### 10.3 更新品牌

**PUT** `/api/admin/brand/update` 🔒

> 参数同添加品牌，额外需要 `id` 字段

---

### 10.4 删除品牌

**DELETE** `/api/admin/brand/{id}` 🔒

---

## 十一、后台订单管理（/api/admin/order）

### 11.1 订单列表

**GET** `/api/admin/order/list` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |
| orderSn | string | 否 | 订单编号搜索 |
| status | int | 否 | 订单状态 |
| receiverPhone | string | 否 | 收货人手机号 |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |

---

### 11.2 订单详情

**GET** `/api/admin/order/{id}` 🔒

> 同前台订单详情，额外包含会员信息

---

### 11.3 订单发货

**POST** `/api/admin/order/deliver` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 订单ID |
| deliveryCompany | string | 是 | 物流公司 |
| deliverySn | string | 是 | 运单号 |

---

### 11.4 退款审批

**POST** `/api/admin/order/refund` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 订单ID |
| approved | boolean | 是 | true通过 false拒绝 |
| note | string | 否 | 审批备注 |

---

## 十二、后台会员管理（/api/admin/member）

### 12.1 会员列表

**GET** `/api/admin/member/list` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |
| keyword | string | 否 | 手机号/昵称搜索 |

**响应data.records项：**
```json
{
  "id": 1,
  "phone": "13800138000",
  "nickname": "用户昵称",
  "avatar": "https://xxx/avatar.jpg",
  "status": 1,
  "createTime": "2026-05-14 10:00:00",
  "loginTime": "2026-05-14 15:00:00"
}
```

---

### 12.2 会员详情

**GET** `/api/admin/member/{id}` 🔒

**响应data：**
```json
{
  "id": 1,
  "phone": "13800138000",
  "nickname": "用户昵称",
  "avatar": "https://xxx/avatar.jpg",
  "gender": 1,
  "birthday": "2000-01-01",
  "status": 1,
  "createTime": "2026-05-14 10:00:00",
  "orderCount": 10,
  "totalConsume": 1980.00
}
```

---

## 十三、后台数据统计（/api/admin/stat）

### 13.1 订单统计

**GET** `/api/admin/stat/order` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 否 | day/week/month |

**响应data：**
```json
{
  "orderCount": 100,
  "totalAmount": 29800.00,
  "refundRate": 0.02,
  "trend": [
    { "date": "2026-05-01", "count": 10, "amount": 2980.00 }
  ]
}
```

---

### 13.2 商品统计

**GET** `/api/admin/stat/product` 🔒

**响应data：**
```json
{
  "hotProducts": [
    { "productId": 1, "productName": "商品名称", "sale": 500, "amount": 49500.00 }
  ]
}
```

---

### 13.3 用户统计

**GET** `/api/admin/stat/user` 🔒

**响应data：**
```json
{
  "newUserCount": 50,
  "activeUserCount": 200,
  "totalUserCount": 1000,
  "trend": [
    { "date": "2026-05-01", "newCount": 5, "activeCount": 20 }
  ]
}
```

---

## 十四、后台系统管理（/api/admin/sys）

### 14.1 管理员CRUD

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/sys/admin/list` | 管理员列表 |
| POST | `/api/admin/sys/admin/create` | 添加管理员 |
| PUT | `/api/admin/sys/admin/update` | 更新管理员 |
| DELETE | `/api/admin/sys/admin/{id}` | 删除管理员 |

**添加/更新管理员参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名(添加时必填) |
| password | string | 是 | 密码(添加时必填) |
| nickname | string | 否 | 昵称 |
| email | string | 否 | 邮箱 |
| status | int | 否 | 状态 0禁用 1正常 |
| roleIds | long[] | 否 | 分配的角色ID列表 |

---

### 14.2 角色CRUD

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/sys/role/list` | 角色列表 |
| POST | `/api/admin/sys/role/create` | 添加角色 |
| PUT | `/api/admin/sys/role/update` | 更新角色 |

**添加/更新角色参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 角色名称 |
| description | string | 否 | 角色描述 |
| sort | int | 否 | 排序值 |
| status | int | 否 | 状态 0禁用 1正常 |

### 角色分配菜单权限

**PUT** `/api/admin/sys/role/allocMenu`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | long | 是 | 角色ID |
| menuIds | long[] | 是 | 菜单ID列表 |

---

### 14.3 菜单CRUD

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/sys/menu/list` | 菜单列表(树形) |
| POST | `/api/admin/sys/menu/create` | 添加菜单 |
| PUT | `/api/admin/sys/menu/update` | 更新菜单 |
| DELETE | `/api/admin/sys/menu/{id}` | 删除菜单 |

**添加/更新菜单参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| parentId | long | 是 | 父菜单ID(0为顶级) |
| name | string | 是 | 菜单名称 |
| url | string | 否 | 路由路径 |
| component | string | 否 | 前端组件路径 |
| icon | string | 否 | 图标 |
| sort | int | 否 | 排序值 |
| type | int | 是 | 类型 0目录 1菜单 2按钮 |
| permission | string | 否 | 权限标识 |
| status | int | 否 | 状态 0禁用 1正常 |

---

## 十五、文件上传

### 15.1 上传图片

**POST** `/api/upload/image` 🔒

- Content-Type: `multipart/form-data`
- 参数: `file` (MultipartFile)

**响应data：**
```json
{
  "url": "https://xxx/upload/2026/05/14/xxx.jpg",
  "fileName": "xxx.jpg"
}
```

---

## 十六、前台优惠券（/api/oms/coupon）

### 16.1 获取可领取优惠券列表

**GET** `/api/oms/coupon/available`

> 公开接口，无需登录

**响应data：**
```json
[
  {
    "id": 1,
    "name": "满100减20",
    "type": 1,
    "amount": 20.00,
    "minPoint": 100.00,
    "maxDiscount": null,
    "totalCount": 500,
    "remainCount": 487,
    "perLimit": 3,
    "startTime": "2026-05-01 00:00:00",
    "endTime": "2026-06-30 23:59:59",
    "useType": 0,
    "status": 1
  }
]
```

> **优惠券类型说明：**
> - type=1：满减券（amount=优惠金额，minPoint=门槛）
> - type=2：折扣券（amount=折扣率如0.20表示打8折，maxDiscount=最大优惠上限）
> - type=3：无门槛券（amount=优惠金额，直接减免）
>
> **使用范围：** useType=0全店、1指定分类、2指定商品

---

### 16.2 领取优惠券

**POST** `/api/oms/coupon/receive/{couponId}` 🔒

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "领取成功"
}
```

> 失败场景：优惠券已禁用、已过期、已领完、超出限领数量

---

### 16.3 获取我的优惠券列表

**GET** `/api/oms/coupon/my` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int | 否 | 状态过滤 0未使用 1已使用 2已过期，不传返回全部 |

**响应data：**
```json
[
  {
    "memberCoupon": {
      "id": 100,
      "memberId": 1,
      "couponId": 1,
      "orderId": null,
      "status": 0,
      "getTime": "2026-05-14 10:00:00",
      "useTime": null,
      "expireTime": "2026-06-30 23:59:59"
    },
    "coupon": {
      "id": 1,
      "name": "满100减20",
      "type": 1,
      "amount": 20.00,
      "minPoint": 100.00,
      "maxDiscount": null,
      "useType": 0
    }
  }
]
```

---

### 16.4 获取当前订单可用优惠券

**GET** `/api/oms/coupon/availableForOrder` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderAmount | decimal | 是 | 订单金额（用于过滤可用券） |

**响应data：**
```json
[
  {
    "memberCoupon": { "id": 100, "couponId": 1, "status": 0 },
    "coupon": { "id": 1, "name": "满100减20", "type": 1, "amount": 20.00, "minPoint": 100.00 },
    "discount": 20.00
  }
]
```

> 仅返回符合订单金额门槛的优惠券，按照优惠金额可由前端排序展示。

---

### 16.5 订单创建支持使用优惠券

> ⚠️ 此为对 [5.2 创建订单](#52-创建订单) 的补充说明

**POST** `/api/oms/order/create` 🔒

**请求新增参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| memberCouponId | long | 否 | 用户优惠券ID（来自/api/oms/coupon/availableForOrder） |

> 订单创建时若传入 `memberCouponId`，系统将自动校验、计算优惠并扣减；订单取消时自动返还优惠券。

---

### 16.6 订单确认页返回可用券

> ⚠️ 此为对 [5.1 生成订单确认页信息](#51-生成订单确认页信息) 的补充说明

**响应data新增字段：**
```json
{
  "addressList": [...],
  "cartItemList": [...],
  "totalAmount": 198.00,
  "freightAmount": 0.00,
  "payAmount": 198.00,
  "availableCoupons": [
    {
      "memberCoupon": { "id": 100, "couponId": 1 },
      "coupon": { "name": "满100减20", "amount": 20.00 },
      "discount": 20.00
    }
  ]
}
```

---

## 十七、后台优惠券管理（/api/admin/coupon）

### 17.1 获取优惠券列表

**GET** `/api/admin/coupon/list` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 优惠券名称搜索 |
| type | int | 否 | 类型 1满减 2折扣 3无门槛 |
| status | int | 否 | 状态 0禁用 1启用 |
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认20 |

**响应data：** 标准分页格式（同 SmsCoupon 字段结构）

---

### 17.2 获取优惠券详情

**GET** `/api/admin/coupon/{id}` 🔒

---

### 17.3 创建优惠券

**POST** `/api/admin/coupon/create` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 优惠券名称 |
| type | int | 是 | 类型 1满减 2折扣 3无门槛 |
| amount | decimal | 是 | 优惠金额或折扣率（折扣券填如0.20表示打8折） |
| minPoint | decimal | 否 | 使用门槛金额，无门槛券填0 |
| maxDiscount | decimal | 否 | 最大优惠金额（折扣券适用） |
| totalCount | int | 是 | 发行总量 |
| perLimit | int | 是 | 每人限领数量 |
| startTime | string | 是 | 开始时间(yyyy-MM-dd HH:mm:ss) |
| endTime | string | 是 | 结束时间(yyyy-MM-dd HH:mm:ss) |
| useType | int | 否 | 使用范围 0全店 1指定分类 2指定商品，默认0 |
| status | int | 否 | 状态 0禁用 1启用，默认1 |

> 创建时 `remainCount` 自动等于 `totalCount`。

---

### 17.4 更新优惠券

**PUT** `/api/admin/coupon/update?id={id}` 🔒

> 参数同创建优惠券。已有用户领取的优惠券不允许修改。

---

### 17.5 删除优惠券

**DELETE** `/api/admin/coupon/{id}` 🔒

> 已有用户领取的优惠券不允许删除。

---

### 17.6 更新优惠券状态

**PUT** `/api/admin/coupon/updateStatus?id={id}&status={status}` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 优惠券ID |
| status | int | 是 | 0禁用 1启用 |

---

## 十八、前台商品评价（/api/pms/comment）

### 18.1 提交评价

**POST** `/api/pms/comment/submit` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 订单ID |
| orderItemId | long | 是 | 订单商品项ID |
| star | int | 是 | 评分 1-5 |
| content | string | 是 | 评价内容 |
| pics | string | 否 | 评价图片URL，多张用逗号分隔 |

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "评价成功"
}
```

> 限制条件：订单状态必须为已完成(status=3)，且同一订单商品只能评价一次。

---

### 18.2 获取商品评价列表

**GET** `/api/pms/comment/list`

> 公开接口，无需登录

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productId | long | 是 | 商品ID |
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认20 |

**响应data.records项：**
```json
{
  "id": 1,
  "productId": 100,
  "memberId": 1,
  "memberNickName": "用户昵称",
  "memberAvatar": "https://xxx/avatar.jpg",
  "orderId": 200,
  "orderItemId": 300,
  "star": 5,
  "content": "商品质量很好，物流也很快！",
  "pics": "https://xxx/pic1.jpg,https://xxx/pic2.jpg",
  "showStatus": 1,
  "replyCount": 1,
  "createTime": "2026-05-20 14:30:00",
  "replyList": [
    {
      "id": 1,
      "commentId": 1,
      "content": "感谢您的评价，我们会继续努力！",
      "adminName": "客服小王",
      "createTime": "2026-05-20 15:00:00"
    }
  ]
}
```

> 仅返回 showStatus=1（显示中）的评价，按时间倒序排列。

---

### 18.3 获取商品评价统计

**GET** `/api/pms/comment/statistics`

> 公开接口，无需登录

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productId | long | 是 | 商品ID |

**响应data：**
```json
{
  "totalCount": 128,
  "avgStar": 4.5,
  "star1Count": 5,
  "star2Count": 8,
  "star3Count": 15,
  "star4Count": 40,
  "star5Count": 60
}
```

> 仅统计 showStatus=1（显示中）的评价。

---

## 十九、后台评价管理（/api/admin/comment）

### 19.1 获取评价列表

**GET** `/api/admin/comment/list` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productId | long | 否 | 商品ID筛选 |
| showStatus | int | 否 | 显示状态 0隐藏 1显示 |
| keyword | string | 否 | 评价内容关键词搜索 |
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认20 |

**响应data：** 标准分页格式（包含评价完整信息及replyList回复列表）

---

### 19.2 获取评价详情

**GET** `/api/admin/comment/{id}` 🔒

**响应data：** 评价完整信息（含replyList回复列表）

---

### 19.3 更新评价显示状态

**PUT** `/api/admin/comment/updateShowStatus` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 评价ID |
| showStatus | int | 是 | 0隐藏 1显示 |

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "更新成功"
}
```

---

### 19.4 回复评价

**POST** `/api/admin/comment/reply` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| commentId | long | 是 | 评价ID |
| content | string | 是 | 回复内容 |

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "回复成功"
}
```

> 回复成功后，评价的 replyCount 自动+1。

---

## 二十、消息通知（/api/ums/message）

### 20.1 获取消息列表

**GET** `/api/ums/message/list` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | int | 否 | 类型过滤 1订单 2系统 3活动，不传返回全部 |
| isRead | int | 否 | 已读状态 0未读 1已读，不传返回全部 |
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认20 |

**响应data.records项：**
```json
{
  "id": 1,
  "memberId": 1,
  "title": "订单支付成功",
  "content": "您的订单 #202605200001 已支付成功，我们将尽快为您发货。",
  "type": 1,
  "isRead": 0,
  "createTime": "2026-05-20 14:30:00"
}
```

> **消息类型说明：** type=1订单通知、type=2系统通知、type=3活动通知

---

### 20.2 获取未读消息数量

**GET** `/api/ums/message/unreadCount` 🔒

**响应data：**
```json
5
```

> 返回当前登录用户的未读消息总数，用于展示消息角标。

---

### 20.3 标记消息已读

**PUT** `/api/ums/message/markRead` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| messageId | long | 是 | 消息ID |

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "标记已读成功"
}
```

---

### 20.4 一键已读

**PUT** `/api/ums/message/markAllRead` 🔒

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "全部标记已读成功"
}
```

> 将当前用户的所有未读消息标记为已读。

---

### 20.5 自动消息触发场景

以下场景会自动向用户发送站内信通知：

| 场景 | 消息标题 | 触发时机 |
|------|---------|---------|
| 订单支付成功 | 订单支付成功 | 支付宝回调确认支付成功 |
| 订单已发货 | 订单已发货 | 后台管理员点击发货 |
| 订单已取消 | 订单已取消 | 用户主动取消订单 |

---

## 二十一、接口限流说明

系统基于Redis滑动窗口算法对接口进行限流保护，防止恶意攻击和滥用。

### 限流规则

| 接口 | 限流维度 | 限制次数 | 时间窗口 | 触发提示 |
|------|---------|---------|---------|---------|
| POST `/api/ums/login` | IP | 5次 | 60秒 | 登录过于频繁，请1分钟后再试 |
| POST `/api/ums/register` | IP | 3次 | 60秒 | 注册过于频繁，请1分钟后再试 |
| POST `/api/admin/login` | IP | 5次 | 60秒 | 登录过于频繁，请1分钟后再试 |

### 限流响应

当请求触发限流规则时，接口返回：

```json
{
  "code": 500,
  "message": "登录过于频繁，请1分钟后再试",
  "data": null
}
```

### 扩展使用

开发者可通过 `@RateLimit` 注解为任意接口添加限流保护：

```java
@RateLimit(key = "api_name", limit = 100, window = 60)
public CommonResult someApi() { ... }
```

**注解参数说明：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| key | string | 必填 | 限流标识，区分不同接口 |
| limit | int | 100 | 时间窗口内最大请求次数 |
| window | int | 60 | 时间窗口大小，单位：秒 |
| limitType | enum | IP | 限流维度：IP/USER/GLOBAL |
| message | string | 请求过于频繁 | 触发限流时的提示消息 |

---

## 二十二、数据报表（/api/admin/report）

### 22.1 订单导出Excel

**GET** `/api/admin/report/exportOrder` 🔒

> 直接返回Excel文件下载流

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderSn | string | 否 | 订单编号模糊搜索 |
| status | int | 否 | 订单状态筛选 |
| receiverPhone | string | 否 | 收货电话模糊搜索 |
| startTime | string | 否 | 创建时间起(yyyy-MM-dd HH:mm:ss) |
| endTime | string | 否 | 创建时间止(yyyy-MM-dd HH:mm:ss) |

**导出字段：** 订单编号、订单状态、订单金额、实付金额、收货人、收货电话、收货地址、创建时间、支付时间

---

### 22.2 销售报表统计

**GET** `/api/admin/report/sales` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startTime | string | 否 | 统计时间起(yyyy-MM-dd HH:mm:ss) |
| endTime | string | 否 | 统计时间止(yyyy-MM-dd HH:mm:ss) |

**响应data：**
```json
{
  "totalOrderCount": 156,
  "paidOrderCount": 120,
  "deliveredOrderCount": 30,
  "completedOrderCount": 6,
  "totalAmount": 25800.00,
  "totalPayAmount": 23650.50
}
```

> 仅统计状态为已支付/已发货/已完成的订单。

---

### 22.3 商品销售排行

**GET** `/api/admin/report/productRank` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startTime | string | 否 | 统计时间起(yyyy-MM-dd HH:mm:ss) |
| endTime | string | 否 | 统计时间止(yyyy-MM-dd HH:mm:ss) |
| topN | int | 否 | 排行数量，默认10 |

**响应data：**
```json
{
  "records": [
    {
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "salesCount": 58,
      "salesAmount": 57942.00
    }
  ],
  "topN": 10
}
```

> 按销售金额倒序排列，仅统计已支付/已发货/已完成的订单。

---

## 二十三、物流查询（/api/oms/logistics）

### 23.1 查询订单物流

**GET** `/api/oms/logistics/order/{orderId}` 🔒

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | long | 是 | 订单ID（路径参数） |

**响应data：**
```json
{
  "orderId": 1,
  "orderSn": "202605200001",
  "deliveryCompany": "顺丰速运",
  "deliverySn": "SF1234567890",
  "status": 1,
  "detailList": [
    {
      "time": "2026-05-20 10:00:00",
      "desc": "已签收，签收人：本人"
    },
    {
      "time": "2026-05-20 08:30:00",
      "desc": "快递员正在派送中"
    },
    {
      "time": "2026-05-20 06:00:00",
      "desc": "到达【北京市朝阳区营业点】"
    },
    {
      "time": "2026-05-19 22:00:00",
      "desc": "离开【上海转运中心】，发往北京"
    },
    {
      "time": "2026-05-19 18:00:00",
      "desc": "【上海转运中心】已揽收"
    }
  ]
}
```

> **物流状态说明：** status=0运输中、1已签收、2异常
> 
> 物流详情按时间倒序排列，最新状态在前。

---

### 23.2 发货时录入物流信息

> ⚠️ 此为对 [12.3 订单发货](#123-订单发货) 的补充说明

后台管理员发货时，系统自动在 `oms_order_logistics` 表中创建物流记录：

| 字段 | 说明 |
|------|------|
| deliveryCompany | 物流公司名称 |
| deliverySn | 物流单号 |
| status | 初始状态为0（运输中） |
| detail | 初始包含一条"已揽收"记录 |

---

> 🔒 表示需要Token认证的接口
