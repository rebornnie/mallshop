import { chromium } from '@playwright/test'
import { mkdir } from 'fs/promises'

const BASE = 'http://localhost:3002'
const OUT = '/Users/agilewing/projects/ai-test-mall/screenshots/admin'

await mkdir(OUT, { recursive: true })

const browser = await chromium.launch({ headless: true })
const ctx = await browser.newContext({ viewport: { width: 1440, height: 900 } })
const page = await ctx.newPage()

// Login
await page.goto(`${BASE}/login`)
await page.waitForSelector('input[placeholder="请输入用户名"]', { timeout: 10000 })
await page.fill('input[placeholder="请输入用户名"]', 'admin')
await page.fill('input[placeholder="请输入密码"]', '123456')
await page.click('button:has-text("登 录")')
await page.waitForURL('**/dashboard', { timeout: 15000 })
await page.waitForTimeout(1500)
await page.screenshot({ path: `${OUT}/01-dashboard.png`, fullPage: true })
console.log('Dashboard screenshot saved')

const pages = [
  { path: '/product', name: '商品列表', file: '02-product' },
  { path: '/category', name: '分类管理', file: '03-category' },
  { path: '/brand', name: '品牌管理', file: '04-brand' },
  { path: '/order', name: '订单管理', file: '05-order' },
  { path: '/member', name: '会员管理', file: '06-member' },
  { path: '/stat', name: '数据统计', file: '07-stat' },
]

for (const p of pages) {
  try {
    const menuItems = page.locator('.el-menu-item')
    const count = await menuItems.count()
    for (let i = 0; i < count; i++) {
      const text = await menuItems.nth(i).textContent()
      if (text?.includes(p.name)) {
        await menuItems.nth(i).click()
        break
      }
    }
    await page.waitForURL(`**${p.path}`, { timeout: 10000 })
    await page.waitForTimeout(1500)
    await page.screenshot({ path: `${OUT}/${p.file}.png`, fullPage: true })
    console.log(`${p.file} screenshot saved`)
  } catch (e) {
    console.log(`Error on ${p.file}: ${e.message}`)
    await page.screenshot({ path: `${OUT}/${p.file}-error.png`, fullPage: true })
  }
}

await browser.close()
console.log('All screenshots done')
