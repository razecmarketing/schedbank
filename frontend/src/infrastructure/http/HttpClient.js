import axios from 'axios'
import { DomainException } from '../../domain/exceptions/DomainExceptions.js'

export class InfrastructureException extends DomainException {
  constructor(message, originalError = null) {
    super(message, 'INFRASTRUCTURE_ERROR')
    this.originalError = originalError
  }
}

export class HttpClient {
  constructor(baseURL = 'http://localhost:8080/api') {
    this._client = axios.create({
      baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })

    this.setupInterceptors()
  }

  setupInterceptors() {
    this._client.interceptors.request.use(
      config => {
        console.log(`HTTP Request: ${config.method?.toUpperCase()} ${config.url}`)
        return config
      },
      error => {
        console.error('HTTP Request Error:', error)
        return Promise.reject(error)
      }
    )

    this._client.interceptors.response.use(
      response => {
        console.log(`HTTP Response: ${response.status} ${response.config.url}`)
        return response
      },
      error => {
        console.error('HTTP Response Error:', error.response?.status, error.message)
        
        if (error.code === 'ECONNREFUSED') {
          throw new InfrastructureException(
            'Unable to connect to server. Please ensure the backend is running.',
            error
          )
        }

        if (error.response?.status >= 500) {
          throw new InfrastructureException(
            'Server error occurred. Please try again later.',
            error
          )
        }

        return Promise.reject(error)
      }
    )
  }

  async get(url, config = {}) {
    try {
      const response = await this._client.get(url, config)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async post(url, data, config = {}) {
    try {
      const response = await this._client.post(url, data, config)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async put(url, data, config = {}) {
    try {
      const response = await this._client.put(url, data, config)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async delete(url, config = {}) {
    try {
      const response = await this._client.delete(url, config)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  handleError(error) {
    if (error instanceof InfrastructureException) {
      return error
    }

    // Use meaningful names and handle specific cases
    const errorMessage = this.extractErrorMessage(error)
    const statusCode = error.response?.status

    if (statusCode === 404) {
      return new InfrastructureException('Recurso não encontrado', error)
    }

    if (statusCode === 400) {
      return new InfrastructureException(`Dados inválidos: ${errorMessage}`, error)
    }

    if (statusCode >= 500) {
      return new InfrastructureException('Erro interno do servidor. Tente novamente mais tarde.', error)
    }

    return new InfrastructureException(errorMessage || 'Erro inesperado', error)
  }

  // Extract Method for better readability
  extractErrorMessage(error) {
    return error.response?.data?.message || 
           error.response?.data?.error || 
           error.message || 
           'Erro de comunicação com o servidor'
  }
}
