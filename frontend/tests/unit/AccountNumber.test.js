import { describe, it, expect } from 'vitest'
import { AccountNumber } from '../../src/domain/valueobjects/AccountNumber.js'

describe('AccountNumber Value Object', () => {
  it('should create account number with valid 10 digits', () => {
    const account = new AccountNumber('1234567890')
    expect(account.value).toBe('1234567890')
  })

  it('should format account number correctly', () => {
    const account = new AccountNumber('1234567890')
    expect(account.toFormattedString()).toBe('1234-567-890')
  })

  it('should throw error for invalid length', () => {
    expect(() => new AccountNumber('123')).toThrow('Account number must be exactly 10 digits')
    expect(() => new AccountNumber('12345678901')).toThrow('Account number must be exactly 10 digits')
  })

  it('should throw error for non-numeric characters', () => {
    expect(() => new AccountNumber('123456789a')).toThrow('Account number must be exactly 10 digits')
  })

  it('should throw error for null or undefined', () => {
    expect(() => new AccountNumber(null)).toThrow('Account number must be a string')
    expect(() => new AccountNumber(undefined)).toThrow('Account number must be a string')
  })

  it('should check equality correctly', () => {
    const account1 = new AccountNumber('1234567890')
    const account2 = new AccountNumber('1234567890')
    const account3 = new AccountNumber('0987654321')
    
    expect(account1.equals(account2)).toBe(true)
    expect(account1.equals(account3)).toBe(false)
  })

  it('should create account using static factory method', () => {
    const account = AccountNumber.of('1234567890')
    expect(account.value).toBe('1234567890')
  })
})
