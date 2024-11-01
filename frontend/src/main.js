import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index.js' // 라우터 import
import axios from 'axios'

const app = createApp(App)
app.config.globalProperties.$axios = axios;

app.use(router) // 라우터 등록
app.mount('#app')
