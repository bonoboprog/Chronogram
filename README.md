# DPN_APP

https://docs.google.com/presentation/d/14NgOd5NSt-bIzUknydG7A0ilcgBkQL68LGOZmH8EEhI/edit?slide=id.g35803e53045_1_16#slide=id.g35803e53045_1_16

# ✍️ Diary_App

The goal of this project is to develop an Android application that allows users to log their daily activities. The app will feature a main page for real-time activity tracking and a calendar section where users can review previously recorded activities. The design supports both real-time and retrospective logging, enabling users to choose the frequency of prompts based on their daily routines. This flexibility makes the app suitable for personal use as well as for researchers and organizations involved in time-use studies.

---

## 🚀 Features

- Secure user registration and login
- Password hashing 
- MySQL database integration
- Client-server communication
- Basic frontend form validation
- Protection against common vulnerabilities (e.g., SQL injection, password theft) (hopefully XD)

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









