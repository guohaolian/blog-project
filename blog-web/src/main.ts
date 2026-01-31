import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@element-plus/theme-chalk/dist/dark/css-vars.css'
import './style.css'
import { createPinia } from 'pinia'

import App from './App.vue'
import { router } from './router'
import { useThemeStore } from './stores/theme'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus)

// init theme as early as possible
useThemeStore(pinia).init()

app.mount('#app')
