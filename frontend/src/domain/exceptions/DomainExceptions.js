export class DomainException extends Error {
  constructor(message, code = null) {
    super(message)
    this.name = 'DomainException'
    this.code = code
  }
}

export class ValidationException extends DomainException {
  constructor(message, field = null) {
    super(message, 'VALIDATION_ERROR')
    this.name = 'ValidationException'
    this.field = field
  }
}

export class BusinessRuleException extends DomainException {
  constructor(message) {
    super(message, 'BUSINESS_RULE_VIOLATION')
    this.name = 'BusinessRuleException'
  }
}

export class InfrastructureException extends Error {
  constructor(message, originalError = null) {
    super(message)
    this.name = 'InfrastructureException'
    this.originalError = originalError
  }
}
