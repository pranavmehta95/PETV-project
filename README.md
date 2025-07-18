# 💰 Personal Expense Tracker

A simple Java-based desktop application to help users manage and track their daily personal expenses. The app provides a GUI interface where users can add, view, and categorize their expenses, which are saved to a file for persistence.

---

## 🚀 Features

- Add expenses with date, category, and amount
- View a list of all saved expenses
- Stores expenses persistently using file serialization
- Simple and clean graphical user interface using **Swing**
- Lightweight and beginner-friendly project

---

## 🛠️ Technologies Used

- Java (JDK 8+)
- Java Swing (GUI)
- Object Serialization (`.dat` file) for saving data

---

## 📁 Project Structure

```bash
.
├── Expense.java                 # Model class for an expense
├── ExpenseManager.java         # Handles adding, retrieving, saving, and loading expenses
├── PersonalExpenseTracker.java # Main GUI class
├── expenses.dat                # Serialized data file for saved expenses
