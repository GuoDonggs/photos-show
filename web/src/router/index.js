import {createRouter, createWebHistory} from 'vue-router'
import axiosPlugin from "@/config/axiosPlugin.js";
import {userApi} from "@/config/api.js";
import {ElMessage} from "element-plus";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import("@/views/HomeView.vue"),
            meta: {keepAlive: true}
        }, {
            path: '/login',
            name: 'login',
            component: () => import("@/views/LoginView.vue"),
            props: (router) => ({f: router.query.f})
        }, {
            path: '/search',
            name: 'search',
            component: () => import("@/views/SearchView.vue"),
            props: (router) => ({t: router.query.t})
        }, {
            path: '/user',
            name: 'user',
            component: () => import("@/views/UserView.vue"),
            mate: {needLogin: true}
        }, {
            path: '/upload',
            name: 'upload',
            component: () => import("@/views/UploadFile.vue"),
            mate: {needLogin: true}
        }
    ]
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.meta.needLogin) {
        axiosPlugin.getAxios(token).get(userApi.check)
            .then((res) => {
                next()
            }, res => {
                ElMessage.error("请先登录")
                next({path: '/login?f=' + to.path})
            })
    } else {
        next()
    }
})

export default router
