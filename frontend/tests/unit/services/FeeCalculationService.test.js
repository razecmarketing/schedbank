import { describe, it, expect, beforeEach } from 'vitest'
import { FeeCalculationService } from '../../../src/application/services/FeeCalculationService.js'

describe('FeeCalculationService', () => {
  describe('calculateDaysBetweenDates', () => {
    it('should calculate zero days for same date', () => {
      const today = new Date('2025-09-13')
      const result = FeeCalculationService.calculateDaysBetweenDates(today, today)
      expect(result).toBe(0)
    })

    it('should calculate positive days for future date', () => {
      const today = new Date('2025-09-13')
      const future = new Date('2025-09-23')
      const result = FeeCalculationService.calculateDaysBetweenDates(today, future)
      expect(result).toBe(10)
    })

    it('should calculate negative days for past date', () => {
      const today = new Date('2025-09-13')
      const past = new Date('2025-09-03')
      const result = FeeCalculationService.calculateDaysBetweenDates(today, past)
      expect(result).toBe(-10)
    })
  })

  describe('calculateTransferFee', () => {
    beforeEach(() => {
      // Mock current date to ensure consistent test results
      vi.setSystemTime(new Date('2025-09-13'))
    })

    it('should calculate same day fee correctly', () => {
      const result = FeeCalculationService.calculateTransferFee(100, '2025-09-13')
      expect(result.fee).toBe(5.50) // 3 + (100 * 0.025)
      expect(result.error).toBeUndefined()
    })

    it('should calculate 1-10 days fee correctly', () => {
      const result = FeeCalculationService.calculateTransferFee(100, '2025-09-23')
      expect(result.fee).toBe(12.00)
      expect(result.error).toBeUndefined()
    })

    it('should calculate 11-20 days fee correctly', () => {
      const result = FeeCalculationService.calculateTransferFee(1000, '2025-09-28')
      expect(result.fee).toBe(82.00) // 1000 * 0.082
      expect(result.error).toBeUndefined()
    })

    it('should calculate 21-30 days fee correctly', () => {
      const result = FeeCalculationService.calculateTransferFee(1000, '2025-10-05')
      expect(result.fee).toBe(69.00) // 1000 * 0.069
      expect(result.error).toBeUndefined()
    })

    it('should calculate 31-40 days fee correctly', () => {
      const result = FeeCalculationService.calculateTransferFee(1000, '2025-10-15')
      expect(result.fee).toBe(47.00) // 1000 * 0.047
      expect(result.error).toBeUndefined()
    })

    it('should calculate 41-50 days fee correctly', () => {
      const result = FeeCalculationService.calculateTransferFee(1000, '2025-10-25')
      expect(result.fee).toBe(17.00) // 1000 * 0.017
      expect(result.error).toBeUndefined()
    })

    it('should return error for past date', () => {
      const result = FeeCalculationService.calculateTransferFee(100, '2025-09-01')
      expect(result.error).toBe('Data de transferência no passado')
      expect(result.fee).toBeUndefined()
    })

    it('should return error for dates beyond 50 days', () => {
      const result = FeeCalculationService.calculateTransferFee(100, '2025-12-01')
      expect(result.error).toBe('Nenhuma taxa aplicável para essa data')
      expect(result.fee).toBeUndefined()
    })
  })

  describe('formatCurrency', () => {
    it('should format valid currency correctly', () => {
      expect(FeeCalculationService.formatCurrency(123.45)).toBe('R$ 123.45')
    })

    it('should format zero correctly', () => {
      expect(FeeCalculationService.formatCurrency(0)).toBe('R$ 0.00')
    })

    it('should handle null values', () => {
      expect(FeeCalculationService.formatCurrency(null)).toBe('R$ 0,00')
    })

    it('should handle undefined values', () => {
      expect(FeeCalculationService.formatCurrency(undefined)).toBe('R$ 0,00')
    })
  })
})