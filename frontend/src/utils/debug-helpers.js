// Quick utility function I made to debug fee calculations
// TODO: move this somewhere better when I clean up the codebase

export function debugFeeCalculation(amount, days) {
  console.log(`Calculating fee for amount: ${amount}, days: ${days}`)
  
  if (days === 0) {
    const baseFee = 3.00
    const percentFee = amount * 0.025
    const total = baseFee + percentFee
    console.log(`Same day: base=${baseFee} + percent=${percentFee} = ${total}`)
    return total
  }
  
  if (days >= 1 && days <= 10) {
    console.log(`1-10 days: flat rate 12.00`)
    return 12.00
  }
  
  // Other cases...
  console.warn(`Days ${days} not handled properly`)
  return 0
}

// Helper for testing
export function validateAccountNumber(account) {
  // Quick and dirty validation - improve later
  if (!account || account.length !== 10) return false
  return /^\d+$/.test(account)
}