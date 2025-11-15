# Crop Advisor – AI-Powered Crop Recommendation App

Crop Advisor is an Android application that uses Google Gemini AI to recommend the best crops based on soil and environmental conditions. It includes an AI chat assistant, task checklist, and Firebase Authentication—all wrapped in a clean, modern UI.

---

## Features

### AI Crop Recommendation
- Enter soil type, nitrogen level, pH, rainfall, temperature, and humidity.
- Gemini AI analyzes the values and suggests suitable crops.
- Displays a clean, readable summary on a dedicated result screen.

### AgriChat – AI Farming Assistant
- Chat with an AI about farming, soil, fertilizers, pests, and crops.
- Gemini AI generates smart agricultural answers.
- Clean chat interface with sent/received message bubbles.

### Farming Checklist
- Add your own farming tasks.
- Mark tasks as completed or delete them.
- Lightweight and fast agricultural to-do manager.

### User Authentication
- Email/Password Login & Signup powered by Firebase.
- Secure session handling.
- Profile page with user info and logout option.

### Smooth Navigation
- Bottom navigation bar with:
  - **Home** (Crop Recommendation Input)
  - **AgriChat**
  - **Checklist**
  - **Profile**

---

## Tech Stack

- **Kotlin**
- **Firebase Authentication**
- **Google Gemini AI API**
- **Retrofit + OkHttp**
- **Gson**
- **Material Design Components**
- **Android Jetpack (Fragments, ViewModel, Navigation)**

---

## How to Run the App

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/CropAdvisor.git
   ```
2. Open the project in Android Studio.

3. Add your Gemini API Key in:

4. HomeFragment.kt (for crop analysis)

5. RetrofitClient.kt (for chat/AI content)

6. Add your google-services.json for Firebase Authentication.

7. Let Gradle sync.

8. Run the app on a physical device or emulator.

---

## Requirements

Android Studio Hedgehog or newer

Minimum SDK: 24+

Gemini API Key

Firebase Project with Authentication enabled
