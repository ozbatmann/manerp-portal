import Vue from 'vue'
import App from './App.vue'

Vue.config.productionTip = false
import {bus, http, i18n, store} from "manerp-vue-base"
import router from '@/router'
import './pagination-models'

//http.defaults.baseURL = "http://157.230.125.223:80";
http.defaults.baseURL = "http://localhost:8091";

if(store.state.shared['auth-token']){
  http.defaults.headers.common['Authorization'] = store.state.shared['auth-token']
}
window.instance = new Vue({
  router,
  store,
  i18n,
  render: h => h(App),
}).$mount('#app')
