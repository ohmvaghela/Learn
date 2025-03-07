# Interview 
1. Day 1 (27)
    - Array, List, String, Queue, Stack
2. Day 2 (28)
    - OS
    - Tree, Graph
3. Day 3 (1)
    - Creational Patterns (Factory, Abstract Factory, Builder, Singleton, Prototype).
    - Any two pattern real world question
4. Day 4 (2)
    - Structural Patterns (Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy).
    - Any two pattern real world question
5. Day 5 (3)
    - Autobilling
    - MechSimVault
    - Two design Pattern + Sort and Search algos
6. Day 6 (4)
    - Accio 
    - Two design Pattern 
    - DSA
      - Linked Lists: (Singly, Doubly, Circular)
      - Hashing: (HashMaps, HashSets, collision handling)
7. Day 7 (5)
    - Docker
    - Two design Pattern + DSA
      - Dynamic Programming: (Knapsack, Fibonacci, Coin Change, etc.)
      - Greedy Algorithms: (Activity Selection, Huffman Coding, etc.)
8. Day 8 (6)
    - Kubernetes
    - Two design Pattern + DSA
      - Heaps: (Heap Sort, Priority Queue)
      - Tries: (Prefix Tree, Autocomplete, Search Suggestions)
- Extra Time
  - SQL queries



## AutoBilling - MERN Stack Application

### 1. Brief Overview  
**AutoBilling** is a MERN stack application designed to help shopkeepers manage pending payments efficiently.  

### 2. Problem Statement  
Many shopkeepers allow customers to buy on credit but struggle with tracking pending payments and following up manually.  
AutoBilling solves this by automating bill management, reminders, and payment collection.  

### 3. Key Features  
- **Shopkeeper Registration:** Shopkeepers can sign up and manage their customers.  
- **Bill Management:** They can create a bill whenever a customer opts for ‘pay later.’  
- **Automated Payment Handling:** Integrated with **Razorpay** for seamless payment collection.  
- **Reminder Emails:** Uses **Nodemailer** to send regular reminders with payment links.  
- **Database:** Stores data securely on **MongoDB Atlas**.  

### 4. Deployment Challenges & Solutions  
- Initially deployed on **Render/Vercel**, but servers went inactive due to PAAS limitations.  
- Shifted to **Google Cloud Platform (GCP)**, but free-tier expired.  
- Now migrating to a self-managed **Linux server (HostEasy)**, using **SFTP for deployment**.  

## Mechanical Simulation Showcase

### 1. Brief Overview
- I am developing a platform similar to GrabCAD, but focused on showcasing **mechanical simulations** instead of CAD models.

### 2. Problem Statement
- While GrabCAD allows users to share CAD designs, there is no dedicated platform to showcase **simulation results** (like animations, stress analysis, and other mechanical engineering simulations). My project fills this gap.

### 3. Key Features
- **Simulation Showcase:** Users can upload and share mechanical simulations.  
- **Data Management:** Stores simulation files (videos, images, zip files, etc.).  
- **Scalable Backend:** Built using **Django** to handle requests efficiently.  
- **Database:** Uses **MySQL** for structured data storage.  
- **Modern UI:** Frontend developed with **React.js** for a smooth user experience.  

### 4. Deployment & Storage Challenges
- **Static File Storage:** Still exploring the best option for hosting large media files (**images, videos, zip files**).  
- **Server Management:** Django handles backend logic, and MySQL ensures reliable data storage.  
