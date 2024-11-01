import { createRouter, createWebHistory } from 'vue-router';
import HomePageIndex from '../components/HomePageIndex';
import ExchangeList from '../components/exchange/ExchangeList';
import ExchangeNew from '../components/exchange/ExchangeNew';
import RecipeSearch from '../components/recipe/RecipeSearch';

const routes = [
    {
        path: '/',
        name: 'index',
        component: HomePageIndex
     },
     {
        path: '/exchange',
        name: 'exchangeList',
        component: ExchangeList
     },
     {
        path: '/exchange/new',
        name: 'exchangeNew',
        component: ExchangeNew
     },
     {
        path: '/recipes',
        name: 'recipeSearch',
        component: RecipeSearch
     }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
