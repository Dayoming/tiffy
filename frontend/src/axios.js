import axios from 'axios';

const instance = axios.create({
    baseURL: `http://${process.env.VUE_APP_IP}:${process.env.VUE_APP_PORT}`, // 기본 URL 설정
});

export default instance;
