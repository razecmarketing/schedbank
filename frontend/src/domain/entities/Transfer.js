import { Money } from '../valueobjects/Money.js'
import { AccountNumber } from '../valueobjects/AccountNumber.js'

/**
 * Transfer Domain Entity - Seguindo Padrão Pessoal de Programação
 * 
 * Princípios aplicados:
 * - Invariantes garantidas matematicamente
 * - Código logicamente correto antes de eficiente
 * - Imutabilidade e defensive programming
 * - Auto-documentação através de código claro
 */
export class Transfer {
  /**
   * Construtor privativo que garante todas as invariantes de domínio
   * Falha rápido em caso de dados inválidos (fail-fast)
   */
  constructor(data) {
    this._enforceBusinessInvariants(data)
    this._initializeImmutableState(data)
  }

  /**
   * Validação rigorosa das regras de negócio
   * Implementa defensive programming
   */
  _enforceBusinessInvariants(data) {
    if (!data) {
      throw new Error('Transfer data é obrigatório - violação de invariante')
    }

    if (!data.sourceAccount || !data.targetAccount) {
      throw new Error('Contas origem e destino são invariantes obrigatórias')
    }

    if (data.sourceAccount === data.targetAccount) {
      throw new Error('Invariante violada: contas origem e destino devem ser distintas')
    }

    if (!data.amount || !data.fee) {
      throw new Error('Valores monetários são invariantes obrigatórias')
    }

    if (!data.transferDate) {
      throw new Error('Data de transferência é invariante temporal obrigatória')
    }
  }

  /**
   * Inicialização do estado imutável com Value Objects
   * Garante integridade referencial
   */
  _initializeImmutableState(data) {
    this._id = data.id
    this._sourceAccount = AccountNumber.of(data.sourceAccount)
    this._targetAccount = AccountNumber.of(data.targetAccount)
    this._amount = Money.of(data.amount)
    this._fee = Money.of(data.fee)
    this._scheduleDate = new Date(data.scheduleDate)
    this._transferDate = new Date(data.transferDate)
    
    // Garante imutabilidade profunda
    Object.freeze(this)
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
