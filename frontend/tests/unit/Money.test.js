import { describe, it, expect, beforeEach } from 'vitest'
import { Money } from '../../src/domain/valueobjects/Money.js'

describe('Money Value Object', () => {
  it('should create money with valid amount', () => {
    const money = new Money(100.50)
    expect(money.amount).toBe(100.50)
  })

  it('should format money to Brazilian currency', () => {
    const money = new Money(1000.50)
    expect(money.toFormattedString()).toBe('R$ 1.000,50')
  })

  it('should throw error for negative amount', () => {
    expect(() => new Money(-100)).toThrow('Amount must be positive')
  })

  it('should throw error for zero amount', () => {
    expect(() => new Money(0)).toThrow('Amount must be positive')
  })

  it('should throw error for null amount', () => {
    expect(() => new Money(null)).toThrow('Amount cannot be null or undefined')
  })

  it('should throw error for invalid amount', () => {
    expect(() => new Money('invalid')).toThrow('Amount must be a valid number')
  })

  it('should check equality correctly', () => {
    const money1 = new Money(100)
    const money2 = new Money(100)
    const money3 = new Money(200)
    
    expect(money1.equals(money2)).toBe(true)
    expect(money1.equals(money3)).toBe(false)
  })

  it('should create money using static factory method', () => {
    const money = Money.of(500)
    expect(money.amount).toBe(500)
  })
})
