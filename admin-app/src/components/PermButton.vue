<script lang="ts" setup>
import { useAuth } from '@/hooks/useAuth'

const props = withDefaults(defineProps<{
  /** 权限标识，与后台 perms 一致 */
  perm?: string
  /** 无 perm 时是否始终显示 */
  alwaysShow?: boolean
}>(), {
  alwaysShow: false,
})

const { hasPermi } = useAuth()

const visible = computed(() => {
  if (props.alwaysShow || !props.perm)
    return true
  return hasPermi(props.perm)
})
</script>

<template>
  <wd-button v-if="visible" v-bind="$attrs">
    <slot />
  </wd-button>
</template>
