import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

import { HttpClient } from './infrastructure/http/HttpClient.js'
import { ApiTransferRepository } from './infrastructure/repositories/ApiTransferRepository.js'
import { TransferSchedulerService } from './application/usecases/TransferSchedulerService.js'
import { useTransferStore } from './application/stores/TransferStore.js'

/**
 * Application Bootstrap - Seguindo Padrão Pessoal de Programação
 * 
 * Principios aplicados:
 * - Dependency Injection: serviços injetados como dependências
 * - Arquitetura Limpa: separação clara entre camadas
 * - Single Responsibility: cada serviço tem uma responsabilidade
 */

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)

// === DEPENDENCY INJECTION SETUP (Arquitetura Limpa) ===
console.log('Initializing services...')
const httpClient = new HttpClient('http://localhost:8080/api')
const apiRepo = new ApiTransferRepository(httpClient)
const transferService = new TransferSchedulerService(apiRepo)

// === STORE CONFIGURATION ===
console.log('Setting up store...')
const store = useTransferStore()
store.setTransferService(transferService)

// === PROVIDE SERVICES FOR COMPONENTS (Dependency Injection) ===
app.provide('transferService', transferService)
app.provide('httpClient', httpClient)

console.log('Services initialized successfully!')

app.mount('#app')
