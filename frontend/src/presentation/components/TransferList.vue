<template>
  <div class="transfer-list">
    <div class="card">
      <div class="card-header">
        <h2>Transfer History</h2>
        <p class="subtitle">View all scheduled transfers</p>
        <div class="header-actions">
          <button @click="refreshTransfers" class="btn btn-outline" :disabled="isLoading">
            {{ isLoading ? 'Loading...' : 'Refresh' }}
          </button>
        </div>
      </div>

      <div class="transfer-summary" v-if="transferStore.transferCount > 0">
        <div class="summary-item">
          <span class="summary-label">Total Transfers:</span>
          <span class="summary-value">{{ transferStore.transferCount }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">Total Amount:</span>
          <span class="summary-value">R$ {{ formatCurrency(transferStore.totalScheduledAmount) }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">Total Fees:</span>
          <span class="summary-value">R$ {{ formatCurrency(transferStore.totalFeesAmount) }}</span>
        </div>
      </div>

      <div v-if="isLoading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>Loading transfers...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <p class="error-message">{{ error }}</p>
        <button @click="refreshTransfers" class="btn btn-primary">Try Again</button>
      </div>

      <div v-else-if="transfers.length === 0" class="empty-state">
        <div class="empty-icon">[ ]</div>
        <h3>No transfers scheduled</h3>
        <p>Schedule your first transfer to see it here.</p>
      </div>

      <div v-else class="transfers-container">
        <div class="transfers-grid">
          <div 
            v-for="transfer in formattedTransfers" 
            :key="transfer.id"
            class="transfer-card"
            :class="getTransferStatusClass(transfer)"
          >
            <div class="transfer-header">
              <span class="transfer-id">{{ transfer.id.substring(0, 8) }}...</span>
              <span class="transfer-status">{{ getTransferStatus(transfer) }}</span>
            </div>

            <div class="transfer-accounts">
              <div class="account-info">
                <span class="account-label">From:</span>
                <span class="account-number">{{ transfer.from }}</span>
              </div>
              <div class="transfer-arrow">→</div>
              <div class="account-info">
                <span class="account-label">To:</span>
                <span class="account-number">{{ transfer.to }}</span>
              </div>
            </div>

            <div class="transfer-amounts">
              <div class="amount-row">
                <span class="amount-label">Amount:</span>
                <span class="amount-value primary">{{ transfer.amount }}</span>
              </div>
              <div class="amount-row">
                <span class="amount-label">Fee:</span>
                <span class="amount-value">{{ transfer.fee }}</span>
              </div>
              <div class="amount-row total">
                <span class="amount-label">Total:</span>
                <span class="amount-value">{{ transfer.total }}</span>
              </div>
            </div>

            <div class="transfer-dates">
              <div class="date-info">
                <span class="date-label">Scheduled:</span>
                <span class="date-value">{{ transfer.scheduleDate }}</span>
              </div>
              <div class="date-info">
                <span class="date-label">Transfer:</span>
                <span class="date-value">{{ transfer.transferDate }}</span>
              </div>
            </div>

            <div class="transfer-footer">
              <span class="days-info">
                {{ getDaysMessage(transfer.daysUntilTransfer) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useTransferStore } from '@application/stores/TransferStore.js'

const transferStore = useTransferStore()

const isLoading = computed(() => transferStore.isLoading)
const error = computed(() => transferStore.error)
const transfers = computed(() => transferStore.transfers)

const formattedTransfers = computed(() => {
  return transfers.value.map(transfer => transfer.toFormattedSummary())
})

onMounted(async () => {
  await refreshTransfers()
})

async function refreshTransfers() {
  try {
    await transferStore.loadTransfers()
  } catch (err) {
    console.error('Failed to load transfers:', err)
  }
}

function formatCurrency(amount) {
  return new Intl.NumberFormat('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount)
}

function getTransferStatus(transfer) {
  const days = transfer.daysUntilTransfer
  if (days < 0) return 'Completed'
  if (days === 0) return 'Today'
  if (days <= 7) return 'This week'
  return 'Scheduled'
}

function getTransferStatusClass(transfer) {
  const days = transfer.daysUntilTransfer
  if (days < 0) return 'completed'
  if (days === 0) return 'today'
  if (days <= 7) return 'soon'
  return 'scheduled'
}

function getDaysMessage(days) {
  if (days < 0) return `Completed ${Math.abs(days)} day(s) ago`
  if (days === 0) return 'Transfer today'
  if (days === 1) return 'Transfer tomorrow'
  return `Transfer in ${days} days`
}
</script>

<style scoped>
.transfer-list {
  max-width: 1200px;
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.header-actions {
  display: flex;
  gap: 12px;
}

.transfer-summary {
  background: #e8f4fd;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-label {
  font-weight: 500;
  color: #495057;
}

.summary-value {
  font-weight: 600;
  color: #2c3e50;
  font-size: 18px;
}

.loading-state {
  padding: 60px 24px;
  text-align: center;
  color: #6c757d;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-state {
  padding: 60px 24px;
  text-align: center;
}

.error-message {
  color: #dc3545;
  margin-bottom: 16px;
}

.empty-state {
  padding: 60px 24px;
  text-align: center;
  color: #6c757d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #495057;
}

.transfers-container {
  padding: 24px;
}

.transfers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.transfer-card {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  background: white;
  transition: all 0.2s;
}

.transfer-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.transfer-card.today {
  border-left: 4px solid #ffc107;
  background: #fffef7;
}

.transfer-card.soon {
  border-left: 4px solid #28a745;
  background: #f8fff9;
}

.transfer-card.scheduled {
  border-left: 4px solid #007bff;
  background: #f8fbff;
}

.transfer-card.completed {
  border-left: 4px solid #6c757d;
  background: #f8f9fa;
  opacity: 0.8;
}

.transfer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.transfer-id {
  font-family: monospace;
  font-size: 12px;
  color: #6c757d;
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 4px;
}

.transfer-status {
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
  color: #495057;
}

.transfer-accounts {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
}

.account-info {
  flex: 1;
  text-align: center;
}

.account-label {
  display: block;
  font-size: 12px;
  color: #6c757d;
  margin-bottom: 4px;
}

.account-number {
  font-family: monospace;
  font-weight: 500;
  color: #2c3e50;
}

.transfer-arrow {
  font-size: 20px;
  color: #007bff;
  font-weight: bold;
}

.transfer-amounts {
  margin-bottom: 16px;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.amount-row.total {
  border-top: 1px solid #e9ecef;
  padding-top: 8px;
  margin-top: 8px;
  font-weight: 600;
}

.amount-label {
  color: #6c757d;
  font-size: 14px;
}

.amount-value {
  font-weight: 500;
  color: #2c3e50;
}

.amount-value.primary {
  color: #007bff;
  font-weight: 600;
}

.transfer-dates {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.date-info {
  text-align: center;
}

.date-label {
  display: block;
  font-size: 12px;
  color: #6c757d;
  margin-bottom: 4px;
}

.date-value {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
}

.transfer-footer {
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid #f8f9fa;
}

.days-info {
  font-size: 12px;
  color: #6c757d;
  font-style: italic;
}

.btn {
  padding: 8px 16px;
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

.btn-outline {
  background: transparent;
  color: #007bff;
  border: 1px solid #007bff;
}

.btn-outline:hover:not(:disabled) {
  background: #007bff;
  color: white;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #0056b3;
}

@media (max-width: 768px) {
  .transfers-grid {
    grid-template-columns: 1fr;
  }
  
  .card-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .transfer-summary {
    grid-template-columns: 1fr;
  }
}
</style>
