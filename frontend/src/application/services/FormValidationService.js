export class FormValidationService {
  static validateTransferForm(form) {
    const errors = []

    if (!form.sourceAccount || form.sourceAccount.length !== 10) {
      errors.push('Conta de origem deve ter exatamente 10 dígitos')
    }

    if (!form.targetAccount || form.targetAccount.length !== 10) {
      errors.push('Conta de destino deve ter exatamente 10 dígitos')
    }

    if (form.sourceAccount === form.targetAccount) {
      errors.push('Conta de origem e destino devem ser diferentes')
    }

    if (!form.amount || form.amount <= 0) {
      errors.push('Valor da transferência deve ser maior que zero')
    }

    if (!form.transferDate) {
      errors.push('Data da transferência é obrigatória')
    }

    const transferDate = new Date(form.transferDate)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    transferDate.setHours(0, 0, 0, 0)

    if (transferDate < today) {
      errors.push('Data da transferência não pode ser no passado')
    }

    return {
      isValid: errors.length === 0,
      errors
    }
  }

  static validateAccountNumber(accountNumber) {
    return /^\d{10}$/.test(accountNumber)
  }
}