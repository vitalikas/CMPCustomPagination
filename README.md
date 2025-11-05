# CMP Custom Pagination

A **Kotlin Multiplatform (KMP)** project demonstrating custom pagination with **MVI + Redux
architecture**, state machines, and clean architecture principles using Compose Multiplatform.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-1.8.2-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20Desktop-green.svg)](https://kotlinlang.org/docs/multiplatform.html)

## Project Overview

This project showcases a production-ready e-commerce application with:

- **Custom pagination** implementation for product listings
- **MVI (Model-View-Intent) + Redux** architecture pattern
- **State machine** for managing UI state transitions
- **Shopping basket** functionality with real-time calculations
- **Multi-platform support** (Android, iOS, Desktop)

## Key Features

### Architecture

- **MVI + Redux Pattern**: Unidirectional data flow with predictable state management
- **State Machine**: Validates state transitions and prevents invalid UI states
- **Clean Architecture**: Separation of concerns with Domain, Data, and Presentation layers
- **Dependency Injection**: Using Koin for DI across all platforms
- **Repository Pattern**: Abstract data sources with clean domain models

### Product Features

- **Infinite Scroll Pagination**: Load products on-demand with custom paging strategies
- **Shopping Basket**: Add, remove, and update product quantities
- **Price Calculations**: Real-time cost and retail price calculations
- **Navigation**: Type-safe navigation with Compose Navigation
- **Error Handling**: Graceful error handling with user feedback

### Testing

- **Unit Tests**: Comprehensive ViewModel and business logic tests
- **State Machine Tests**: Validates all state transitions
- **Coroutine Testing**: Using `UnconfinedTestDispatcher` for async tests
- **Fake Implementations**: Test doubles for repositories and pagers

## Architecture Layers

```
┌─────────────────────────────────────────────────────┐
│              Presentation Layer                     │
│  ┌──────────────┐  ┌──────────────┐                 │
│  │  ViewModel   │  │     MVI      │                 │
│  │              │  │   Store      │                 │
│  └──────────────┘  └──────────────┘                 │
│         ↕                 ↕                         │
│  ┌──────────────┐  ┌──────────────┐                 │
│  │    State     │  │    Intent    │                 │
│  │   Machine    │  │   Handler    │                 │
│  └──────────────┘  └──────────────┘                 │
└─────────────────────────────────────────────────────┘
                       ↕
┌─────────────────────────────────────────────────────┐
│               Domain Layer                          │
│  ┌──────────────┐  ┌──────────────┐                 │
│  │  Use Cases   │  │    Models    │                 │
│  └──────────────┘  └──────────────┘                 │
│  ┌──────────────┐  ┌──────────────┐                 │
│  │  Pagination  │  │  Repository  │                 │
│  │   Strategy   │  │  Interfaces  │                 │
│  └──────────────┘  └──────────────┘                 │
└─────────────────────────────────────────────────────┘
                       ↕
┌─────────────────────────────────────────────────────┐
│                Data Layer                           │
│  ┌──────────────┐  ┌──────────────┐                 │
│  │  Repository  │  │   Mappers    │                 │
│  │     Impl     │  │              │                 │
│  └──────────────┘  └──────────────┘                 │
│  ┌──────────────┐  ┌──────────────┐                 │
│  │   API        │  │     DTO      │                 │
│  │ (Ktor)       │  │    Models    │                 │
│  └──────────────┘  └──────────────┘                 │
└─────────────────────────────────────────────────────┘
```

## MVI Flow

```
User Action → Intent → State Machine Validation
                              ↓
                      Store processes Intent
                              ↓
                      Reducer creates new State
                              ↓
                      Side Effects (optional)
                              ↓
                      UI renders new State
```

## Tech Stack

### Core

- **Kotlin**: 2.2.0
- **Compose Multiplatform**: 1.8.2
- **Kotlin Coroutines**: 1.10.2
- **Kotlin Serialization**: For JSON parsing

### Networking

- **Ktor Client**: 3.2.1
    - Content negotiation
    - Logging
    - Platform-specific engines (OkHttp, Darwin)

### Dependency Injection

- **Koin**: 3.5.6
    - Core
    - Android
    - Compose integration

### UI & Navigation

- **Compose Multiplatform**: Declarative UI
- **Navigation Compose**: 2.9.0-beta03
- **Material Icons Extended**: Icon library
- **Lifecycle ViewModel**: 2.9.1

### Testing

- **JUnit**: 4.13.2
- **Kotlin Test**: For multiplatform tests
- **Coroutines Test**: For async testing
- **Espresso**: UI testing (Android)

## Project Structure

```
composeApp/src/
├── commonMain/
│   └── kotlin/
│       └── lt/vitalijus/cmp_custom_pagination/
│           ├── core/              # Core utilities
│           │   ├── mvi/           # MVI base classes
│           │   ├── network/       # Network utilities
│           │   └── utils/         # Pager utilities
│           ├── data/              # Data layer
│           │   ├── di/            # Data DI modules
│           │   ├── mapper/        # DTO to Domain mappers
│           │   ├── model/         # DTOs
│           │   ├── repository/    # Repository implementations
│           │   └── source/        # Remote data sources
│           ├── domain/            # Domain layer
│           │   ├── calculator/    # Business calculations
│           │   ├── model/         # Domain models
│           │   ├── navigation/    # Navigation utilities
│           │   ├── paging/        # Pagination strategies
│           │   ├── repository/    # Repository interfaces
│           │   └── usecase/       # Use cases
│           ├── presentation/      # Presentation layer
│           │   └── products/      # Products feature
│           │       ├── mvi/       # MVI components
│           │       ├── ui/        # UI components
│           │       └── ProductsViewModel.kt
│           ├── ui/                # Shared UI
│           │   └── theme/         # Theme configuration
│           └── di/                # App-level DI
├── androidMain/           # Android-specific code
├── iosMain/              # iOS-specific code
├── desktopMain/          # Desktop-specific code
└── androidTest/          # Android instrumented tests
```

## Getting Started

### Prerequisites

- **JDK**: 17 or higher
- **Android Studio**: Ladybug or newer
- **Xcode**: 15+ (for iOS development)
- **Kotlin**: 2.2.0

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/CMPCustomPagination.git
   cd CMPCustomPagination
   ```

2. **Open in Android Studio**
    - Open the project in Android Studio
    - Wait for Gradle sync to complete

3. **Run the app**

   **Android:**
   ```bash
   ./gradlew :composeApp:installDebug
   ```

   **Desktop:**
   ```bash
   ./gradlew :composeApp:run
   ```

   **iOS:**
    - Open `iosApp/iosApp.xcodeproj` in Xcode
    - Select a simulator or device
    - Press Run (⌘R)

### Running Tests

**All Tests:**

```bash
./gradlew test
```

**Android Instrumented Tests:**

```bash
./gradlew :composeApp:connectedAndroidTest
```

**Specific Test Class:**

```bash
./gradlew test --tests "lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModelTest"
```

## Key Components

### MVI Components

**Intent**: User actions

```kotlin
sealed interface ProductsIntent {
    data object LoadMore : ProductsIntent
    data class AddToBasket(val product: Product, val quantity: Int) : ProductsIntent
    data class UpdateQuantity(val productId: Long, val newQuantity: Int) : ProductsIntent
}
```

**State**: UI state

```kotlin
data class ProductsState(
    val products: List<Product> = emptyList(),
    val basketItems: List<BasketItem> = emptyList(),
    val isLoadingMore: Boolean = false,
    val error: String? = null
)
```

**Effect**: One-time events

```kotlin
sealed interface ProductsEffect {
    data class ShowError(val message: String) : ProductsEffect
    data object ShowBasketUpdated : ProductsEffect
    data class NavigateTo(val screen: Screen) : ProductsEffect
}
```

### State Machine

The state machine validates transitions between states:

```
Idle → LoadingProducts → Ready/Error
Ready → ProcessingBasket → Ready
Error → LoadingProducts (retry)
```

### Pagination Strategy

Custom pagination with pluggable strategies:

- **Offset-based pagination**: Traditional skip/limit approach
- **Cursor-based pagination**: For real-time data
- **Extensible**: Easy to add new strategies

## Testing Strategy

### Test Coverage

- **ViewModel Tests**: State management and business logic
- **State Machine Tests**: All valid/invalid transitions
- **Repository Tests**: Data layer logic
- **Use Case Tests**: Domain business rules
- **Paging Tests**: Pagination logic

### Test Approach

- **Fake Implementations**: Test doubles for external dependencies
- **Unconfined Dispatcher**: Synchronous coroutine execution in tests
- **State Collection**: Comprehensive state verification
- **Effect Verification**: Side effect testing

## UI Features

- **Material Design 3**: Modern, adaptive UI
- **Dark/Light Theme**: System theme support
- **Responsive Layout**: Adapts to different screen sizes
- **Loading States**: Skeleton screens and progress indicators
- **Error States**: User-friendly error messages
- **Empty States**: Guidance when no content is available

## Configuration

### Gradle Properties

Key configurations in `gradle.properties`:

```properties
kotlin.mpp.androidSourceSetLayoutV2.nowarn=true
org.gradle.jvmargs=-Xmx2048m
```

### API Configuration

Configure API endpoint in `data/source/RemoteDataSource.kt`:

```kotlin
private const val BASE_URL = "https://api.example.com"
```

## Documentation

### Code Documentation

- Comprehensive KDoc comments on public APIs
- Inline comments for complex logic
- Architecture decision records (ADRs) in code

### Architecture Patterns

- **MVI**: Model-View-Intent pattern
- **Redux**: Predictable state container
- **Clean Architecture**: Separation of concerns
- **Repository Pattern**: Data abstraction
- **Use Cases**: Single responsibility business logic

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable names
- Write tests for new features
- Document public APIs

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Ktor](https://ktor.io/)
- [Koin](https://insert-koin.io/)

## Contact

**Vitalijus** - [@vitalikas](https://github.com/yourusername)

Project
Link: [https://github.com/yourusername/CMPCustomPagination](https://github.com/yourusername/CMPCustomPagination)

---
**⭐ If you found this project helpful, please give it a star!**