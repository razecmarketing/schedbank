export class AccountNumber {
  constructor(value) {
    this.validateAccountNumber(value)
    this._value = value
  }

  validateAccountNumber(value) {
    if (!value || typeof value !== 'string') {
      throw new Error('Account number must be a string')
    }
    
    if (!/^\d{10}$/.test(value)) {
      throw new Error('Account number must be exactly 10 digits')
    }
  }

  get value() {
    return this._value
  }

  equals(other) {
    return other instanceof AccountNumber && this._value === other._value
  }

  toString() {
    return this._value
  }

  toFormattedString() {
    return this._value.replace(/(\d{4})(\d{3})(\d{3})/, '$1-$2-$3')
  }

  static of(value) {
    return new AccountNumber(value)
  }
}
