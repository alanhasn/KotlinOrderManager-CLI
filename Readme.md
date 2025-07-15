# 🛒 Order Manager – Kotlin Console App

**📌 Project Type:** Training + Exam Project  
**🧪 Language:** Kotlin  
**⌨️ Interface:** Console (CLI)  
**⏱️ Duration:** Developed during a 2-hour coding exam  

---

## 📦 Project Overview

Order Manager is a simple Kotlin-based console application that simulates a basic e-commerce order system.  
It was developed as part of a **time-limited exam** to test proficiency in **Object-Oriented Programming (OOP)** and **Kotlin Coroutines**.  
This is **not a production-ready** project, but rather a clean and organized example of applying key programming principles.

---

## 🎯 Objectives

- Display available products in a virtual store.
- Allow customers to place orders.
- Admins can view all existing orders.
- Use **Kotlin Coroutines** to simulate asynchronous data fetching and processing.
- Graceful error handling and user interaction via terminal input.

---

## 🛠️ Technologies Used

| Technology       | Purpose |
|------------------|---------|
| **Kotlin**       | Main programming language |
| **OOP**          | Abstract classes, inheritance, and interfaces |
| **Data Classes** | Modeling `Product` and `Order` objects |
| **Interfaces**   | Separation of concerns (`Loggable`, `OrderManager`) |
| **Coroutines**   | Asynchronous operations using `suspend`, `async`, and `withContext(Dispatchers.IO)` |
| **Console I/O**  | Text-based interaction with users |
| **Layered Design** | Clean project structure with separate folders |

---

## 📁 Project Structure

```bash
src/
└── main/
    └── kotlin/
        ├── data/
        │   ├── models/
        │   │   ├── User.kt
        │   │   ├── Product.kt
        │   │   └── Order.kt
        │   └── StoreManager.kt
        ├── interfaces/
        │   ├── OrderManager.kt
        │   └── Loggable.kt
        ├── views/
        │   └── ConsoleUI.kt
        └── Main.kt
````

---

## 🚀 Key Features

* ✅ Well-structured OOP design using abstract classes and interfaces.
* ✅ Realistic simulation of a shopping experience.
* ✅ Admin and customer roles.
* ✅ Asynchronous product fetching and order handling using coroutines.
* ✅ Graceful error handling using `Result` API and `try/catch`.
* ✅ Clean code organization with separated concerns.

---

## 🧪 Example Run

```text
=== Shop Console ===
Enter username (admin for admin access): ahmed

=== Main Menu ===
1. View Products
2. Place Order
3. My Orders
4. Get Product by id
5. Exit

> 2
Ordering: Laptop
✅ Ordered: Laptop
```

---

## ⚠️ Notes

* This is a **practice-only** and **exam-based** project.
* No database or external API is used — all data is in-memory.
* Designed to showcase understanding of:

    * Kotlin basics
    * OOP design
    * Coroutine usage for concurrency
* Can be extended into:

    * A GUI desktop version using JavaFX or Compose
    * A version backed by a real database

---

## 👤 Author

Created by **Alan Hassan**
📅 Developed: **July 2025**
🎓 Context: Kotlin Exam – OOP & Coroutines Console App
