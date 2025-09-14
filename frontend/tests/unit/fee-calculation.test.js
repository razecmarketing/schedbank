import { describe, it, expect } from 'vitest'

// Função de cálculo de taxa (extraída da lógica do App.vue)
function daysBetween(today, then) {
  const t = new Date(today.getFullYear(), today.getMonth(), today.getDate())
  const d = new Date(then.getFullYear(), then.getMonth(), then.getDate())
  return Math.round((d - t) / (1000 * 60 * 60 * 24))
}

function computeFee(amount, transferDateStr) {
  const transferDate = new Date(transferDateStr)
  const diff = daysBetween(new Date(), transferDate)
  
  if (diff < 0) return { error: 'Data de transferência no passado' }
  if (diff === 0) return { fee: 3 + amount * 0.025 }
  if (diff >= 1 && diff <= 10) return { fee: 12.0 }
  if (diff >= 11 && diff <= 20) return { fee: amount * 0.082 }
  if (diff >= 21 && diff <= 30) return { fee: amount * 0.069 }
  if (diff >= 31 && diff <= 40) return { fee: amount * 0.047 }
  if (diff >= 41 && diff <= 50) return { fee: amount * 0.017 }
  
  return { error: 'Nenhuma taxa aplicável para essa data' }
}

describe('Fee Calculation', () => {
  const today = new Date('2025-09-12') // Data fixa para testes
  
  it('should calculate fee for same day transfer', () => {
    const result = computeFee(100, '2025-09-12')
    expect(result).toEqual({ fee: 3 + 100 * 0.025 }) // 3 + 2.5 = 5.5
  })
  
  it('should calculate fixed fee for 1-10 days', () => {
    const result = computeFee(100, '2025-09-15') // 3 dias
    expect(result).toEqual({ fee: 12.0 })
  })
  
  it('should calculate percentage fee for 11-20 days', () => {
    const result = computeFee(100, '2025-09-25') // 13 dias
    expect(result).toEqual({ fee: 100 * 0.082 }) // 8.2
  })
  
  it('should calculate percentage fee for 21-30 days', () => {
    const result = computeFee(100, '2025-10-05') // 23 dias
    expect(result).toEqual({ fee: 100 * 0.069 }) // 6.9
  })
  
  it('should calculate percentage fee for 31-40 days', () => {
    const result = computeFee(100, '2025-10-15') // 33 dias
    expect(result).toEqual({ fee: 100 * 0.047 }) // 4.7
  })
  
  it('should calculate percentage fee for 41-50 days', () => {
    const result = computeFee(100, '2025-10-25') // 43 dias
    expect(result).toEqual({ fee: 100 * 0.017 }) // 1.7
  })
  
  it('should return error for past dates', () => {
    const result = computeFee(100, '2025-09-10') // 2 dias no passado
    expect(result).toEqual({ error: 'Data de transferência no passado' })
  })
  
  it('should return error for dates beyond 50 days', () => {
    const result = computeFee(100, '2025-11-05') // 54 dias
    expect(result).toEqual({ error: 'Nenhuma taxa aplicável para essa data' })
  })
})