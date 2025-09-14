/**
 * FormValidationService - Seguindo Padrão Pessoal de Programação
 * 
 * Princípios aplicados:
 * - Responsabilidade única: apenas validação de formulários
 * - Modularidade extrema: cada validação isolada
 * - Código auto-documentado através de nomes claros
 * - Fail-fast: retorna erro imediatamente quando encontrado
 * - Precisão matemática nas validações monetárias
 */
export class FormValidationService {
  /**
   * Validação completa do formulário de transferência
   * Implementa all-or-nothing validation com mapeamento específico por campo
   */
  static validateTransferForm(formData) {
    const errors = {}
    
    this._validateSourceAccount(formData.sourceAccount, errors)
    this._validateTargetAccount(formData.targetAccount, formData.sourceAccount, errors)
    this._validateAmount(formData.amount, errors)
    this._validateTransferDate(formData.transferDate, errors)
    
    return {
      isValid: Object.keys(errors).length === 0,
      errors,
      errorCount: Object.keys(errors).length
    }
  }

  /**
   * Validação rigorosa da conta de origem
   * Aplica invariantes de formato de conta bancária brasileiro
   */
  static _validateSourceAccount(sourceAccount, errors) {
    if (!sourceAccount) {
      errors.sourceAccount = 'Conta de origem é invariante obrigatória'
      return
    }
    
    if (!this._isValidAccountFormat(sourceAccount)) {
      errors.sourceAccount = 'Formato inválido: exatamente 10 dígitos numéricos'
      return
    }
  }

  /**
   * Validação da conta de destino com verificação de auto-transferência
   * Implementa regra de negócio: contas devem ser matematicamente distintas
   */
  static _validateTargetAccount(targetAccount, sourceAccount, errors) {
    if (!targetAccount) {
      errors.targetAccount = 'Conta de destino é invariante obrigatória'
      return
    }
    
    if (!this._isValidAccountFormat(targetAccount)) {
      errors.targetAccount = 'Formato inválido: exatamente 10 dígitos numéricos'
      return
    }
    
    // Invariante de negócio: não permitir auto-transferência
    if (targetAccount === sourceAccount) {
      errors.targetAccount = 'Invariante violada: contas origem e destino devem ser distintas'
      return
    }
  }

  /**
   * Validação monetária com precisão matemática rigorosa
   * Garante valor positivo, finito e com precisão adequada
   */
  static _validateAmount(amount, errors) {
    if (!amount && amount !== 0) {
      errors.amount = 'Valor monetário é invariante obrigatória'
      return
    }
    
    const numericAmount = Number(amount)
    
    if (isNaN(numericAmount)) {
      errors.amount = 'Valor deve ser numericamente válido'
      return
    }
    
    if (!Number.isFinite(numericAmount)) {
      errors.amount = 'Valor deve ser finito'
      return
    }
    
    if (numericAmount <= 0) {
      errors.amount = 'Invariante violada: valor deve ser estritamente positivo'
      return
    }
    
    // Validação de precisão monetária (centavos)
    if (numericAmount < 0.01) {
      errors.amount = 'Valor mínimo: R$ 0,01 (precisão de centavos)'
      return
    }
    
    // Limite prático para evitar overflow
    if (numericAmount > 999999999.99) {
      errors.amount = 'Valor máximo excedido: R$ 999.999.999,99'
      return
    }
  }

  /**
   * Validação temporal rigorosa da data de transferência
   * Implementa invariante temporal: data >= hoje
   */
  static _validateTransferDate(transferDate, errors) {
    if (!transferDate) {
      errors.transferDate = 'Data de transferência é invariante temporal obrigatória'
      return
    }
    
    const selectedDate = new Date(transferDate)
    
    if (isNaN(selectedDate.getTime())) {
      errors.transferDate = 'Data deve estar em formato válido'
      return
    }
    
    const today = new Date()
    today.setHours(0, 0, 0, 0) // Normalização temporal para comparação de datas
    selectedDate.setHours(0, 0, 0, 0)
    
    if (selectedDate < today) {
      errors.transferDate = 'Invariante temporal violada: data deve ser hoje ou futura'
      return
    }
  }

  /**
   * Validação de formato de conta bancária brasileira
   * Invariante: exatamente 10 dígitos numéricos consecutivos
   */
  /**
   * Validação de formato de conta bancária brasileira
   * Invariante: exatamente 10 dígitos numéricos consecutivos
   */
  static _isValidAccountFormat(account) {
    const BRAZILIAN_ACCOUNT_PATTERN = /^\d{10}$/
    return BRAZILIAN_ACCOUNT_PATTERN.test(account)
  }
}