import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch, type Ref } from 'vue'
import Sortable from 'sortablejs'

export type DraggableTableColumn = {
  /** Stable unique id for a column, used for ordering & persistence */
  key: string
  /** el-table-column label */
  label?: string
  /** el-table-column prop */
  prop?: string
  width?: string | number
  minWidth?: string | number
  fixed?: boolean | 'left' | 'right'
  align?: 'left' | 'center' | 'right'
  /** If provided, render this column via a named slot */
  slot?: string
  /** Pass-through to el-table-column */
  sortable?: boolean | 'custom'
  /** Pass-through */
  showOverflowTooltip?: boolean
}

type UseDraggableTableColumnsOptions = {
  /** LocalStorage key used to persist order. If omitted, order isn't persisted. */
  storageKey?: string
  /** Disable dragging (still keeps order mapping). */
  enabled?: boolean
  /** Columns that are not allowed to be moved (by key). */
  lockKeys?: string[]
}

function safeReadOrder(storageKey: string): string[] | null {
  try {
    const raw = localStorage.getItem(storageKey)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    if (Array.isArray(parsed) && parsed.every((x) => typeof x === 'string')) return parsed
    return null
  } catch {
    return null
  }
}

function safeWriteOrder(storageKey: string, order: string[]) {
  try {
    localStorage.setItem(storageKey, JSON.stringify(order))
  } catch {
    // ignore
  }
}

function reorderByKey<T extends { key: string }>(columns: T[], order: string[]): T[] {
  const map = new Map(columns.map((c) => [c.key, c]))
  const result: T[] = []
  for (const k of order) {
    const col = map.get(k)
    if (col) {
      result.push(col)
      map.delete(k)
    }
  }
  // append any new columns
  for (const col of columns) {
    if (map.has(col.key)) result.push(col)
  }
  return result
}

/**
 * Enable drag-and-drop reordering for Element Plus el-table columns.
 *
 * Contract:
 * - input: a `columns` list with stable `key`
 * - output: `orderedColumns` (reactive) + helpers
 * - effect: columns can be reordered by dragging table header
 */
export function useDraggableTableColumns(
  tableRef: Ref<any>,
  columns: Ref<DraggableTableColumn[]>,
  options: UseDraggableTableColumnsOptions = {},
) {
  const enabled = options.enabled ?? true
  const lockKeys = options.lockKeys ?? []

  const order = ref<string[]>([])

  const orderedColumns = computed(() => {
    if (!order.value.length) return columns.value
    return reorderByKey(columns.value, order.value)
  })

  const persist = () => {
    if (!options.storageKey) return
    safeWriteOrder(options.storageKey, order.value)
  }

  const setOrderByColumns = (cols: DraggableTableColumn[]) => {
    order.value = cols.map((c) => c.key)
    persist()
  }

  // Keep a default order snapshot (initial columns order).
  const defaultOrder = ref<string[]>([])
  watch(
    () => columns.value.map((c) => c.key).join(','),
    () => {
      if (!defaultOrder.value.length) {
        defaultOrder.value = columns.value.map((c) => c.key)
      }
    },
    { immediate: true },
  )

  const resetOrder = async () => {
    order.value = defaultOrder.value.length ? [...defaultOrder.value] : []
    if (options.storageKey) {
      try {
        localStorage.removeItem(options.storageKey)
      } catch {
        // ignore
      }
    }
    await initSortable()
  }

  let sortable: Sortable | null = null

  const syncOrderFromStorage = () => {
    if (!options.storageKey) return
    const fromStorage = safeReadOrder(options.storageKey)
    if (!fromStorage || !fromStorage.length) return
    // Only accept when it contains at least one known key.
    const keys = new Set(columns.value.map((c) => c.key))
    if (!fromStorage.some((k) => keys.has(k))) return
    order.value = fromStorage
  }

  const initSortable = async () => {
    if (!enabled) return
    await nextTick()

    const table = tableRef.value
    const rootEl: HTMLElement | null = table?.$el ?? table
    if (!rootEl) return

    const headerRow = rootEl.querySelector<HTMLElement>('.el-table__header-wrapper thead tr')
    if (!headerRow) return

    sortable?.destroy()

    // Tell Sortable how many columns we manage. This is the source of truth for indices.
    const getManagedKeys = () => orderedColumns.value.map((c) => c.key)

    sortable = Sortable.create(headerRow, {
      animation: 120,
      ghostClass: 'draggable-col-ghost',
      chosenClass: 'draggable-col-chosen',
      dragClass: 'draggable-col-drag',
      filter: lockKeys.map((k) => `th[data-col-key="${CSS.escape(k)}"]`).join(','),
      onMove: (evt: any) => {
        const related = evt.related as HTMLElement | null
        const dragged = evt.dragged as HTMLElement | null
        const isLocked = (el: HTMLElement | null) => {
          const key = el?.getAttribute('data-col-key')
          return !!key && lockKeys.includes(key)
        }
        return !(isLocked(dragged) || isLocked(related))
      },
      onEnd: (evt: any) => {
        const oldIndex = evt.oldIndex as number | undefined
        const newIndex = evt.newIndex as number | undefined
        if (typeof oldIndex !== 'number' || typeof newIndex !== 'number') return
        if (oldIndex === newIndex) return

        const keys = getManagedKeys()
        const moved = keys.splice(oldIndex, 1)[0]
        if (!moved) return
        keys.splice(newIndex, 0, moved)
        order.value = keys
        persist()
      },
    })
  }

  onMounted(() => {
    syncOrderFromStorage()
    initSortable()
  })

  // Re-init when columns changed (e.g., conditional) or when initial data loaded.
  watch(
    () => columns.value.map((c) => c.key).join(','),
    async () => {
      if (order.value.length) {
        // keep user order but ensure new columns appended
        order.value = reorderByKey(columns.value, order.value).map((c) => c.key)
        persist()
      }
      await initSortable()
    },
  )

  // Re-init when table rerenders (e.g., show/hide) - user can call this.
  const refresh = () => initSortable()

  onBeforeUnmount(() => {
    sortable?.destroy()
    sortable = null
  })

  return {
    orderedColumns,
    order,
    setOrderByColumns,
    resetOrder,
    refresh,
  }
}
