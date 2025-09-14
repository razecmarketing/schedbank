<template>
  <div class="transfer-scheduler">
    <div class="card">
      <div class="card-header">
        <h2>Schedule Transfer</h2>
        <p class="subtitle">Create a new financial transfer</p>
      </div>

      <form @submit.prevent="handleSubmit" class="transfer-form">
        <div class="form-row">
          <div class="form-group">
            <label for="sourceAccount" class="form-label">Source Account</label>
            <input
              id="sourceAccount"
              v-model="form.sourceAccount"
              type="text"
              maxlength="10"
              pattern="[0-9]{10}"
              class="form-input"
              :class="{ 'error': errors.sourceAccount }"
              placeholder="1234567890"
              @input="clearFieldError('sourceAccount')"
            />
            <span v-if="errors.sourceAccount" class="error-message">{{ errors.sourceAccount }}</span>
          </div>

          <div class="form-group">
            <label for="targetAccount" class="form-label">Target Account</label>
            <input
              id="targetAccount"
              v-model="form.targetAccount"
              type="text"
              maxlength="10"
              pattern="[0-9]{10}"
              class="form-input"
              :class="{ 'error': errors.targetAccount }"
              placeholder="0987654321"
              @input="clearFieldError('targetAccount')"
            />
            <span v-if="errors.targetAccount" class="error-message">{{ errors.targetAccount }}</span>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="amount" class="form-label">Amount (R$)</label>
            <input
              id="amount"
              v-model="form.amount"
              type="number"
              step="0.01"
              min="0.01"
              class="form-input"
              :class="{ 'error': errors.amount }"
              placeholder="1000.00"
              @input="clearFieldError('amount')"
            />
            <span v-if="errors.amount" class="error-message">{{ errors.amount }}</span>
          </div>

          <div class="form-group">
            <label for="transferDate" class="form-label">Transfer Date</label>
            <input
              id="transferDate"
              v-model="form.transferDate"
              type="date"
              :min="minDate"
              class="form-input"
              :class="{ 'error': errors.transferDate }"
              @change="updateFeePreview"
              @input="clearFieldError('transferDate')"
            />
            <span v-if="errors.transferDate" class="error-message">{{ errors.transferDate }}</span>
          </div>
        </div>

        <div v-if="feePreview" class="fee-preview">
          <div class="fee-info">
            <h3>Fee Calculation Preview</h3>
            <p><strong>Days until transfer:</strong> {{ feePreview.days }}</p>
            <p><strong>Fee range:</strong> {{ feePreview.description }}</p>
            <p><strong>Estimated fee:</strong> {{ feePreview.estimatedFee }}</p>
          </div>
        </div>

        <div class="form-actions">
          <button 
            type="button" 
            @click="resetForm" 
            class="btn btn-secondary"
            :disabled="isSubmitting"
          >
            Clear
          </button>
          <button 
            type="submit" 
            class="btn btn-primary"
            :disabled="isSubmitting || !isFormValid"
          >
            {{ isSubmitting ? 'Scheduling...' : 'Schedule Transfer' }}
          </button>
        </div>
      </form>

      <div v-if="errors.general" class="error-banner">
        {{ errors.general }}
      </div>

      <div v-if="successMessage" class="success-banner">
        {{ successMessage }}
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * TransferScheduler Component - Seguindo Padrão Pessoal de Programação
 * 
 * Princípios aplicados:
 * - Engenharia de Software: simples, claro, modular, sustentável
 * - Código como obra de engenharia: frameworks são detalhes, não o núcleo
 * - Modularidade extrema: cada função com propósito único
 * - Arquitetura evolutiva: refatoração contínua, mudanças naturais
 * - Pragmatismo: simplicidade no núcleo, "código é a verdade"
 */
import { ref, computed, inject, onMounted } from 'vue'
import { useTransferStore } from '../../application/stores/TransferStore.js'
import { FormValidationService } from '../../application/services/FormValidationService.js'

// === DEPENDENCY INJECTION (Arquitetura Limpa) ===
const transferService = inject('transferService')
const store = useTransferStore()

