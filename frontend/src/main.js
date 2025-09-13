import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

import { HttpClient } from './infrastructure/http/HttpClient.js'
import { ApiTransferRepository } from './infrastructure/repositories/ApiTransferRepository.js'
import { TransferSchedulerService } from './application/usecases/TransferSchedulerService.js'
import { useTransferStore } from './application/stores/TransferStore.js'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)

// setup infrastructure -> repository -> service -> store
console.log('Initializing services...')
const httpClient = new HttpClient('http://localhost:8080/api')
const apiRepo = new ApiTransferRepository(httpClient)
const transferService = new TransferSchedulerService(apiRepo)

console.log('Setting up store...')
const store = useTransferStore()
store.setTransferService(transferService)
console.log('Services initialized successfully!')

app.mount('#app')
