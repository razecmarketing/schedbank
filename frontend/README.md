# Frontend README

## Bank Transfer Scheduler - Vue.js Frontend

Modular architecture implementation of a financial transfer scheduling system using Vue.js 3.

### Architecture Overview

This frontend follows layered architecture principles with strict layer separation:

- **Domain Layer**: Pure business logic, value objects, entities
- **Application Layer**: Use cases, application services, state management
- **Infrastructure Layer**: HTTP clients, repositories, external adapters
- **Presentation Layer**: Vue.js components, routing, UI logic

### Technology Stack

- **Vue.js 3.4+**: Progressive JavaScript framework
- **Composition API**: Modern Vue development approach
- **Pinia 2.1+**: Type-safe state management
- **Vue Router 4.2+**: Client-side routing
- **Axios 1.6+**: HTTP client for API communication
- **Vite 5.0+**: Fast build tool and dev server
- **Vitest 1.0+**: Unit testing framework

### Prerequisites

- Node.js 18+ 
- npm 8+
- Backend API running on http://localhost:8080

### Installation

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Application will be available at http://localhost:5173
```

### Available Scripts

```bash
# Development
npm run dev          # Start dev server with HMR
npm run build        # Production build
npm run preview      # Preview production build

# Testing
npm run test         # Run unit tests
npm run test:ui      # Run tests with UI
npm run test:coverage # Run tests with coverage

# Code Quality
npm run lint         # ESLint code analysis
npm run type-check   # TypeScript type checking
```

### Project Structure

```
frontend/
├── docs/                 # Architecture documentation
├── src/
│   ├── domain/          # Business rules and entities
│   │   ├── entities/    # Core business objects
│   │   ├── valueobjects/# Immutable domain concepts  
│   │   ├── exceptions/  # Domain-specific errors
│   │   └── ports/       # Dependency abstractions
│   ├── application/     # Application business rules
│   │   ├── usecases/    # Business use cases
│   │   └── stores/      # Application state
│   ├── infrastructure/ # Frameworks and drivers
│   │   ├── http/        # HTTP client adapters
│   │   ├── repositories/# Data access implementations
│   │   └── router/      # Routing configuration
│   ├── presentation/    # Interface adapters
│   │   └── components/  # Vue.js UI components
│   ├── App.vue         # Root component
│   └── main.js         # Application entry point
├── tests/              # Test files
│   ├── unit/           # Unit tests
│   ├── integration/    # Integration tests
│   └── e2e/            # End-to-end tests
├── index.html          # HTML entry point
├── vite.config.js      # Vite configuration
└── package.json        # Dependencies and scripts
```

### Features

#### Transfer Scheduling
- Create new financial transfers
- Real-time fee calculation preview
- Form validation with business rules
- Account number validation (10 digits)
- Transfer date constraints

#### Transfer Management
- View all scheduled transfers
- Transfer status tracking
- Summary statistics
- Responsive design for mobile/desktop

#### Architecture Features
- Layered architecture implementation
- Development principles applied
- Dependency injection
- Domain-driven design
- Exception handling
- Unit testing coverage

### API Integration

The frontend consumes REST APIs from the Spring Boot backend:

```javascript
// Base URL
const API_BASE = 'http://localhost:8080/api'

// Endpoints
POST /transfers    # Schedule new transfer
GET  /transfers    # List all transfers
```

### Development Guidelines

#### Code Standards
- No emojis in code (clean code principle)
- Self-documenting code with meaningful names
- Single Responsibility Principle
- Dependency Inversion via dependency injection
- Test-driven development

#### Component Design
- Composition API for reactive logic
- Props/events for parent-child communication
- Provide/inject for deep dependency passing
- Scoped styles for component isolation

#### State Management
- Pinia stores in application layer
- Business logic in use cases, not stores
- Reactive state with computed properties
- Error handling at appropriate boundaries

### Testing Strategy

#### Unit Tests
```bash
# Run all unit tests
npm run test

# Watch mode for development
npm run test -- --watch

# Coverage report
npm run test:coverage
```

#### Test Philosophy
- Test behavior, not implementation
- Mock external dependencies
- Test domain logic thoroughly
- Integration tests for critical paths

### Deployment

#### Development Build
```bash
npm run build
```

#### Production Considerations
- Environment-specific API URLs
- Build optimization for performance
- CDN deployment for static assets
- HTTPS enforcement
- Security headers configuration

### Error Handling

The application implements comprehensive error handling:

- **Domain Errors**: Business rule violations
- **Validation Errors**: Input validation failures  
- **Infrastructure Errors**: Network/API failures
- **User Feedback**: Clear error messages in UI

### Performance Optimization

- **Code Splitting**: Route-based lazy loading
- **Tree Shaking**: Unused code elimination
- **Bundle Analysis**: Size optimization
- **Caching**: HTTP response caching
- **Reactive Updates**: Efficient DOM updates

### Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

### Contributing

Follow the established patterns:
1. 1. Layered architecture separation
2. SOLID principles implementation
3. Comprehensive unit testing
4. Self-documenting code
5. No architectural compromises for convenience

### License

MIT License - See LICENSE file for details
