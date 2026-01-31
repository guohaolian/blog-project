<template>
  <transition name="backtotop-fade">
    <el-button
      v-if="visible"
      class="backtotop"
      circle
      type="primary"
      :title="title"
      @click="scrollToTop"
    >
      <el-icon><ArrowUp /></el-icon>
    </el-button>
  </transition>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ArrowUp } from '@element-plus/icons-vue'

type Props = {
  /** CSS selector of scroll container. If not found, fall back to window. */
  target?: string
  /** Show button after scrolling this many px */
  threshold?: number
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  target: '',
  threshold: 240,
  title: 'Back to top',
})

const visible = ref(false)
let el: HTMLElement | null = null
let bound: (() => void) | null = null

function getScrollTop(): number {
  if (el) return el.scrollTop
  return window.scrollY || document.documentElement.scrollTop || 0
}

function onScroll() {
  visible.value = getScrollTop() > props.threshold
}

function attach() {
  detach()
  el = props.target ? (document.querySelector(props.target) as HTMLElement | null) : null

  bound = () => onScroll()
  if (el) {
    el.addEventListener('scroll', bound, { passive: true })
  } else {
    window.addEventListener('scroll', bound, { passive: true })
  }
  onScroll()
}

function detach() {
  if (bound) {
    if (el) el.removeEventListener('scroll', bound)
    else window.removeEventListener('scroll', bound)
  }
  bound = null
  el = null
}

function scrollToTop() {
  if (el) {
    el.scrollTo({ top: 0, behavior: 'smooth' })
  } else {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

onMounted(() => attach())

watch(
  () => props.target,
  () => {
    // DOM may change after route switch, delay a tick
    setTimeout(() => attach(), 0)
  },
)

onBeforeUnmount(() => detach())
</script>

<style scoped>
.backtotop {
  position: fixed;
  right: 18px;
  bottom: 18px;
  z-index: 3000;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.18);
}

.backtotop-fade-enter-active,
.backtotop-fade-leave-active {
  transition: opacity 0.16s ease;
}

.backtotop-fade-enter-from,
.backtotop-fade-leave-to {
  opacity: 0;
}
</style>
