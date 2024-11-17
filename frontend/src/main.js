import { createApp } from 'vue'
import { reactive } from 'vue'
import App from './App.vue'
import router from './router/index.js' // 라우터 import
import axios from './axios'
import Vue3Geolocation from 'vue3-geolocation'

// 모든 요청에서 Authorization 헤더에 추가하도록 axios 설정
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('authToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

const app = createApp(App)

// 로그인 상태를 전역으로 관리
app.config.globalProperties.$globalState = reactive({
  isLoggedIn: !!localStorage.getItem("authToken"),
});

app.config.globalProperties.$axios = axios;
app.use(router) // 라우터 등록
app.use(Vue3Geolocation)
app.mount('#app')
