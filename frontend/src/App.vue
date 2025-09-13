<template>
  <div class="app">
    <header class="header">
      <div class="logo">
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <!-- Background with modern gradient -->
          <defs>
            <linearGradient id="logoGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#0b5fff;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#1890ff;stop-opacity:1" />
            </linearGradient>
          </defs>
          <rect width="24" height="24" rx="6" fill="url(#logoGradient)" />
          
          <!-- Modern bank/transfer icon -->
          <!-- Building/Bank structure -->
          <rect x="4" y="8" width="16" height="1" fill="white" opacity="0.9"/>
          <rect x="6" y="9" width="2" height="6" fill="white" opacity="0.8"/>
          <rect x="9" y="9" width="2" height="6" fill="white" opacity="0.8"/>
          <rect x="12" y="9" width="2" height="6" fill="white" opacity="0.8"/>
          <rect x="15" y="9" width="2" height="6" fill="white" opacity="0.8"/>
          <rect x="4" y="15" width="16" height="1" fill="white"/>
          
          <!-- Transfer arrows -->
          <path d="M17 4L19 6L17 8" stroke="white" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round" opacity="0.9"/>
          <path d="M19 6H13" stroke="white" stroke-width="1.5" stroke-linecap="round" opacity="0.9"/>
          <path d="M7 18L5 20L7 22" stroke="white" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round" opacity="0.9"/>
          <path d="M5 20H11" stroke="white" stroke-width="1.5" stroke-linecap="round" opacity="0.9"/>
        </svg>
        <h1>SchedBank</h1>
      </div>
      <nav class="nav">
        <button @click="showForm = true" class="btn primary">Agendar</button>
        <button @click="showForm = false" class="btn">Extrato</button>
      </nav>
    </header>

    <main class="main">
      <section class="panel form" v-if="showForm">
        <h2>Agendar Transferência</h2>
        <form @submit.prevent="onSubmit">
          <label>Conta de Origem
            <input v-model="form.sourceAccount" placeholder="XXXXXXXXXX" maxlength="10" required />
          </label>

          <label>Conta de Destino
            <input v-model="form.targetAccount" placeholder="XXXXXXXXXX" maxlength="10" required />
          </label>

          <label>Valor (R$)
            <input v-model.number="form.amount" type="number" step="0.01" min="0.01" required />
          </label>

          <label>Data da Transferência
            <input v-model="form.transferDate" type="date" :min="todayStr" required />
          </label>

          <div class="row">
            <div>Taxa calculada: <strong>{{ feeDisplay }}</strong></div>
            <div>Total débito: <strong>{{ totalDisplay }}</strong></div>
          </div>

          <div class="actions">
            <button type="submit" class="btn primary">Confirmar agendamento</button>
            <button type="button" class="btn" @click="resetForm">Limpar</button>
          </div>
        </form>
        <p class="note">A taxa é calculada automaticamente a partir da data da transferência.</p>
      </section>

      <section class="panel list" v-else>
        <div class="header-section">
          <h2>Extrato de Agendamentos</h2>
          <div class="actions">
            <button class="btn primary" @click="toggleForm">Nova Transferência</button>
            <button class="btn danger" @click="clearAllTransfers" v-if="transfers.length > 0">Limpar Extratos</button>
          </div>
        </div>
        <div v-if="isLoading" class="empty">Carregando...</div>
        <div v-else>
          <div v-if="transfers.length === 0" class="empty">Nenhum agendamento encontrado.</div>
          <ul class="schedules">
            <li v-for="t in transfers" :key="t.id" class="schedule-item">
              <div class="info">
                <div><strong>{{ t.sourceAccount }}</strong> → <strong>{{ t.targetAccount }}</strong></div>
                <div>Data transferência: {{ t.transferDate }} | Agendado em: {{ t.createdAt }}</div>
              </div>
              <div class="meta">
                <div>Valor: R$ {{ Number(t.amount).toFixed(2) }}</div>
                <div>Taxa: R$ {{ Number(t.fee || 0).toFixed(2) }}</div>
                <div>Total: R$ {{ (Number(t.amount) + Number(t.fee || 0)).toFixed(2) }}</div>
                <div class="actions-transfer">
                  <button @click="editTransfer(t)" class="btn-small edit">Editar</button>
                  <button @click="deleteTransfer(t.id)" class="btn-small danger">Excluir</button>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </section>
    </main>

    <!-- Modal de Edição -->
    <div v-if="editingTransfer" class="modal-overlay" @click="cancelEdit">
      <div class="modal-content" @click.stop>
        <h3>Editar Transferência</h3>
        <form @submit.prevent="updateTransfer">
          <label>Conta de Origem
            <input v-model="editForm.sourceAccount" placeholder="XXXXXXXXXX" maxlength="10" required />
          </label>
          <label>Conta de Destino
            <input v-model="editForm.targetAccount" placeholder="XXXXXXXXXX" maxlength="10" required />
          </label>
          <label>Valor (R$)
            <input v-model.number="editForm.amount" type="number" step="0.01" min="0.01" required />
          </label>
          <label>Data da Transferência
            <input v-model="editForm.transferDate" type="date" :min="todayStr" required />
          </label>
          <div class="modal-actions">
            <button type="submit" class="btn primary">Salvar</button>
            <button type="button" class="btn" @click="cancelEdit">Cancelar</button>
          </div>
        </form>
      </div>
    </div>

    <footer class="footer">
      <div class="footer-content">
        <p>Orgulhosamente Desenvolvido por <strong>Cezi Cola Senior Software Engineer Java</strong></p>
        <p>Todos os Direitos Reservados 2025</p>
        <p>Sistema de Pagamentos Desenvolvido em <strong>Java Spring Boot</strong> no backend e Frontend <strong>Vue.js</strong></p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useTransferStore } from './application/stores/TransferStore.js'
