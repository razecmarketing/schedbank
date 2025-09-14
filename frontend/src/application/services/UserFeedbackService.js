export class UserFeedbackService {
  static showSuccessMessage(message) {
    alert(`Sucesso: ${message}`)
  }

  static showErrorMessage(error) {
    const errorMessage = this.extractErrorMessage(error)
    alert(`Erro: ${errorMessage}`)
  }

  static showValidationErrors(errors) {
    const errorList = errors.join('\n- ')
    alert(`Erros de validação:\n- ${errorList}`)
  }

  static confirmAction(message) {
    return confirm(message)
  }

  static extractErrorMessage(error) {
    if (error.response?.data?.message) {
      return error.response.data.message
    }
    
    if (error.message) {
      return error.message
    }
    
    return 'Erro inesperado'
  }
}