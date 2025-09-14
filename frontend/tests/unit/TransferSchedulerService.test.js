import { describe, it, expect, vi } from 'vitest'
import { TransferSchedulerService } from '../../src/application/usecases/TransferSchedulerService.js'
import { ValidationException, BusinessRuleException } from '../../src/domain/exceptions/DomainExceptions.js'

describe('TransferSchedulerService', () => {
  let mockRepository
  let service

  beforeEach(() => {
    mockRepository = {
      scheduleTransfer: vi.fn(),
      getAllTransfers: vi.fn()
    }
    service = new TransferSchedulerService(mockRepository)
  })

  describe('validateTransferRequest', () => {
    it('should validate correct transfer request', () => {
      const request = {
        sourceAccount: '1234567890',
        targetAccount: '0987654321',
        amount: 1000,
        transferDate: '2025-12-31'
      }

      expect(() => service.validateTransferRequest(request)).not.toThrow()
    })

    it('should throw ValidationException for missing source account', () => {
      const request = {
        targetAccount: '0987654321',
        amount: 1000,
        transferDate: '2025-12-31'
      }

      expect(() => service.validateTransferRequest(request))
        .toThrow(ValidationException)
    })

    it('should throw ValidationException for same source and target accounts', () => {
      const request = {
        sourceAccount: '1234567890',
        targetAccount: '1234567890',
        amount: 1000,
        transferDate: '2025-12-31'
      }

      expect(() => service.validateTransferRequest(request))
        .toThrow(ValidationException)
    })

    it('should throw ValidationException for invalid account number format', () => {
      const request = {
        sourceAccount: '123',
        targetAccount: '0987654321',
        amount: 1000,
        transferDate: '2025-12-31'
      }

      expect(() => service.validateTransferRequest(request))
        .toThrow(ValidationException)
    })

    it('should throw ValidationException for negative amount', () => {
      const request = {
        sourceAccount: '1234567890',
        targetAccount: '0987654321',
        amount: -100,
        transferDate: '2025-12-31'
      }

      expect(() => service.validateTransferRequest(request))
        .toThrow(ValidationException)
    })
  })

  describe('calculateDaysUntilTransfer', () => {
    it('should calculate days correctly', () => {
      const tomorrow = new Date()
      tomorrow.setDate(tomorrow.getDate() + 1)
      const tomorrowString = tomorrow.toISOString().split('T')[0]
      
      const days = service.calculateDaysUntilTransfer(tomorrowString)
      expect(days).toBe(1)
    })
  })

  describe('getFeeRangeDescription', () => {
    it('should return correct description for each range', () => {
      expect(service.getFeeRangeDescription(0)).toBe('Same day: R$ 3.00 + 2.5%')
      expect(service.getFeeRangeDescription(5)).toBe('1-10 days: R$ 12.00 fixed')
      expect(service.getFeeRangeDescription(15)).toBe('11-20 days: 8.2%')
      expect(service.getFeeRangeDescription(25)).toBe('21-30 days: 6.9%')
      expect(service.getFeeRangeDescription(35)).toBe('31-40 days: 4.7%')
      expect(service.getFeeRangeDescription(45)).toBe('41-50 days: 1.7%')
      expect(service.getFeeRangeDescription(60)).toBe('Not applicable (>50 days)')
    })
  })
})
