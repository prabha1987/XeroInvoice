# Xero Invoice App

A simple Android app that displays invoice lists and details using Clean Architecture and Jetpack Compose.

## Features

- Invoice list with pull-to-refresh
- Invoice details with line items
- Error handling and retry functionality
- Different API endpoints for testing (menu dropdown)
- Material Design 3 UI

## Architecture

The app follows Clean Architecture with MVVM pattern:

- **UI Layer**: Jetpack Compose screens and ViewModels
- **Domain Layer**: Use cases and business models
- **Data Layer**: Repository with API service and caching

## Tech Stack

- Kotlin
- Jetpack Compose
- Material Design 3
- Kodein DI
- Retrofit + OkHttp
- Coroutines
- JUnit + MockK

## Key Implementation Details

- Custom `InvoiceResult<T>` for error handling instead of exceptions
- ViewModelProvider.Factory for dependency injection with Kodein
- In-memory caching for fast detail screen navigation
- Comprehensive unit tests for all layers

## How to Run

1. Clone the repository
2. Open in Android Studio
3. Build and run

## Requirements

- Android Studio
- Min SDK: 26 (Android 8.0)
- Target SDK: 35 (Android 15)