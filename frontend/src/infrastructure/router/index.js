import { createRouter, createWebHistory } from 'vue-router'
import TransferScheduler from '../../presentation/components/TransferScheduler-simple.vue'
import TransferList from '../../presentation/components/TransferList-simple.vue'

const routes = [
  {
    path: '/',
    redirect: '/schedule'
  },
  {
    path: '/schedule',
    name: 'Schedule',
    component: TransferScheduler,
    meta: {
      title: 'Schedule Transfer'
    }
  },
  {
    path: '/transfers',
    name: 'Transfers',
    component: TransferList,
    meta: {
      title: 'Transfer History'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'Bank Transfer Scheduler'} - Sistema de Transferências`
  next()
})

export default router
