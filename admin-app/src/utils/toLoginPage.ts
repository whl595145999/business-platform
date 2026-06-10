import { LOGIN_PAGE } from '@/router/config'
import { getLastPage } from '@/utils'
import { debounce } from '@/utils/debounce'

interface ToLoginPageOptions {
  mode?: 'navigateTo' | 'reLaunch'
  queryString?: string
}

export const toLoginPage = debounce((options: ToLoginPageOptions = {}) => {
  const { mode = 'navigateTo', queryString = '' } = options
  const url = `${LOGIN_PAGE}${queryString}`

  const currentPage = getLastPage()
  const currentPath = `/${currentPage?.route || ''}`
  if (currentPath === LOGIN_PAGE)
    return

  if (mode === 'navigateTo')
    uni.navigateTo({ url })
  else
    uni.reLaunch({ url })
}, 500)
