# Warehouse Management System with AI Assistant

This project is a **desktop application for warehouse management** with an integrated **AI-powered assistant** that enables users to interact with the system using **natural language queries**.

## Features
- Inventory management: add, update, search, and categorize products
- Order management: create, track, filter, and cancel orders
- Customer management: register, edit, and search client data
- Employee management with role-based access control
- Automatic event logging (Spring AOP)
- Analytical reports and statistics generation (tables & charts)
- Integrated AI assistant (via **Groq API + Spring AI**) for:
  - Natural language queries
  - Data analysis and reporting
  - Returning structured responses (text, tables, or charts)

## Tech Stack
- **Java** – core programming language
- **Spring Boot** – backend services and data access
- **JavaFX** – user interface
- **PostgreSQL** – relational database
- **Spring AI + Groq API** – natural language assistant integration
- **Spring AOP** – aspect-oriented logging

## Architecture
- **Three-layer architecture** (UI, business logic, data access)
- **MVC pattern** for JavaFX interface
- **Spring Data JPA** for database operations
- **Modular design** for goods, orders, customers, employees, analytics, and AI

## Use Cases
- Automating warehouse processes for small and medium businesses
- Improving decision-making with AI-driven analytics
- Enhancing user interaction with natural language queries

## Screenshots
![AppSchema](/images/login.png)

![AppSchema](/images/admin.png)

![AppSchema](/images/warehouseman.png)

![AppSchema](/images/operator.png)

![AppSchema](/images/assistant_chat.png)

![AppSchema](/images/assistant_request.png)

![AppSchema](/images/assistant_chart.png)

![AppSchema](/images/analytic.png)

![AppSchema](/images/goods_add.png)

![AppSchema](/images/order_create.png)


## Installation
1. Clone the repository
2. Configure PostgreSQL database and update application properties
3. Run the Spring Boot backend
4. Launch the JavaFX client

## Author
Developed by **Artem Chaika** as a **Bachelor’s Thesis Project** at Oles Honchar Dnipro National University (2025).
