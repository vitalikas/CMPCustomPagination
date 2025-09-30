package lt.vitalijus.cmp_custom_pagination.di

import lt.vitalijus.cmp_custom_pagination.core.network.networkModule
import lt.vitalijus.cmp_custom_pagination.data.di.dataModule
import lt.vitalijus.cmp_custom_pagination.domain.di.domainModule
import lt.vitalijus.cmp_custom_pagination.presentation.products.di.presentationModule

/**
 * Main application DI module
 * Combines all layer-specific modules following Clean Architecture:
 * - Domain Layer: Pure business logic (use cases, entities)
 * - Data Layer: Infrastructure (repositories, APIs, databases)
 * - Presentation Layer: UI logic (ViewModels, navigation)
 * - Core/Network: Cross-cutting concerns
 */
val appModule = listOf(
    networkModule,      // Cross-cutting: HTTP client, etc.
    dataModule,         // Data layer: repositories, APIs
    domainModule,       // Domain layer: use cases, business logic
    presentationModule  // Presentation layer: ViewModels, navigation
)
