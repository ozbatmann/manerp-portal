import Vue from 'vue'
import App from './App.vue'

Vue.config.productionTip = false
import {bus, http, i18n, store} from "manerp-vue-base"
import router from '@/router'
import './pagination-models'

this.http.defaults.baseURL = "http://localhost:80"

window.instance = new Vue({
  router,
  store,
  i18n,
  render: h => h(App),
}).$mount('#app')
