import { createApp } from 'vue'
import { reactive } from 'vue'
import App from './App.vue'
import router from './router/index.js' // 라우터 import
import axios from 'axios'

const app = createApp(App)
app.config.globalProperties.$axios = axios;

// 로그인 상태를 전역으로 관리
app.config.globalProperties.$globalState = reactive({
  isLoggedIn: !!localStorage.getItem("authToken"),
});

app.use(router) // 라우터 등록
app.mount('#app')
