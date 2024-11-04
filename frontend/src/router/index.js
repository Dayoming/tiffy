import { createRouter, createWebHistory } from 'vue-router';
import HomePageIndex from '../components/HomePageIndex';
import ExchangeList from '../components/exchange/ExchangeList';
import ExchangeNew from '../components/exchange/ExchangeNew';
import ExchangeDetail from '../components/exchange/ExchangeDetail';
import ExchangeEdit from '../components/exchange/ExchangeEdit';
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
        path: '/exchange/:id',
        name: 'exchangeDetail',
        component: ExchangeDetail
     },
     {
        path: '/exchange/edit/:id',
        name: 'exchangeEdit',
        component: ExchangeEdit
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
