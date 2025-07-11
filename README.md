<p align="center">
  <img src="docs/Logo.png" alt="Chronogram Title" width="400"/>
</p>


<p align="center" style="margin-top: 40px;">
    <img src="https://img.shields.io/github/stars/bonoboprog/Chronogram?style=plastic&color=FF2E2E&labelColor=2d0052" alt="GitHub stars">         <!-- Rosso -->
    <img src="https://img.shields.io/github/contributors/bonoboprog/Chronogram?style=plastic&color=FF7F00&labelColor=2d0052" alt="GitHub contributors"> <!-- Arancione -->
    <img src="https://img.shields.io/github/repo-size/bonoboprog/Chronogram?style=plastic&color=FFFF33&labelColor=2d0052" alt="GitHub repo size">  <!-- Giallo -->
    <img src="https://img.shields.io/github/license/bonoboprog/Chronogram?style=plastic&color=33FF33&labelColor=2d0052" alt="GitHub License">     <!-- Verde -->
    <img src="https://img.shields.io/badge/API%20Status-stable-33CCFF?style=plastic&labelColor=2d0052" alt="API Status">                          <!-- Azzurro -->
    <img src="https://img.shields.io/badge/Platform-Android-6666FF?style=plastic&labelColor=2d0052" alt="Platform">                               <!-- Blu -->
    <img src="https://img.shields.io/badge/Version-1.0.0-CC66FF?style=plastic&labelColor=2d0052" alt="Version">                                   <!-- Viola -->
</p>







## 📛 License

All rights reserved.  
This code is proprietary and intended for academic or personal reference only.  
Unauthorized use, modification, or redistribution is prohibited.




[📊 View Chronogram Presentation](https://docs.google.com/presentation/d/14NgOd5NSt-bIzUknydG7A0ilcgBkQL68LGOZmH8EEhI/edit?slide=id.g35803e53045_1_16)


The goal of this project is to develop an Android application that allows users to record their daily activities. The app will feature a main page for real-time activity tracking, logging the various activities performed throughout the day. On the same page, the user will also be able to view activity logs from the previous two days. The design supports both real-time and retrospective logging, enabling users to choose the frequency of notifications based on their daily routines. This flexibility makes the app suitable for both personal use and for researchers or organizations conducting time-use studies.

---

## 🚀 Features

- Secure user registration and login
- Password hashing 
- MySQL database integration
- Client-server communication
- Basic frontend form validation
- Protection against common vulnerabilities (e.g., SQL injection, password theft) 

---

## 📚 Libraries and Dependencies

This project uses the following key libraries:

| Library               | Purpose                                      | Version              | Download                                            |
|-----------------------|----------------------------------------------|----------------------|-----------------------------------------------------|
| **Struts 2**          | MVC web framework for Java                   | struts-2.5.22-all    | [Struts Download](https://archive.apache.org/dist/struts/2.5.22/) |
| **MySQL Connector**   | JDBC driver for MySQL	                       | 8.0.33               |  [Struts Download](https://archive.apache.org/dist/struts/2.5.22/) |
| **Maven**          |  Java build tool                 | 3.8+    | [Maven](https://maven.apache.org/) |
| **Docker**          | Containerization + orchestration                   | Engine 24.x+    | [Docker](https://www.docker.com/) |
| **Docker Compose**          | Multi-container orchestration                   | v2.0+    | [Struts Download](https://archive.apache.org/dist/struts/2.5.22/) |

---

## 🛠️ Tech Stack

| Layer       | Technology             |
|-------------|------------------------|
| Backend     | Java + Struts 2        |
| Server      | Apache Tomcat (v9.0.71)|
| Database    | MySQL                  |
| Frontend    | HTML, CSS, JavaScript  |
| Security    | HTTPS                  |


Library / Tool	Purpose	Version	Download / Info
Struts 2	MVC web framework for Java	struts-2.5.22-all	Struts Download
/J		Included via Maven
Maven			Maven
			Docker
Docker Compose	Multi-container orchestration	v2.0+	Included in Docker Desktop



---
<details>
<summary>🧑‍💻 <strong>Prerequisiti</strong></summary>

- [x] Docker
- [x] Java 8+
- [x] Maven
- [x] Node.js + Ionic CLI (`npm install -g @ionic/cli`)
- [ ] (Opzionale) MySQL Workbench per visualizzare lo schema

</details>

---

<details>
<summary>⚙️ <strong>Setting Up a Development Environment</strong></summary>

 1. **Clone the repository**
    ```bash
    git clone https://github.com/bonoboprog/DPN_APP.git
    cd DPN_APP
    ```

 2. **Execute the Complete Setup Script**

    ```bash
    ./dev_setup.sh
    ```

    This script will:

    - Stop and remove old containers
    - Build the backend (`build.sh`)
    - Start MySQL and Tomcat
    - Initialize the database with `schema.sql`

 3. **Go to:**

     👉 http://localhost:8080/chronogram

</details>



<details> 
<summary>🗃️ <strong>Explore the Database</strong></summary>


 1. **Access via terminal:**

    ```bash
    docker exec -it chronogram-mysql mysql -u chronouser -pchronopass chronogram
    ```

 2. **Verify the tables:**

    ```bash
    SHOW TABLES;
    DESCRIBE nome_tabella;
    ```

</details>



<details> 
<summary>📤 <strong>Update the shared DB schema</strong></summary>

1. **After making EER/Workbench changes → perform *forward engineering* to the MySQL container**

2. **Export the Schema with:**

   ```bash
   ./export_schema.sh
   ```

3. **Version the schema:**

   ```bash
   git add docker/init/schema.sql
   git commit -m "🔄 aggiornata struttura DB"
   git push
   ```


</details>


> [!IMPORTANT]  
> ⚠️ The `dev_setup.sh` script automatically loads schema.sql only on the first startup (empty volume).
>
> To force a reset:
>
>  ```bash
>  docker compose down -v
>  ./dev_setup.sh
>  ```

---

### 🤝 Collaborazione

Ogni collaboratore può:

```bash
git clone ...
./dev_setup.sh
```

Poi contribuire modificando:

- Backend (Java)
- Frontend (/mobile)
- Schema DB (export_schema.sh)

> [!NOTE] 
> 🧠 Importante: il file schema.sql è il punto di sincronizzazione per il DB!

---

## <a name="contributors">👥 Contributors</a>

| Name              |  GitHub                                               |
|-------------------|-------------------------------------------------------|
| Violeta Perez     | [@violetapd](https://github.com/violetapd)            |
| Paolo Simeone     | [@bonoboprog](https://github.com/bonoboprog)          |
| Giuseppe Alfieri  | [@giusalfieri](https://github.com/giusalfieri)        |









