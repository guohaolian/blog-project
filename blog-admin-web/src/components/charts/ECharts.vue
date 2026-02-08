<template>
  <div ref="elRef" :style="{ width: '100%', height }" />
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import type { ECElementEvent } from 'echarts/core'

const props = withDefaults(
  defineProps<{
    option: EChartsOption
    height?: string
    loading?: boolean
    onClick?: (params: ECElementEvent) => void
  }>(),
  {
    height: '280px',
    loading: false,
    onClick: undefined,
  },
)

const elRef = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null
let ro: ResizeObserver | null = null
let onWindowResize: (() => void) | null = null
let onChartClick: ((params: ECElementEvent) => void) | null = null

function render() {
  if (!chart) return
  chart.setOption(props.option, { notMerge: true })
  if (props.loading) chart.showLoading('default')
  else chart.hideLoading()
}

onMounted(() => {
  if (!elRef.value) return
  chart = echarts.init(elRef.value)
  render()

  if (props.onClick) {
    onChartClick = ((params: any) => props.onClick?.(params)) as unknown as (params: any) => void
    chart.on('click', onChartClick as any)
  }

  ro = new ResizeObserver(() => {
    chart?.resize()
  })
  ro.observe(elRef.value)

  onWindowResize = () => chart?.resize()
  window.addEventListener('resize', onWindowResize)
})

onBeforeUnmount(() => {
  if (onChartClick) chart?.off('click', onChartClick as any)
  onChartClick = null

  if (onWindowResize) window.removeEventListener('resize', onWindowResize)
  onWindowResize = null
  ro?.disconnect()
  ro = null
  chart?.dispose()
  chart = null
})

watch(
  () => props.option,
  () => render(),
  { deep: true },
)

watch(
  () => props.loading,
  () => render(),
)
</script>
