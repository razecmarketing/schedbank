import { TransferRepository } from '@domain/ports/TransferRepository.js'

export class ApiTransferRepository extends TransferRepository {
  constructor(httpClient) {
    super()
    this._httpClient = httpClient
  }

  async scheduleTransfer(transferRequest) {
    const payload = {
      sourceAccount: transferRequest.sourceAccount,
      targetAccount: transferRequest.targetAccount,
      amount: Number(transferRequest.amount),
      transferDate: transferRequest.transferDate
    }

    return await this._httpClient.post('/transfers', payload)
  }

  async getAllTransfers() {
    const transfers = await this._httpClient.get('/transfers')
    return Array.isArray(transfers) ? transfers : []
  }

  async getTransferById(id) {
    return await this._httpClient.get(`/transfers/${id}`)
  }

  async updateTransfer(transferRequest) {
    const payload = {
      sourceAccount: transferRequest.sourceAccount,
      targetAccount: transferRequest.targetAccount,
      amount: Number(transferRequest.amount),
      transferDate: transferRequest.transferDate
    }

    return await this._httpClient.put(`/transfers/${transferRequest.id}`, payload)
  }

  async deleteTransfer(transferId) {
    return await this._httpClient.delete(`/transfers/${transferId}`)
  }

  async clearAllTransfers() {
    return await this._httpClient.delete('/transfers')
  }
}
