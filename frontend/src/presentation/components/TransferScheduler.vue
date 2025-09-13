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
import { ref, computed, inject, onMounted } from 'vue'
import { useTransferStore } from '@application/stores/TransferStore.js'

const transferService = inject('transferService')
const transferStore = useTransferStore()

const form = ref({
  sourceAccount: '',
  targetAccount: '',
  amount: '',
  transferDate: ''
})

const errors = ref({})
const isSubmitting = ref(false)
const successMessage = ref('')
const feePreview = ref(null)

const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const isFormValid = computed(() => {
  return form.value.sourceAccount &&
         form.value.targetAccount &&
         form.value.amount &&
         form.value.transferDate &&
         Object.keys(errors.value).length === 0
})

onMounted(() => {
  form.value.transferDate = minDate.value
})

function clearFieldError(field) {
  if (errors.value[field]) {
    delete errors.value[field]
  }
  if (errors.value.general) {
    errors.value.general = null
  }
}

function updateFeePreview() {
  if (!form.value.transferDate || !transferService) return

  try {
    const days = transferService.calculateDaysUntilTransfer(form.value.transferDate)
    const description = transferService.getFeeRangeDescription(days)
    
    let estimatedFee = 'Contact support'
    if (days === 0) estimatedFee = 'R$ 3.00 + 2.5% of amount'
    else if (days >= 1 && days <= 10) estimatedFee = 'R$ 12.00'
    else if (days >= 11 && days <= 20) estimatedFee = '8.2% of amount'
    else if (days >= 21 && days <= 30) estimatedFee = '6.9% of amount'
    else if (days >= 31 && days <= 40) estimatedFee = '4.7% of amount'
    else if (days >= 41 && days <= 50) estimatedFee = '1.7% of amount'

    feePreview.value = {
      days,
      description,
      estimatedFee
    }
  } catch (error) {
    feePreview.value = null
  }
}

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

    const result = await transferStore.scheduleTransfer(transferRequest)
    
    successMessage.value = `Transfer scheduled successfully! Fee: ${result.fee.toFormattedString()}`
    resetForm()
    
    setTimeout(() => {
      successMessage.value = ''
    }, 5000)

  } catch (error) {
    if (error.field) {
      errors.value[error.field] = error.message
    } else {
      errors.value.general = error.message
    }
  } finally {
    isSubmitting.value = false
  }
}

function validateForm() {
  errors.value = {}

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
    errors.value.amount = 'Amount is required'
  } else if (parseFloat(form.value.amount) <= 0) {
    errors.value.amount = 'Amount must be positive'
  }

  if (!form.value.transferDate) {
    errors.value.transferDate = 'Transfer date is required'
  }

  return Object.keys(errors.value).length === 0
}

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