// === STATE MANAGEMENT (Fundamentos Matemáticos + Imutabilidade) ===
/**
 * Formulário reativo - Estado matematicamente preciso
 * Cada propriedade tem invariantes garantidas e é imutável por design
 */
const form = ref({
  sourceAccount: '',
  targetAccount: '',
  amount: '',
  transferDate: ''
})

/**
 * Erros de validação - Separação de responsabilidades
 * Estado de erro isolado do estado de dados para clareza arquitetural
 */
const errors = ref({})

/**
 * Estados de UI - Interface resiliente e responsiva
 * Controle de estados de carregamento e feedback do usuário
 */
const isSubmitting = ref(false)
const successMessage = ref('')
const feePreview = ref(null)

// === COMPUTED PROPERTIES (Performance Inteligente) ===
/**
 * Data mínima - Cálculo reativo otimizado
 * "Otimização prematura é a raiz de todo mal" - computed quando necessário
 */
const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

/**
 * Validação de formulário - Reatividade matemática precisa
 * Validação em tempo real com lógica clara e testável
 */
const isFormValid = computed(() => {
  return form.value.sourceAccount &&
         form.value.targetAccount &&
         form.value.amount &&
         form.value.transferDate &&
         Object.keys(errors.value).length === 0
})

// === LIFECYCLE HOOKS (Inicialização Controlada) ===
onMounted(() => {
  initializeComponent()
})

// === BUSINESS METHODS (Modularidade Extrema) ===

/**
 * Inicialização do componente - Single Responsibility Principle
 * Responsabilidade única: preparar estado inicial do componente
 */
function initializeComponent() {
  form.value.transferDate = minDate.value
}

/**
 * Limpar erro de campo - Feedback Imediato ao Usuário
 * Implementação clara e testável para UX responsiva
 */
function clearFieldError(fieldName) {
  if (errors.value[fieldName]) {
    delete errors.value[fieldName]
  }
  if (errors.value.general) {
    delete errors.value.general
  }
}

/**
 * Atualizar preview da taxa - Cálculo em Tempo Real
 * Lógica de negócio isolada e testável
 */
function updateFeePreview() {
  if (!form.value.transferDate || !transferService) return

  try {
    const days = transferService.calculateDaysUntilTransfer(form.value.transferDate)
    const description = transferService.getFeeRangeDescription(days)
    
    let estimatedFee = 'Contact support'
    if (form.value.amount && !isNaN(parseFloat(form.value.amount))) {
      const amount = parseFloat(form.value.amount)
      estimatedFee = transferService.calculateFee(amount, days)
    }

    feePreview.value = {
      days,
      description,
      estimatedFee
    }
  } catch (error) {
    console.warn('Fee calculation error:', error.message)
    feePreview.value = null
  }
}

/**
 * Validar formulário - Disciplina de Programação
 * Validação rigorosa seguindo invariantes matemáticas
 */
function validateForm() {
  errors.value = {}
  
  // Validação básica inline para garantir funcionamento
  if (!form.value.sourceAccount) {
    errors.value.sourceAccount = 'Source account is required'
  } else if (!/^\d{10}$/.test(form.value.sourceAccount)) {
    errors.value.sourceAccount = 'Source account must be exactly 10 digits'
  }
  
  if (!form.value.targetAccount) {
    errors.value.targetAccount = 'Target account is required'  
  } else if (!/^\d{10}$/.test(form.value.targetAccount)) {
    errors.value.targetAccount = 'Target account must be exactly 10 digits'
  }
  
  if (!form.value.amount) {
    errors.value.amount = 'Amount is required'
  } else if (parseFloat(form.value.amount) <= 0) {
    errors.value.amount = 'Amount must be positive'
  }
  
  if (!form.value.transferDate) {
    errors.value.transferDate = 'Transfer date is required'
  }
  
  return Object.keys(errors.value).length === 0
}

/**
 * Submeter formulário - Arquitetura Resiliente
 * Orquestração de processo com tratamento de erros robusto
 */
