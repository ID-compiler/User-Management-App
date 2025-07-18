# UserManagementApp

**UserManagementApp** is an Android application built as part of the **Android Development assignment for the BITS Pilani **. It demonstrates core mobile development skills such as admin panel, user authentication, data persistence, Firebase backend integration, and background services.

---

## Features

- **User Registration & Login**
  - Register using username, password, and role (`admin` or `normal`)
  - Firebase Authentication (`createUserWithEmailAndPassword`, `signInWithEmailAndPassword`)
  - Network connectivity check before registration or login
  - Login saves username in SharedPreferences

- **Welcome Screen**
  - Displays a personalized welcome message: “Welcome, [Username]!”
  - Logout clears SharedPreferences and navigates back to the Register screen

- **Firebase Realtime Database Integration**
  - Logged-in users can store Name and Email in Firebase
  - Admin users can view data of all registered users
  - Normal users can view only their own data
  - Data is displayed using a scrollable ListView

- **Background Service**
  - Periodic background sync and notifications implemented using Android Services

---

## Tech Stack

- Java (**ANDROID**)
- Firebase Authentication (**BACKEND**)
- Firebase Realtime Database (**BACKEND**)
- SharedPreferences (**Local Storage**)
- Android Services
- XML Layouts
- Android Studio

---

## Notes

- Firebase backend is used for user authentication and data storage
- Local SharedPreferences is used for storing session data like username
- The app is structured with clean activity navigation and role-based access
- Developed independently as part of BITS coursework

---

## Author

**Ichha Dwivedi**  
