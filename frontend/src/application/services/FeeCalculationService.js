export class FeeCalculationService {
  static calculateDaysBetweenDates(startDate, endDate) {
    const start = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate())
    const end = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate())
    return Math.round((end - start) / (1000 * 60 * 60 * 24))
  }

  static calculateTransferFee(amount, transferDateStr) {
    const transferDate = new Date(transferDateStr)
    const daysDifference = this.calculateDaysBetweenDates(new Date(), transferDate)
    
    if (daysDifference < 0) {
      return { error: 'Data de transferência no passado' }
    }
    
    const feeRules = [
      { min: 0, max: 0, fixedFee: 3.00, percentageFee: 0.025 },
      { min: 1, max: 10, fixedFee: 12.00, percentageFee: 0.0 },
      { min: 11, max: 20, fixedFee: 0.00, percentageFee: 0.082 },
      { min: 21, max: 30, fixedFee: 0.00, percentageFee: 0.069 },
      { min: 31, max: 40, fixedFee: 0.00, percentageFee: 0.047 },
      { min: 41, max: 50, fixedFee: 0.00, percentageFee: 0.017 }
    ]

    const applicableRule = feeRules.find(rule => 
      daysDifference >= rule.min && daysDifference <= rule.max
    )

    if (!applicableRule) {
      return { error: 'Nenhuma taxa aplicável para essa data' }
    }

    const fee = applicableRule.fixedFee + (amount * applicableRule.percentageFee)
    return { fee }
  }

  static formatCurrency(value) {
    return value ? `R$ ${value.toFixed(2)}` : 'R$ 0,00'
  }
}