import { FeeCalculationService } from './application/services/FeeCalculationService.js'
import { FormValidationService } from './application/services/FormValidationService.js'
import { UserFeedbackService } from './application/services/UserFeedbackService.js'

export default {
  setup() {
    const store = useTransferStore()
    const showForm = ref(true)
    const editingTransfer = ref(null)
    
    const todayStr = new Date().toISOString().slice(0, 10)

    const form = reactive(createEmptyForm(todayStr))
    const editForm = reactive(createEmptyEditForm(todayStr))

    const isLoading = computed(() => store.isLoading)
    const transfers = computed(() => store.transfers)

    const feeComputed = computed(() => {
      const amount = Number(form.amount) || 0
      return FeeCalculationService.calculateTransferFee(amount, form.transferDate)
    })

    const feeDisplay = computed(() => {
      if (!feeComputed.value) return '—'
      if (feeComputed.value.error) return `Erro: ${feeComputed.value.error}`
      return FeeCalculationService.formatCurrency(feeComputed.value.fee)
    })

    const totalDisplay = computed(() => {
      if (feeComputed.value && feeComputed.value.fee != null) {
        const total = Number(form.amount || 0) + feeComputed.value.fee
        return FeeCalculationService.formatCurrency(total)
      }
      return '—'
    })

    async function onSubmit() {
      const validation = FormValidationService.validateTransferForm(form)
      
      if (!validation.isValid) {
        UserFeedbackService.showValidationErrors(validation.errors)
        return
      }

      try {
        const transferRequest = createTransferRequest(form)
        await store.scheduleTransfer(transferRequest)
        resetForm()
        showForm.value = false
        UserFeedbackService.showSuccessMessage('Transferência agendada com sucesso!')
      } catch (error) {
        UserFeedbackService.showErrorMessage(error)
      }
    }

    function resetForm() {
      Object.assign(form, createEmptyForm(todayStr))
    }

    function editTransfer(transfer) {
      editingTransfer.value = transfer
      Object.assign(editForm, createEditFormFromTransfer(transfer))
    }

    function cancelEdit() {
      editingTransfer.value = null
      Object.assign(editForm, createEmptyEditForm(todayStr))
    }

    async function updateTransfer() {
      const validation = FormValidationService.validateTransferForm(editForm)
      
      if (!validation.isValid) {
        UserFeedbackService.showValidationErrors(validation.errors)
        return
      }

      try {
        const transferRequest = createTransferRequest(editForm)
        await store.updateTransfer(transferRequest)
        cancelEdit()
        UserFeedbackService.showSuccessMessage('Transferência atualizada com sucesso!')
      } catch (error) {
        UserFeedbackService.showErrorMessage(error)
      }
    }

    async function deleteTransfer(transferId) {
      console.log('Delete button clicked for transfer ID:', transferId)
      
      if (!UserFeedbackService.confirmAction('Tem certeza que deseja excluir esta transferência?')) {
        console.log('User cancelled delete action')
        return
      }

      try {
        console.log('Calling store.deleteTransfer...')
        await store.deleteTransfer(transferId)
        console.log('Transfer deleted successfully')
        UserFeedbackService.showSuccessMessage('Transferência excluída com sucesso!')
      } catch (error) {
        console.error('Error deleting transfer:', error)
        UserFeedbackService.showErrorMessage(error)
      }
    }

    async function clearAllTransfers() {
      console.log('Clear All button clicked')
      
      if (!UserFeedbackService.confirmAction('Tem certeza que deseja limpar todos os extratos? Esta ação não pode ser desfeita.')) {
        console.log('User cancelled clear all action')
        return
      }

      try {
        console.log('Calling store.clearAllTransfers...')
        await store.clearAllTransfers()
        console.log('All transfers cleared successfully')
        UserFeedbackService.showSuccessMessage('Todos os extratos foram limpos com sucesso!')
      } catch (error) {
        console.error('Error clearing transfers:', error)
        UserFeedbackService.showErrorMessage(error)
      }
    }

    onMounted(async () => {
      console.log('Component mounted, loading transfers...')
      try {
        await store.loadTransfers()
        console.log('Transfers loaded:', store.transfers.length)
      } catch (error) {
        console.error('Failed to load transfers:', error)
      }
    })

    return {
      form,
      editForm,
      isLoading,
      transfers,
      feeDisplay,
      totalDisplay,
      showForm,
      editingTransfer,
      onSubmit,
      resetForm,
      editTransfer,
      cancelEdit,
      updateTransfer,
      deleteTransfer,
      clearAllTransfers,
      toggleForm: () => { showForm.value = !showForm.value },
      formatCurrency: FeeCalculationService.formatCurrency,
      todayStr
    }
  }
}

