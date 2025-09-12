# Smart Vending Machine Management System  

A **Spring Boot application** designed to simulate and manage a vending machine. The system allows customers to browse products and complete purchases using either cash or card, while administrators can securely log in to manage reports and monitor machine performance. Reports are generated in **JSON format** and cover stock levels, sales transactions, and product sales volume.  

## Features  
- **Customer-facing module**:  
  - Browse available products.  
  - Simulate item purchase using cash or card, with the option of returning change amount in case of cash payment.  
  - Automatic stock validation and **error handling** (e.g., insufficient stock, invalid payment).  

- **Admin panel** (secured with **Spring Security**):  
  - Login with admin password.  
  - Generate and download JSON reports:  
    - **Stock Report** – current product quantities.  
    - **Sales Report** – detailed transactions within a date range.  
    - **Volume Report** – product sales counts over time.  

- **Data persistence**: MySQL database integration, ensuring all products and sales data are stored between runs.  

- **Testing and reliability**:  
  - **JUnit 5** and **Mockito** for service and controller testing.  
  - Covers business logic, payment validation, and exception handling.  

- **Project management**:  
  - Worked in weekly **sprints** with defined goals.  
  - Tracked progress using **GitHub Issues** and a **Kanban board**.

## Tech Stack  
- **Backend**: Java, Spring Boot, Spring Security  
- **Database**: MySQL (Spring Data JPA)  
- **Testing**: JUnit 5, Mockito, MockMvc  
- **Project management**: GitHub Issues, Kanban board, sprints  
