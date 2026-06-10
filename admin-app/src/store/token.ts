import type { ILoginForm, ILoginVo } from '@/api/types/backend'
import { getUserInfo, login as loginApi, logout as logoutApi } from '@/api/auth'
import { TOKEN_KEY } from '@/constants/app'
import { clearTokenExpireTime, saveTokenExpireTime } from '@/http/http'
import { useDictStore } from '@/store/dict'
import { usePermissionStore } from '@/store/permission'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { useUserStore } from './user'

const emptyToken: ILoginVo = {
  access_token: '',
  expire_in: 0,
}

export const useTokenStore = defineStore(
  'token',
  () => {
    const tokenInfo = ref<ILoginVo>({ ...emptyToken })
    const nowTime = ref(Date.now())

    const updateNowTime = () => {
      nowTime.value = Date.now()
      return useTokenStore()
    }

    const setTokenInfo = (val: ILoginVo) => {
      updateNowTime()
      tokenInfo.value = val
      uni.setStorageSync(TOKEN_KEY, val.access_token)
      if (val.expire_in)
        saveTokenExpireTime(val.expire_in)
    }

    const isTokenExpired = computed(() => {
      if (!tokenInfo.value.access_token)
        return true
      const expireTime = uni.getStorageSync('accessTokenExpireTime')
      if (!expireTime)
        return true
      return nowTime.value >= expireTime
    })

    async function postLogin(loginVo: ILoginVo) {
      setTokenInfo(loginVo)
      await useUserStore().fetchUserInfo()
      await usePermissionStore().fetchRoutes(true)
      const dictStore = useDictStore()
      await dictStore.loadDict('sys_user_sex')
      await dictStore.loadDict('sys_normal_disable')
    }

    const login = async (loginForm: ILoginForm) => {
      try {
        const res = await loginApi(loginForm)
        await postLogin(res)
        uni.showToast({ title: '登录成功', icon: 'success' })
        return res
      }
      catch (error) {
        // 业务/网络错误由 http 层 toast 后端 msg，勿再弹出「登录失败，请重试」覆盖真实原因
        throw error
      }
      finally {
        updateNowTime()
      }
    }

    const logout = async () => {
      try {
        if (tokenInfo.value.access_token)
          await logoutApi()
      }
      catch (error) {
        console.error('退出登录失败:', error)
      }
      finally {
        updateNowTime()
        clearTokenExpireTime()
        uni.removeStorageSync(TOKEN_KEY)
        tokenInfo.value = { ...emptyToken }
        useUserStore().clearUserInfo()
        usePermissionStore().clearRoutes()
        useDictStore().clearDict()
      }
    }

    const validToken = computed(() => {
      if (isTokenExpired.value)
        return ''
      return tokenInfo.value.access_token
    })

    const hasValidLogin = computed(() => !!tokenInfo.value.access_token && !isTokenExpired.value)

    return {
      login,
      logout,
      hasLogin: hasValidLogin,
      validToken,
      tokenInfo,
      setTokenInfo,
      updateNowTime,
    }
  },
  {
    persist: { pick: ['tokenInfo'] },
  },
)
