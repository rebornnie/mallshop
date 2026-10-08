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
  { path: '/product', selector: 'text=商品列表', name: '02-product' },
  { path: '/category', selector: 'text=分类管理', name: '03-category' },
  { path: '/brand', selector: 'text=品牌管理', name: '04-brand' },
  { path: '/order', selector: 'text=订单管理', name: '05-order' },
  { path: '/member', selector: 'text=会员管理', name: '06-member' },
  { path: '/stat', selector: 'text=数据统计', name: '07-stat' },
]

for (const p of pages) {
  try {
    // Click menu item
    const menuItem = page.locator('.el-menu-item, .el-sub-menu__title').filter({ hasText: p.selector.replace('text=', '') })
    // Try to find and click the exact menu item
    const link = page.locator(`a[href="${p.path}"], .el-menu-item:has-text("${p.selector.replace('text=', '')}")`)
    await link.first().click({ timeout: 5000 })
    await page.waitForURL(`**${p.path}`, { timeout: 10000 })
    await page.waitForTimeout(1500)
    await page.screenshot({ path: `${OUT}/${p.name}.png`, fullPage: true })
    console.log(`${p.name} screenshot saved`)
  } catch (e) {
    console.log(`Error on ${p.name}: ${e.message}`)
    await page.screenshot({ path: `${OUT}/${p.name}-error.png`, fullPage: true })
  }
}

await browser.close()
console.log('All screenshots done')
