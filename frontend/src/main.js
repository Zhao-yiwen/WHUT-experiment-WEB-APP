import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'

Vue.config.productionTip = false
Vue.use(ElementUI)

// 配置axios
axios.defaults.baseURL = 'http://localhost:8080/api'
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

axios.interceptors.response.use(response => {
  // 处理响应中的用户数据，确保头像URL是完整的
  if (response.data && response.data.user && response.data.user.avatar) {
    // 如果头像URL不是以http开头，则添加完整路径
    if (!response.data.user.avatar.startsWith('http')) {
      response.data.user.avatar = `http://localhost:8080${response.data.user.avatar}`;
    }
  }
  
  // 对于普通的用户数据响应
  if (response.data && response.data.avatar) {
    // 如果头像URL不是以http开头，则添加完整路径
    if (!response.data.avatar.startsWith('http')) {
      response.data.avatar = `http://localhost:8080${response.data.avatar}`;
    }
  }
  
  return response
}, error => {
  if (error.response && error.response.status === 401) {
    // 未授权，重定向到登录页
    router.push('/login')
  }
  return Promise.reject(error)
})

Vue.prototype.$http = axios

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app') 