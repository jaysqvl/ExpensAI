# ExpensAI

ExpensAI is an intelligent expense tracking application that combines traditional financial management with AI-powered insights. Built for Android using modern architecture and best practices, it helps users manage their finances more effectively through smart receipt scanning, automated categorization, and personalized spending insights.

## 🌟 Key Features

- **Smart Receipt Scanning**: Automatically extract transaction details from receipt photos using computer vision
- **AI-Powered Insights**: Get personalized spending analysis and recommendations
- **Real-time Transaction Tracking**: Monitor expenses and income with an intuitive dashboard
- **Customizable Categories**: Organize transactions with user-defined categories
- **Spending Goals**: Set and track monthly spending limits and savings goals
- **Visual Analytics**: View spending patterns through interactive charts
- **Cloud Sync**: Secure data synchronization with Firebase
- **Multi-user Support**: Individual user accounts with personalized preferences

## 🛠️ Technology Stack

- **Frontend**: Native Android with Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Persistence Library
- **Authentication**: Firebase Auth
- **Cloud Storage**: Cloud Firestore
- **AI/ML Services**: 
  - OpenAI GPT-4 for transaction analysis
  - Computer Vision for receipt processing
- **Charts**: MPAndroidChart
- **Dependency Injection**: Manual DI with ViewModelFactory
- **API Communication**: Retrofit2

## 🏗️ Architecture

The application follows clean architecture principles and is organized into the following key components:

- **UI Layer**: Activities, Fragments, and ViewModels
- **Data Layer**: Repositories, DAOs, and Remote Data Sources
- **Domain Layer**: Use Cases and Business Logic
- **Cloud Services**: Text and Vision microservices for AI processing

## 🔒 Security Features

- Secure user authentication via Firebase
- Local data encryption with Room
- Safe API communication over HTTPS
- Protected cloud functions with proper authentication

## 🚀 Future Enhancements

- Budget forecasting using AI
- Expense sharing between users
- Export functionality for financial reports
- Advanced analytics dashboard
- Custom notification rules


## 🛠️ Setup and Installation

1. Clone the repository
2. Add your `google-services.json` file to the app directory
3. Configure your API keys in the appropriate configuration files
4. Build and run using Android Studio

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.