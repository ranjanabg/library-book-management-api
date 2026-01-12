# 📚 Library Book Management API

![Java](https://img.shields.io/badge/Java-17-blue)
![REST](https://img.shields.io/badge/API-RESTful-blue)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-success)
![Database](https://img.shields.io/badge/Database-MySQL-orange)

![Build](https://github.com/ranjanabg/HouseFinder/actions/workflows/ci.yml/badge.svg)

A backend RESTful service for managing library operations such as books, members, racks, transactions, overdue fees, and recommendations.  
This service is built using a **service-oriented architecture** and supports both **librarian** and **reader** workflows, with RFID-based book tracking.

🌐 **Base URL:**  
https://library-book-management-api.dustakar.com

---

## ✨ Features

### Core Capabilities
- 📖 Book management (Add, View, Update, Delete)
- 👤 Member management (Register, Update, Cancel membership)
- 🔄 Book circulation (Checkout, Renew, Return)
- ⏰ Overdue fee calculation and enforcement
- 📍 Book location tracking using Rack & RFID concepts
- 🔔 Due-date notifications
- 📊 Library statistics (books, members, activity)
- ⭐ Book recommendation service using **TOPSIS + CRITIC** methodology

### Role-Based Access
- **Librarian**
  - Manage books, members, and racks
  - Verify misplaced books
  - View library analytics
- **Reader**
  - Search and view books
  - Checkout / Renew / Return books (PIN-based)
  - View profile and dues
  - Get personalized book recommendations

---

## 🧱 Architecture Overview

- **Backend**: Java (JAX-RS – RESTful Web Services)
- **Server**: Apache Tomcat
- **Database**: MySQL
- **Frontend (separate service)**: ReactJS

The backend is organized into logical REST services:
- Books Management Service
- Members / Profile Management Service
- Rack Management Service
- Book Transactions Service
- Recommendation (Composite) Service

---

## 🗄️ Database Schema

The system uses the following core tables:

- `Books`
- `Members`
- `Book_Transactions`
- `Book_Current_Location`
- `Overdue_Fees`
- `Racks`

---

## ⚙️ Prerequisites (Local Development)

- Java 8 or later
- Maven 3+
- MySQL 5.7+
- Eclipse IDE (recommended)

---

## 🛠️ Setup Instructions

### Database Setup

```bash
find . -name "init.sql"
mysql -u root -p < /path/to/init.sql
```

---

## 🔌 API Overview (Sample)

| Resource | Description |
|--------|-------------|
| `/books` | Manage books |
| `/members` | Manage library members |
| `/transactions` | Checkout, Renew, Return |
| `/racks` | Rack verification & placement |
| `/recommendations` | TOPSIS-based book suggestions |
| `/profile` | Reader profile & dues |

---

## 📄 License


[MIT License](https://choosealicense.com/licenses/mit/)


