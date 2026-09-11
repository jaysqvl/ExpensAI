# ExpensAI

ExpensAI is a team-built Android expense-tracking prototype for CMPT362. It combines local transaction history, receipt scanning, and generated spending summaries using Kotlin, Room, Firebase Authentication, and Python HTTP services.

## 🌟 Key Features

- **Smart Receipt Scanning**: Automatically extract transaction details from receipt photos using computer vision
- **AI-Powered Insights**: Get personalized spending analysis and recommendations
- **Real-time Transaction Tracking**: Monitor expenses and income with an intuitive dashboard
- **Customizable Categories**: Organize transactions with user-defined categories
- **Spending Goals**: Set and track monthly spending limits and savings goals
- **Visual Analytics**: View spending patterns through interactive charts
- **Cloud Sync**: Firestore transaction-sync code is present but currently disabled
- **Sign-in**: Firebase Authentication and user preferences

## 🛠️ Technology Stack

- **Frontend**: Native Android with Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Persistence Library
- **Authentication**: Firebase Auth
- **Cloud Storage**: Cloud Firestore
- **AI/ML Services**: 
  - OpenAI GPT-4o mini for receipt extraction and spending summaries
  - Computer Vision for receipt processing
- **Charts**: MPAndroidChart
- **Dependency Injection**: Manual DI with ViewModelFactory
- **API Communication**: Retrofit2

## 🏗️ Architecture

The application follows clean architecture principles and is organized into the following key components:

- **UI Layer**: Activities, Fragments, and ViewModels
- **Data Layer**: Repositories, DAOs, and Remote Data Sources
- **Transaction Processing**: Receipt response parsing and local transaction creation
- **Cloud Services**: Text and Vision microservices for AI processing

## Data and Service Status

- Sign-in uses Firebase Authentication. Transactions are stored in a local Room database without configured encryption.
- Firestore transaction sync is disabled in both `PurchaseRepository` and `TransactionSyncService`.
- The app uses HTTPS service URLs. The checked-in Python handlers do not verify Firebase tokens; server-side access control must be configured before exposing a deployment.
- The current Android tests are template smoke tests; receipt processing and synchronization do not have automated coverage.

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

No project-level LICENSE file is currently included.
