import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useTransferStore = defineStore('transfers', () => {
  const transfers = ref([])
  const isLoading = ref(false)
  const error = ref(null)
  
  let transferService = null

  function setTransferService(service) {
    transferService = service
  }

  const transferCount = computed(() => transfers.value.length)
  
  const totalScheduledAmount = computed(() => {
    return transfers.value.reduce((total, transfer) => {
      return total + (transfer.amount?.amount || 0)
    }, 0)
  })

  const totalFeesAmount = computed(() => {
    return transfers.value.reduce((total, transfer) => {
      return total + (transfer.fee?.amount || 0)
    }, 0)
  })

  async function scheduleTransfer(transferRequest) {
    if (!transferService) {
      throw new Error('Transfer service not initialized')
    }

    isLoading.value = true
    error.value = null

    try {
      const newTransfer = await transferService.scheduleTransfer(transferRequest)
      transfers.value.push(newTransfer)
      return newTransfer
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function loadTransfers() {
    if (!transferService) {
      throw new Error('Transfer service not initialized')
    }

    isLoading.value = true
    error.value = null

    try {
      const loadedTransfers = await transferService.listScheduledTransfers()
      transfers.value = loadedTransfers
      return loadedTransfers
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function updateTransfer(transferRequest) {
    if (!transferService) {
      throw new Error('Transfer service not initialized')
    }

    isLoading.value = true
    error.value = null

    try {
      const updatedTransfer = await transferService.updateTransfer(transferRequest)
      const index = transfers.value.findIndex(t => t.id === transferRequest.id)
      if (index !== -1) {
        transfers.value[index] = updatedTransfer
      }
      return updatedTransfer
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function deleteTransfer(transferId) {
    if (!transferService) {
      throw new Error('Transfer service not initialized')
    }

    if (!transferId) {
      throw new Error('Transfer ID is required for deletion')
    }

    isLoading.value = true
    error.value = null

    try {
      await transferService.deleteTransfer(transferId)
      // Don't mutate state directly - use immutable operations
      const filteredTransfers = transfers.value.filter(t => t.id !== transferId)
      transfers.value = filteredTransfers
    } catch (err) {
      error.value = err.message || 'Failed to delete transfer'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function clearAllTransfers() {
    if (!transferService) {
      throw new Error('Transfer service not initialized')
    }

    isLoading.value = true
    error.value = null

    try {
      await transferService.clearAllTransfers()
      // Make intention clear
      transfers.value = []
    } catch (err) {
      error.value = err.message || 'Failed to clear all transfers'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function clearError() {
    error.value = null
  }

  function clearTransfers() {
    transfers.value = []
  }

  function getTransfersByDateRange(startDate, endDate) {
    return transfers.value.filter(transfer => {
      const transferDate = new Date(transfer.transferDate)
      return transferDate >= startDate && transferDate <= endDate
    })
  }

  function getTransfersSummary() {
    return transfers.value.map(transfer => transfer.toFormattedSummary())
  }

  return {
    transfers: computed(() => transfers.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    transferCount,
    totalScheduledAmount,
    totalFeesAmount,
    setTransferService,
    scheduleTransfer,
    loadTransfers,
    updateTransfer,
    deleteTransfer,
    clearAllTransfers,
    clearError,
    clearTransfers,
    getTransfersByDateRange,
    getTransfersSummary
  }
})
