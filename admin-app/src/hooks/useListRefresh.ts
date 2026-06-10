import type { Ref } from 'vue'
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

type PagingRef = Ref<{
  reload?: (showLoading?: boolean) => void
} | null>

/**
 * 从 form 页 navigateBack 后刷新 z-paging 列表
 * @example
 * const pagingRef = ref(null)
 * useListRefresh(pagingRef)
 */
export function useListRefresh(pagingRef: PagingRef, options?: { showLoading?: boolean }) {
  const isFirstShow = ref(true)
  const showLoading = options?.showLoading ?? false

  onShow(() => {
    if (isFirstShow.value) {
      isFirstShow.value = false
      return
    }
    pagingRef.value?.reload?.(showLoading)
  })
}
