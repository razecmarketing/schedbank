import { Transfer } from '@domain/entities/Transfer.js'
import { ValidationException, BusinessRuleException } from '@domain/exceptions/DomainExceptions.js'

export class TransferSchedulerService {
  constructor(transferRepository) {
    this._transferRepository = transferRepository
  }

  async scheduleTransfer(transferRequest) {
    this.validateTransferRequest(transferRequest)
    
    try {
      const response = await this._transferRepository.scheduleTransfer(transferRequest)
      return Transfer.fromApiResponse(response)
    } catch (error) {
      if (error.response?.status === 400) {
        throw new BusinessRuleException(
          error.response.data.message || 'Invalid transfer request'
        )
      }
      throw error
    }
  }

  async listScheduledTransfers() {
    try {
      const transfers = await this._transferRepository.getAllTransfers()
      return transfers.map(transfer => Transfer.fromApiResponse(transfer))
    } catch (error) {
      throw new BusinessRuleException('Failed to retrieve transfers: ' + error.message)
    }
  }

  async updateTransfer(transferRequest) {
    this.validateTransferRequest(transferRequest)
    
    if (!transferRequest.id) {
      throw new ValidationException('Transfer ID is required for update', 'id')
    }
    
    try {
      const response = await this._transferRepository.updateTransfer(transferRequest)
      return Transfer.fromApiResponse(response)
    } catch (error) {
      if (error.response?.status === 400) {
        throw new BusinessRuleException(
          error.response.data.message || 'Invalid transfer update request'
        )
      }
      if (error.response?.status === 404) {
        throw new BusinessRuleException('Transfer not found')
      }
      throw error
    }
  }

  async deleteTransfer(transferId) {
    if (!transferId) {
      throw new ValidationException('Transfer ID is required for deletion', 'id')
    }
    
    try {
      await this._transferRepository.deleteTransfer(transferId)
    } catch (error) {
      if (error.response?.status === 404) {
        throw new BusinessRuleException('Transfer not found')
      }
      throw error
    }
  }

  async clearAllTransfers() {
    try {
      await this._transferRepository.clearAllTransfers()
    } catch (error) {
      throw new BusinessRuleException('Failed to clear transfers: ' + error.message)
    }
  }

  validateTransferRequest(request) {
    if (!request.sourceAccount) {
      throw new ValidationException('Source account is required', 'sourceAccount')
    }

    if (!request.targetAccount) {
      throw new ValidationException('Target account is required', 'targetAccount')
    }

    if (request.sourceAccount === request.targetAccount) {
      throw new ValidationException('Source and target accounts must be different', 'targetAccount')
    }

    if (!request.amount || request.amount <= 0) {
      throw new ValidationException('Amount must be positive', 'amount')
    }

    if (!request.transferDate) {
      throw new ValidationException('Transfer date is required', 'transferDate')
    }

    const transferDate = new Date(request.transferDate)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    
    if (transferDate < today) {
      throw new ValidationException('Transfer date cannot be in the past', 'transferDate')
    }

    this.validateAccountNumber(request.sourceAccount, 'sourceAccount')
    this.validateAccountNumber(request.targetAccount, 'targetAccount')
  }

  validateAccountNumber(accountNumber, fieldName) {
    if (!/^\d{10}$/.test(accountNumber)) {
      throw new ValidationException(
        'Account number must be exactly 10 digits', 
        fieldName
      )
    }
  }

  calculateDaysUntilTransfer(transferDate) {
    const today = new Date()
    const target = new Date(transferDate)
    const diffTime = target.getTime() - today.getTime()
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  }

  getFeeRangeDescription(days) {
    if (days === 0) return 'Same day: R$ 3.00 + 2.5%'
    if (days >= 1 && days <= 10) return '1-10 days: R$ 12.00 fixed'
    if (days >= 11 && days <= 20) return '11-20 days: 8.2%'
    if (days >= 21 && days <= 30) return '21-30 days: 6.9%'
    if (days >= 31 && days <= 40) return '31-40 days: 4.7%'
    if (days >= 41 && days <= 50) return '41-50 days: 1.7%'
    return 'Not applicable (>50 days)'
  }
}
