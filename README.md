# 📖 Quotd - Daily Quote Discovery App

<div align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

**A beautiful, Instagram-inspired daily quote discovery app built with Kotlin & Jetpack Compose** ❤️

*A personal Android project using ZenQuotes API*

[Features](#-features) • [Screenshots](#-screenshots) • [Tech Stack](#-tech-stack) • [Installation](#-installation) • [Architecture](#-architecture)

</div>

---

## ✨ Features

### 🎯 Core Functionality
- **📜 Daily Quotes**: Discover inspiring quotes from famous personalities
- **⬆️⬇️ Vertical Swipe Navigation**: Instagram-style vertical paging for seamless quote browsing
- **❤️ Double-Tap to Like**: Intuitive double-tap gesture with beautiful heart animation
- **⭐ Favorites Collection**: Save your favorite quotes for later viewing
- **📤 Easy Sharing**: Share quotes with friends via any messaging or social media app
- **🗑️ Swipe to Delete**: Remove favorites with a smooth swipe-to-dismiss gesture

### 🎨 Design Highlights
- **Modern Material 3 Design**: Beautiful, cohesive UI following Material You guidelines
- **Instagram-Inspired Interface**: Familiar and friendly user experience
- **Smooth Animations**: Delightful micro-interactions and transitions
- **Dark Mode Support**: Comfortable reading in any lighting condition
- **Custom Typography**: Elegant Funnel Display and Museo Moderno font families

---

## 📱 Screenshots

> Add your app screenshots here to showcase the beautiful UI! 

---

## 🛠️ Tech Stack

### Core
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: 33 (Android 13)
- **Target SDK**: 36

### Architecture & Libraries
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**:  Hilt/Dagger
- **Navigation**: Jetpack Navigation Compose
- **Networking**: 
  - Retrofit for API calls
  - OkHttp for logging
  - Gson for JSON parsing
- **Local Database**: Room
- **API**:  [ZenQuotes API](https://zenquotes.io/)

### Key Dependencies
```gradle
• Jetpack Compose (Material 3, Navigation, UI)
• Hilt for Dependency Injection
• Retrofit & OkHttp for Networking
• Room for Local Database
• Kotlin Coroutines & Flow
• Material Icons Extended
```

---

## 📥 Installation

### Prerequisites
- Android Studio (Hedgehog | 2023.1.1 or later)
- JDK 21
- Android SDK 33+

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Quantum3600/Quotd.git
   cd Quotd
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on emulator or device**
   - Click the Run button or press `Shift + F10`
   - Select your target device

---

## 🏗️ Architecture

The app follows **MVI (Model-View-Intent)** architecture pattern with **Clean Architecture** principles:

```
📦 com.trishit.quotd
 ┣ 📂 data              # Data layer (API, Database, Models)
 ┣ 📂 ui                # UI layer (Screens, ViewModels)
 ┣ 📂 components        # Reusable Compose components
 ┣ 📂 di                # Dependency Injection modules
 ┗ 📂 theme             # Material 3 theming
```

### Key Components

- **HomeScreen**: Main quote discovery screen with vertical pager
- **FavouriteScreen**: Collection of saved quotes
- **QuoteViewModel**: State management and business logic
- **Room Database**: Local persistence for favorites
- **Retrofit Service**: Network calls to ZenQuotes API

---

## 🎯 Key Features Implementation

### 🔄 Vertical Paging
Uses `VerticalPager` from Jetpack Compose Foundation to create Instagram-like quote browsing: 
```kotlin
val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
```

### ❤️ Double-Tap Animation
Custom gesture detection with animated heart popup:
- Double-tap triggers favorite action
- Beautiful spring-based animation
- Smooth fade-in/fade-out effects

### 🗑️ Swipe to Delete
Material 3 `SwipeToDismissBox` for intuitive favorite removal:
- Swipe left or right to reveal delete action
- Heartbroken icon indicator
- Smooth dismissal animation

---

## 🌐 API Reference

This app uses the **[ZenQuotes API](https://zenquotes.io/)** to fetch inspirational quotes. 

**Endpoint**: `https://zenquotes.io/api/quotes`

No API key required! 🎉

---

## 🤝 Contributing

Contributions are welcome! Feel free to:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is available for educational and personal use.  Please check the [Privacy Policy](privacy-policy.md) for data handling information.

---

## 👤 Author

**Trishit Majumdar** ([@Quantum3600](https://github.com/Quantum3600))

- 📧 Feel free to reach out for collaborations! 

---

## 🙏 Acknowledgments

- **[ZenQuotes API](https://zenquotes.io/)** for providing the quote data
- **Jetpack Compose** community for excellent resources
- All the inspiring personalities whose quotes make this app meaningful

---

<div align="center">

**Made with ❤️ using Kotlin & Jetpack Compose**

⭐ Star this repo if you find it helpful! 

</div>