async function handleSubmit() {
  if (!validateForm()) return

  isSubmitting.value = true
  errors.value = {}
  successMessage.value = ''

  try {
    const transferRequest = {
      sourceAccount: form.value.sourceAccount,
      targetAccount: form.value.targetAccount,
      amount: parseFloat(form.value.amount),
      transferDate: form.value.transferDate
    }

    const result = await store.scheduleTransfer(transferRequest)
    
    successMessage.value = `Transfer scheduled successfully! Fee: ${result.fee.toFormattedString()}`
    resetForm()
    
    setTimeout(() => {
      successMessage.value = ''
    }, 5000)

  } catch (error) {
    console.error('Transfer submission error:', error)
    
    if (error.response?.status === 400) {
      errors.value.general = 'Invalid transfer data. Please check all fields.'
    } else if (error.response?.status === 500) {
      errors.value.general = 'Server error. Please try again later.'
    } else {
      errors.value.general = 'Network error. Please check your connection and try again.'
    }
  } finally {
    isSubmitting.value = false
  }
}

/**
 * Reset do formulário - Estado Inicial Limpo
 * Volta ao estado matematicamente consistente inicial
 */
function resetForm() {
  form.value = {
    sourceAccount: '',
    targetAccount: '',
    amount: '',
    transferDate: minDate.value
  }
  errors.value = {}
  successMessage.value = ''
  feePreview.value = null
}
</script>
onMounted(() => {
  initializeDefaultValues()
})

// === PRIVATE BUSINESS METHODS (Modularidade Extrema) ===

/**
 * Inicializa valores padrão do formulário
 * Responsabilidade única: preparar estado inicial
 */
function initializeDefaultValues() {
  formState.value.transferDate = minDate.value
}

/**
 * Limpa erro específico de campo - Pattern de Feedback Imediato
 * @param {string} fieldName - nome do campo
 */
function clearFieldError(fieldName) {
  const currentErrors = { ...validationState.value.errors }
  
  if (currentErrors[fieldName]) {
    delete currentErrors[fieldName]
    validationState.value.errors = currentErrors
  }
  
  // Remove erro geral se existir
  if (currentErrors.general) {
    delete currentErrors.general
    validationState.value.errors = currentErrors
  }
}

/**
 * Atualiza preview da taxa - Cálculo em tempo real
 * Responsabilidade única: calcular e exibir taxa estimada
 */
function updateFeePreview() {
  if (!formState.value.transferDate || !transferService) return

  try {
    const days = transferService.calculateDaysUntilTransfer(formState.value.transferDate)
    const description = transferService.getFeeRangeDescription(days)
    
    let estimatedFee = 'Contact support'
    if (formState.value.amount && !isNaN(parseFloat(formState.value.amount))) {
      const amount = parseFloat(formState.value.amount)
      estimatedFee = transferService.calculateFee(amount, days)
    }

    uiState.value.feePreview = {
      days,
      description,
      estimatedFee
    }
  } catch (error) {
    console.warn('Erro ao calcular taxa:', error.message)
    uiState.value.feePreview = null
  }
}
    if (days === 0) estimatedFee = 'R$ 3.00 + 2.5% of amount'
    else if (days >= 1 && days <= 10) estimatedFee = 'R$ 12.00'
    else if (days >= 11 && days <= 20) estimatedFee = '8.2% of amount'
    else if (days >= 21 && days <= 30) estimatedFee = '6.9% of amount'
    else if (days >= 31 && days <= 40) estimatedFee = '4.7% of amount'
    else if (days >= 41 && days <= 50) estimatedFee = '1.7% of amount'

/**
 * Valida todo o formulário - Validação Rigorosa
 * @returns {boolean} - true se válido, false caso contrário
 */
function validateForm() {
  const currentForm = formState.value
  const validationResult = FormValidationService.validateTransferForm(currentForm)
  
  validationState.value.errors = validationResult.errors
  validationState.value.isValid = validationResult.isValid
  
  return validationResult.isValid
}

/**
 * Manipula envio do formulário - Pattern de Command
 * Responsabilidade única: orquestrar o processo de agendamento
 */
