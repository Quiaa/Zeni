# Zeni - Income Expense Tracker App

Zeni is a comprehensive mobile application that helps you manage your personal finances. You can easily track your income and expenses, set savings goals, and create payment reminders. This project is integrated with Firebase for backend services like authentication and database.

## 🌟 Features

- **Firebase Integration:** Uses Firebase for user authentication and cloud data storage.
- **User Login and Registration:** Securely create and log into your own account.
- **Dashboard:** Get an overview of your financial situation. View your income, expenses, and balance information.
- **Income/Expense Transactions:**
    - Add new income and expense transactions.
    - List all your transactions and review your historical records.
- **Savings Management:**
    - Create custom savings goals for yourself.
    - Track your progress towards your goals.
- **Reminders:**
    - Set reminders for your bills or other payments.
    - View your upcoming payments.
- **Category-Based Tracking:** Analyze where your money is going by categorizing your transactions.

## 🛠️ Installation

Follow the steps below to run the project on your local machine:

1.  **Clone the Project:**
    ```bash
    git clone https://github.com/Quiaa/Zeni.git
    ```
2.  **Firebase Setup:**
    - This project uses Firebase. Before building the app, you need to set up your own Firebase project.
    - Go to the [Firebase Console](https://console.firebase.google.com/), create a new project, and register your Android app with the package name `com.example.zeni`.
    - Download the `google-services.json` file from your Firebase project settings.
    - Place the downloaded `google-services.json` file in the `app/` directory of this project.
3.  **Open in Android Studio:**
    - Launch Android Studio.
    - Select "Open an existing Android Studio project".
    - Navigate to the cloned project directory and open it.
4.  **Install Dependencies:**
    - Android Studio will automatically sync the necessary Gradle dependencies when you open the project.
5.  **Run the Application:**
    - Select an emulator or connect a physical Android device.
    - Click the "Run 'app'" button.

## 🚀 Usage

1.  After launching the app, create an account or log in with your existing account.
2.  View your financial summary on the main dashboard.
3.  Add new transactions, savings goals, or reminders via the `+` button or relevant menus.
4.  Switch between different features (Transactions, Savings, Reminders) using the bottom navigation menu.
