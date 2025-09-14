/**
 * Money Value Object - Seguindo Padrão Pessoal de Programação
 * 
 * Princípios aplicados:
 * - Precisão matemática rigorosa (2 casas decimais)
 * - Invariantes garantidas na construção
 * - Imutabilidade total
 * - Operações matematicamente corretas
 */
export class Money {
  static DECIMAL_PLACES = 2
  static ROUNDING_PRECISION = 100 // Para 2 casas decimais
  
  /**
   * Factory method que garante criação consistente
   * Implementa fail-fast validation
   */
  static of(value) {
    return new Money(value)
  }

  /**
   * Construtor privativo com validação matemática rigorosa
   */
  constructor(amount) {
    this._enforceMonetaryInvariants(amount)
    this._amount = this._normalizeToPrecision(amount)
    Object.freeze(this) // Garantia de imutabilidade
  }

  /**
   * Validação rigorosa de invariantes monetárias
   * Matemática precisa: valor > 0, numérico válido
   */
  _enforceMonetaryInvariants(amount) {
    if (amount == null || amount === undefined) {
      throw new Error('Invariante violada: valor monetário não pode ser nulo')
    }
    
    const numericAmount = Number(amount)
    if (isNaN(numericAmount)) {
      throw new Error('Invariante violada: valor deve ser numericamente válido')
    }
    
    if (numericAmount <= 0) {
      throw new Error('Invariante violada: valor monetário deve ser positivo')
    }

    if (!Number.isFinite(numericAmount)) {
      throw new Error('Invariante violada: valor deve ser finito')
    }
  }

  /**
   * Normalização para precisão monetária (2 casas decimais)
   * Evita problemas de ponto flutuante
   */
  _normalizeToPrecision(amount) {
    return Math.round(Number(amount) * Money.ROUNDING_PRECISION) / Money.ROUNDING_PRECISION
  }

  get amount() {
    return this._amount
  }

  equals(other) {
    return other instanceof Money && this._amount === other._amount
  }

  toString() {
    return this._amount.toFixed(2)
  }

  toFormattedString() {
    return `R$ ${this._amount.toLocaleString('pt-BR', { 
      minimumFractionDigits: 2, 
      maximumFractionDigits: 2 
    })}`
  }

  static of(amount) {
    return new Money(amount)
  }
}
