import type { Plugin } from 'vite'
import fs from 'node:fs'
import path from 'node:path'
import process from 'node:process'

const LAZY_CODE_LOADING = 'requiredComponents'

function patchAppJson(appJsonPath: string) {
  if (!fs.existsSync(appJsonPath))
    return false

  const appJson = JSON.parse(fs.readFileSync(appJsonPath, 'utf8')) as Record<string, unknown>
  if (appJson.lazyCodeLoading === LAZY_CODE_LOADING)
    return false

  appJson.lazyCodeLoading = LAZY_CODE_LOADING
  fs.writeFileSync(appJsonPath, `${JSON.stringify(appJson, null, 2)}\n`)
  return true
}

/**
 * uni-app 开发模式 (pnpm dev:mp) 编译产物 dist/dev/mp-weixin/app.json
 * 不会写入 manifest 中的 lazyCodeLoading，导致微信代码质量检测未通过。
 */
export default function patchMpWeixinAppJsonPlugin(mode: string): Plugin {
  return {
    name: 'patch-mp-weixin-app-json',
    apply: 'build',
    enforce: 'post',
    closeBundle() {
      if (process.env.UNI_PLATFORM !== 'mp-weixin')
        return

      const subDir = mode === 'production' ? 'build' : 'dev'
      const appJsonPath = path.resolve(process.cwd(), `dist/${subDir}/mp-weixin/app.json`)

      if (patchAppJson(appJsonPath)) {
        console.log(`✅ 已注入 lazyCodeLoading → ${path.relative(process.cwd(), appJsonPath)}`)
      }
    },
  }
}
