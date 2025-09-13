import { describe, it, expect } from 'vitest'
import { FormValidationService } from '../../../src/application/services/FormValidationService.js'

describe('FormValidationService', () => {
  describe('validateTransferForm', () => {
    it('should validate a correct form', () => {
      const validForm = {
        sourceAccount: '1234567890',
        targetAccount: '0987654321',
        amount: 100.50,
        transferDate: '2025-12-31'
      }

      const result = FormValidationService.validateTransferForm(validForm)
      expect(result.isValid).toBe(true)
      expect(result.errors).toHaveLength(0)
    })

    it('should reject invalid source account', () => {
      const invalidForm = {
        sourceAccount: '123',
        targetAccount: '0987654321',
        amount: 100.50,
        transferDate: '2025-12-31'
      }

      const result = FormValidationService.validateTransferForm(invalidForm)
      expect(result.isValid).toBe(false)
      expect(result.errors).toContain('Conta de origem deve ter exatamente 10 dígitos')
    })

    it('should reject invalid target account', () => {
      const invalidForm = {
        sourceAccount: '1234567890',
        targetAccount: '098765432199',
        amount: 100.50,
        transferDate: '2025-12-31'
      }

      const result = FormValidationService.validateTransferForm(invalidForm)
      expect(result.isValid).toBe(false)
      expect(result.errors).toContain('Conta de destino deve ter exatamente 10 dígitos')
    })

    it('should reject same source and target accounts', () => {
      const invalidForm = {
        sourceAccount: '1234567890',
        targetAccount: '1234567890',
        amount: 100.50,
        transferDate: '2025-12-31'
      }

      const result = FormValidationService.validateTransferForm(invalidForm)
      expect(result.isValid).toBe(false)
      expect(result.errors).toContain('Conta de origem e destino devem ser diferentes')
    })

    it('should reject zero or negative amounts', () => {
      const invalidForm = {
        sourceAccount: '1234567890',
        targetAccount: '0987654321',
        amount: 0,
        transferDate: '2025-12-31'
      }

      const result = FormValidationService.validateTransferForm(invalidForm)
      expect(result.isValid).toBe(false)
      expect(result.errors).toContain('Valor da transferência deve ser maior que zero')
    })

    it('should reject missing transfer date', () => {
      const invalidForm = {
        sourceAccount: '1234567890',
        targetAccount: '0987654321',
        amount: 100.50,
        transferDate: null
      }

      const result = FormValidationService.validateTransferForm(invalidForm)
      expect(result.isValid).toBe(false)
      expect(result.errors).toContain('Data da transferência é obrigatória')
    })

    it('should reject past transfer dates', () => {
      const invalidForm = {
        sourceAccount: '1234567890',
        targetAccount: '0987654321',
        amount: 100.50,
        transferDate: '2020-01-01'
      }

      const result = FormValidationService.validateTransferForm(invalidForm)
      expect(result.isValid).toBe(false)
      expect(result.errors).toContain('Data da transferência não pode ser no passado')
    })
  })

  describe('validateAccountNumber', () => {
    it('should validate correct account number', () => {
      expect(FormValidationService.validateAccountNumber('1234567890')).toBe(true)
    })

    it('should reject short account number', () => {
      expect(FormValidationService.validateAccountNumber('123456789')).toBe(false)
    })

    it('should reject long account number', () => {
      expect(FormValidationService.validateAccountNumber('12345678901')).toBe(false)
    })

    it('should reject non-numeric account number', () => {
      expect(FormValidationService.validateAccountNumber('123456789A')).toBe(false)
    })
  })
})