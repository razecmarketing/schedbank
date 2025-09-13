export class Money {
  constructor(amount) {
    this.validateAmount(amount)
    this._amount = Number(amount)
  }

  validateAmount(amount) {
    if (amount == null || amount === undefined) {
      throw new Error('Amount cannot be null or undefined')
    }
    
    const numericAmount = Number(amount)
    if (isNaN(numericAmount)) {
      throw new Error('Amount must be a valid number')
    }
    
    if (numericAmount <= 0) {
      throw new Error('Amount must be positive')
    }
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