async function handleFormSubmission() {
  if (!validateForm()) return

  uiState.value.isSubmitting = true
  validationState.value.errors = {}
  uiState.value.successMessage = ''

  try {
    const transferRequest = createTransferRequest(formState.value)
    const result = await transferStore.scheduleTransfer(transferRequest)
    
    handleSuccessfulSubmission(result)
    
  } catch (error) {
    handleSubmissionError(error)
  } finally {
    uiState.value.isSubmitting = false
  }
}

/**
 * Cria objeto de requisição - Factory Pattern
 * @param {Object} formData - dados do formulário
 * @returns {Object} - requisição formatada
 */
function createTransferRequest(formData) {
  return {
    sourceAccount: formData.sourceAccount,
    targetAccount: formData.targetAccount,
    amount: parseFloat(formData.amount),
    transferDate: formData.transferDate
  }
}

/**
 * Trata sucesso no envio - Pattern de Success Handler
 * @param {Object} result - resultado da operação
 */
function handleSuccessfulSubmission(result) {
  uiState.value.successMessage = `Transfer scheduled successfully! Fee: ${result.fee.toFormattedString()}`
  resetFormToInitialState()
}

/**
 * Trata erro no envio - Pattern de Error Handler
 * @param {Error} error - erro capturado
 */
function handleSubmissionError(error) {
  console.error('Transfer submission error:', error)
  
  if (error.response?.status === 400) {
    validationState.value.errors = { general: 'Invalid transfer data. Please check all fields.' }
  } else if (error.response?.status === 500) {
    validationState.value.errors = { general: 'Server error. Please try again later.' }
  } else {
    validationState.value.errors = { general: 'Network error. Please check your connection and try again.' }
  }
}

/**
 * Reseta formulário para estado inicial - Pattern de Reset
 * Responsabilidade única: limpar estado e voltar ao inicial
 */
function resetFormToInitialState() {
  formState.value = {
    sourceAccount: '',
    targetAccount: '',
    amount: '',
    transferDate: minDate.value
  }
  
  validationState.value = {
    errors: {},
    isValid: false
  }
  
  uiState.value.feePreview = null
  
  // Auto-remove mensagem de sucesso após 5 segundos
  setTimeout(() => {
    uiState.value.successMessage = ''
  }, 5000)
}

  if (!form.value.sourceAccount) {
    errors.value.sourceAccount = 'Source account is required'
  } else if (!/^\d{10}$/.test(form.value.sourceAccount)) {
    errors.value.sourceAccount = 'Source account must be exactly 10 digits'
  }

  if (!form.value.targetAccount) {
    errors.value.targetAccount = 'Target account is required'
  } else if (!/^\d{10}$/.test(form.value.targetAccount)) {
    errors.value.targetAccount = 'Target account must be exactly 10 digits'
  }

  if (form.value.sourceAccount === form.value.targetAccount) {
    errors.value.targetAccount = 'Target account must be different from source account'
  }

  if (!form.value.amount) {
</script>

<style scoped>
.transfer-scheduler {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  background: #f8f9fa;
  padding: 24px;
  border-bottom: 1px solid #e9ecef;
}

.card-header h2 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
}

.transfer-form {
  padding: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-input {
  padding: 12px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.form-input.error {
  border-color: #dc3545;
}

.error-message {
  color: #dc3545;
  font-size: 12px;
  margin-top: 4px;
}

.fee-preview {
  background: #e7f3ff;
  border: 1px solid #b8daff;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 20px;
}

.fee-info h3 {
  margin: 0 0 12px 0;
  color: #004085;
  font-size: 16px;
}

.fee-info p {
  margin: 4px 0;
  color: #004085;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.btn {
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #5a6268;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #0056b3;
}

.error-banner {
  background: #f8d7da;
  color: #721c24;
  padding: 12px;
  border-radius: 4px;
  margin-top: 16px;
  border: 1px solid #f5c6cb;
}

.success-banner {
  background: #d4edda;
  color: #155724;
  padding: 12px;
  border-radius: 4px;
  margin-top: 16px;
  border: 1px solid #c3e6cb;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
