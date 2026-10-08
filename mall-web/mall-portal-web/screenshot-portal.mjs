import { chromium } from '@playwright/test'
import { mkdir } from 'fs/promises'

const BASE = 'http://localhost:5173'
const OUT = '/Users/agilewing/projects/ai-test-mall/screenshots/portal'

await mkdir(OUT, { recursive: true })

const browser = await chromium.launch({ headless: true })
const ctx = await browser.newContext({ viewport: { width: 1440, height: 900 } })
const page = await ctx.newPage()

// Home page
await page.goto(`${BASE}/`)
await page.waitForTimeout(2000)
await page.screenshot({ path: `${OUT}/01-home.png`, fullPage: true })
console.log('Home screenshot saved')

// Product list
await page.goto(`${BASE}/product/list`)
await page.waitForTimeout(2000)
await page.screenshot({ path: `${OUT}/02-product-list.png`, fullPage: true })
console.log('Product list screenshot saved')

// Product detail
await page.goto(`${BASE}/product/detail/1`)
await page.waitForTimeout(2000)
await page.screenshot({ path: `${OUT}/03-product-detail.png`, fullPage: true })
console.log('Product detail screenshot saved')

await browser.close()
console.log('All portal screenshots done')
