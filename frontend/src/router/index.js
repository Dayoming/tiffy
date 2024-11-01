import { createRouter, createWebHistory } from 'vue-router';
import HomePageIndex from '../components/HomePageIndex';

const routes = [
    {
        path: '/',
        name: 'index',
        component: HomePageIndex
     },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
