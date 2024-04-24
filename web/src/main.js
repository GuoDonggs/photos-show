import './assets/main.css'
// el-message 失效解决
import 'element-plus/theme-chalk/el-message.css'

import {createApp} from 'vue'
import {createPinia} from 'pinia'

import App from './App.vue'
import router from './router'

const app = createApp(App)
 

app.use(createPinia())
app.use(router)

app.mount('#app')
