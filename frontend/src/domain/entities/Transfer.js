import { Money } from '../valueobjects/Money.js'
import { AccountNumber } from '../valueobjects/AccountNumber.js'

export class Transfer {
  constructor(data) {
    this.validateTransferData(data)
    
    this._id = data.id
    this._sourceAccount = AccountNumber.of(data.sourceAccount)
    this._targetAccount = AccountNumber.of(data.targetAccount)
    this._amount = Money.of(data.amount)
    this._fee = Money.of(data.fee)
    this._scheduleDate = new Date(data.scheduleDate)
    this._transferDate = new Date(data.transferDate)
  }

  validateTransferData(data) {
    if (!data) {
      throw new Error('Transfer data is required')
    }

    if (!data.sourceAccount || !data.targetAccount) {
      throw new Error('Source and target accounts are required')
    }

    if (data.sourceAccount === data.targetAccount) {
      throw new Error('Source and target accounts must be different')
    }

    if (!data.amount || !data.fee) {
      throw new Error('Amount and fee are required')
    }

    if (!data.transferDate) {
      throw new Error('Transfer date is required')
    }
  }

  get id() { return this._id }
  get sourceAccount() { return this._sourceAccount }
  get targetAccount() { return this._targetAccount }
  get amount() { return this._amount }
  get fee() { return this._fee }
  get scheduleDate() { return this._scheduleDate }
  get transferDate() { return this._transferDate }

  getDaysUntilTransfer() {
    const today = new Date()
    const diffTime = this._transferDate.getTime() - today.getTime()
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  }

  getTotalAmount() {
    return Money.of(this._amount.amount + this._fee.amount)
  }

  toFormattedSummary() {
    return {
      id: this._id,
      from: this._sourceAccount.toFormattedString(),
      to: this._targetAccount.toFormattedString(),
      amount: this._amount.toFormattedString(),
      fee: this._fee.toFormattedString(),
      total: this.getTotalAmount().toFormattedString(),
      scheduleDate: this._scheduleDate.toLocaleDateString('pt-BR'),
      transferDate: this._transferDate.toLocaleDateString('pt-BR'),
      daysUntilTransfer: this.getDaysUntilTransfer()
    }
  }

  static fromApiResponse(apiData) {
    return new Transfer(apiData)
  }
}
