/** 业务平台通用格式化与数据工具 */

export function parseTime(time?: string | number | Date, pattern = '{y}-{m}-{d} {h}:{i}:{s}') {
  if (!time)
    return null
  let date: Date
  if (time instanceof Date) {
    date = time
  }
  else if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
    date = new Date(Number.parseInt(time))
  }
  else if (typeof time === 'number') {
    date = new Date(time.toString().length === 10 ? time * 1000 : time)
  }
  else {
    date = new Date(time)
  }
  const formatObj: Record<string, number> = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay(),
  }
  return pattern.replace(/\{([ymdhisa])+\}/g, (_result, key: string) => {
    let value = formatObj[key]
    if (key === 'a')
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    if (_result.length > 0 && value < 10)
      value = Number(`0${value}`)
    return String(value ?? 0)
  })
}

export function handleTree<T extends Record<string, any>>(
  data: T[],
  id = 'id',
  parentId = 'parentId',
  children = 'children',
) {
  const childrenListMap: Record<string, T[]> = {}
  const nodeIds: Record<string, T> = {}
  const tree: T[] = []
  for (const item of data) {
    const pid = item[parentId]
    if (childrenListMap[pid] == null)
      childrenListMap[pid] = []
    nodeIds[item[id]] = item
    childrenListMap[pid].push(item)
  }
  for (const item of data) {
    const pid = item[parentId]
    if (nodeIds[pid] == null)
      tree.push(item)
  }
  for (const node of tree)
    adaptToChildrenList(node)
  function adaptToChildrenList(node: T) {
    if (childrenListMap[node[id]] != null)
      (node as any)[children] = childrenListMap[node[id]]
    if ((node as any)[children]) {
      for (const child of (node as any)[children])
        adaptToChildrenList(child)
    }
  }
  return tree
}

export function selectDictLabel(datas: Array<{ label: string, value: string }>, value?: string | number) {
  if (value === undefined || value === null)
    return ''
  const actions: string[] = []
  datas.forEach((item) => {
    if (item.value === String(value))
      actions.push(item.label)
  })
  return actions.join('')
}

export function tansParams(params: Record<string, any>) {
  let result = ''
  for (const propName of Object.keys(params)) {
    const value = params[propName]
    const part = `${encodeURIComponent(propName)}=`
    if (value !== null && value !== '' && typeof value !== 'undefined') {
      if (typeof value === 'object') {
        for (const key of Object.keys(value))
          result += `${encodeURIComponent(`${propName}[${key}]`)}=${encodeURIComponent(value[key])}&`
      }
      else {
        result += `${part + encodeURIComponent(value)}&`
      }
    }
  }
  return result
}

export function resolveAvatarUrl(avatar?: string, baseUrl?: string) {
  if (!avatar)
    return '/static/images/default-avatar.png'
  if (avatar.startsWith('http') || avatar.startsWith('/static'))
    return avatar
  return `${baseUrl || ''}${avatar}`
}
