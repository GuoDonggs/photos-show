import {onBeforeMount, reactive} from 'vue'
import {defineStore} from 'pinia'
import {userApi} from "@/config/api.js";
import axiosPlugin from "@/config/axiosPlugin.js";


export const userStore = defineStore('user', () => {
    onBeforeMount(() => {
        checkLogin()
    })

    const user = reactive({
        login: false,
        info: {},
        token: ''
    })

    function getUserName() {
        return user.info.username
    }

    function checkLogin() {
        let token = localStorage.getItem('token')
        if (token === undefined) {
            user.login = false
        } else {
            axiosPlugin.getAxios(token)
                .get(userApi.check)
                .then(
                    (res) => {
                        user.login = true
                        user.info = res.data.data
                        user.token = token
                    },
                    (res) => {
                        user.login = false
                        // localStorage.clear()
                    }
                )
        }
    }

    function isLogin() {
        return user.login
    }

    function setToken(token) {
        user.token = token
        localStorage.setItem('token', token)
        user.login = true
        checkLogin()
    }

    function setInfo(info) {
        user.info = info
    }

    return {isLogin, checkLogin, setToken, setInfo, getUserName, user}
})