function createEmptyForm(todayStr) {
  return {
    sourceAccount: '',
    targetAccount: '',
    amount: 0.0,
    transferDate: todayStr
  }
}

function createEmptyEditForm(todayStr) {
  return {
    id: '',
    sourceAccount: '',
    targetAccount: '',
    amount: 0.0,
    transferDate: todayStr
  }
}

function createEditFormFromTransfer(transfer) {
  return {
    id: transfer.id,
    sourceAccount: transfer.sourceAccount,
    targetAccount: transfer.targetAccount,
    amount: Number(transfer.amount),
    transferDate: transfer.transferDate
  }
}

function createTransferRequest(formData) {
  return {
    id: formData.id,
    sourceAccount: formData.sourceAccount,
    targetAccount: formData.targetAccount,
    amount: Number(formData.amount),
    transferDate: formData.transferDate
  }
}
</script>

<style scoped>
.app { 
  max-width: 920px; 
  margin: 1.5rem auto; 
  font-family: Inter, system-ui, Arial; 
  color: #1b1b1f 
}
.header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  margin-bottom: 1rem 
}
.logo { 
  display: flex; 
  gap: 0.75rem; 
  align-items: center;
  transition: transform 0.2s ease-in-out;
}
.logo:hover {
  transform: scale(1.05);
}
.logo svg {
  transition: all 0.3s ease-in-out;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(11, 95, 255, 0.2);
}
.logo svg:hover {
  box-shadow: 0 4px 16px rgba(11, 95, 255, 0.3);
  transform: rotate(1deg);
}
.logo h1 { 
  margin: 0; 
  font-size: 1.25rem;
  font-weight: 700;
  background: linear-gradient(135deg, #0b5fff, #1890ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(11, 95, 255, 0.1);
}
.nav .btn { 
  margin-left: 0.5rem 
}
.main { 
  display: flex; 
  gap: 1rem 
}
.panel { 
  flex: 1; 
  background: #fff; 
  border: 1px solid #e8e8ef; 
  padding: 1rem; 
  border-radius: 8px 
}
.form label { 
  display: block; 
  margin-bottom: 0.75rem 
}
.form input { 
  width: 100%; 
  padding: 0.5rem; 
  margin-top: 0.25rem; 
  border: 1px solid #dcdce6; 
  border-radius: 4px 
}
.row { 
  display: flex; 
  justify-content: space-between; 
  margin-top: 0.5rem 
}
.actions { 
  margin-top: 1rem; 
  display: flex; 
  gap: 0.5rem 
}
.btn { 
  padding: 0.5rem 0.75rem; 
  border-radius: 6px; 
  border: 1px solid #cfcfd7; 
  background: #fff; 
  cursor: pointer 
}
.btn.primary { 
  background: #0b5fff; 
  color: white; 
  border-color: #0b5fff 
}
.btn.danger { 
  background: #ff4d4f; 
  color: white 
}
.schedules { 
  list-style: none; 
  padding: 0; 
  margin: 0 
}
.schedule-item { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  padding: 0.5rem 0; 
  border-bottom: 1px dashed #efeff5 
}
.footer { 
  text-align: center; 
  margin-top: 1rem; 
  color: #6b6b75 
}
.empty { 
  color: #6b6b75 
}
.note { 
  font-size: 0.85rem; 
  color: #666 
}
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.header-section h2 {
  margin: 0;
}
.actions-transfer {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}
.btn-small {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  border-radius: 4px;
  border: 1px solid #cfcfd7;
  background: #fff;
  cursor: pointer;
}
.btn-small.edit {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}
.btn-small.danger {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}
.modal-content h3 {
  margin-top: 0;
}
</style>