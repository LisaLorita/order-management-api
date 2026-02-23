# Order Management API

Backend REST API built with Spring Boot for managing customer orders.

This project is part of my learning journey as a backend developer.
The goal is to understand how to design, build and structure a professional Java backend application using modern tools and best practices.

## 🚀 Tech Stack

- Java 17
- Spring Boot 4
- Maven
- PostgreSQL
- Spring Data JPA
- Spring Security + JWT
- Docker
- Git

## 📌 Features

### 👤 Users

- Create user
- Get users
- Delete user
- User authentication (JWT)
- Role-based authorization

### 📦 Products

- Create product
- Update product
- Delete product
- Manage stock
- Prevent order creation if stock is insufficient

### 🧾 Orders

- Create order
- Get all orders
- Get order by ID
- Cancel order
- Order status management

### 🧩 Order Items

- Each order contains multiple products
- Quantity per product
- Price stored at purchase time
- Automatic stock reduction when order is created

### 💬 Comments

- Users can comment on orders
- Each comment is linked to a user and an order

---

## 🏗 Domain Model Overview

Entities:

- User
- Product
- Order
- OrderItem
- Comment

Relationships:

- A User creates many Orders
- An Order contains multiple OrderItems
- Each OrderItem references one Product
- An Order can have multiple Comments
- A User can write multiple Comments

---

## 🧠 What I'm Learning

- REST API design
- Package by feature architecture
- Database integration
- Clean code practices
- Docker basics
- Git workflow with conventional commits
- Secure authentication with JWT

## 👩‍💻 Author

Alba M  
GitHub: https://github.com/LisaLorita
