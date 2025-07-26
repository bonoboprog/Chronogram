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









## 💼 License

All rights reserved.
This code is proprietary and intended for academic or personal reference only.
Unauthorized use, modification, or redistribution is prohibited.

[📊 View Chronogram Presentation](https://docs.google.com/presentation/d/14NgOd5NSt-bIzUknydG7A0ilcgBkQL68LGOZmH8EEhI/edit?slide=id.g35803e53045_1_16)

The goal of this project is to develop an Android application that allows users to record their daily activities. The app will feature a main page for real-time activity tracking, logging the various activities performed throughout the day. On the same page, the user will also be able to view activity logs from the previous two days. The design supports both real-time and retrospective logging, enabling users to choose the frequency of notifications based on their daily routines. This flexibility makes the app suitable for both personal use and for researchers or organizations conducting time-use studies.

---

## 🚀 Features

* Secure user registration and login
* Password hashing
* MySQL database integration
* Client-server communication
* Basic frontend form validation
* Protection against common vulnerabilities (e.g., SQL injection, password theft)

---

## 📚 Libraries and Dependencies

| **Library**         | **Purpose**                   | **Version**  | **Download**                                                      |
| ------------------- | ----------------------------- | ------------ | ----------------------------------------------------------------- |
| **Struts 2**        | MVC web framework for Java    | 2.5.22 (all) | [Struts Download](https://archive.apache.org/dist/struts/2.5.22/) |
| **MySQL Connector** | JDBC driver for MySQL         | 8.0.33       | [MySQL Connector](https://dev.mysql.com/downloads/connector/j/)   |
| **Maven**           | Java build tool               | 3.8+         | [Maven](https://maven.apache.org/)                                |
| **Docker**          | Containerization engine       | 24.x+        | [Docker](https://www.docker.com/)                                 |
| **Docker Compose**  | Multi-container orchestration | 2.0+         | Included in Docker Desktop                                        |

---

## 🛠️ Tech Stack

| Layer    | Technology              |
| -------- | ----------------------- |
| Backend  | Java + Struts 2         |
| Server   | Apache Tomcat (v9.0.71) |
| Database | MySQL                   |
| Frontend | HTML, CSS, JavaScript   |
| Security | HTTPS                   |

Library / Tool	Purpose	Version	Download / Info
Struts 2	MVC web framework for Java	struts-2.5.22-all	Struts Download
/J		Included via Maven
Maven			Maven
			Docker
Docker Compose	Multi-container orchestration	v2.0+	Included in Docker Desktop

---

## 🔐 Environment Variables (.env)

> [!IMPORTANT]
> ⚠️ **Make sure all `.env` files are saved with LF (Unix-style) line endings — especially after each edit.**  
> On Windows, you can switch from `CRLF` to `LF` in the bottom-right corner of editors like VS Code.  
> This prevents parsing issues in Docker, Node, and other tools.


The project uses two environment configuration files that look like these:



### 1. Root `.env` (Backend Configuration)
Located in the project root folder:

```env
# Database
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=chronogram
MYSQL_USER=chronouser
MYSQL_PASSWORD=your_db_password

# API Keys
LLM_API_KEY=your_openrouter_api_key

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USER=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Security
JWT_SECRET_KEY=your_jwt_secret_key
APP_CANONICAL_URL=http://localhost:8100
```

### 2. Front `.env` (Frontend Configuration)

Located in /frontend folder

```env
VITE_API_BASE_URL=https://your-ngrok-subdomain.ngrok-free.app/chronogram
```


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

0. **Install Core Dependencies**

   These are required globally on your system before launching the app.

   ````bash
	# --- Java 11+ ---
	sudo apt update
	sudo apt install openjdk-11-jdk

	# Verify Java version
	java -version

	# --- Maven ---
	sudo apt install maven

	# Verify Maven version
	mvn -v

	# --- Node.js (v18.x recommended) ---
	# Use Node Version Manager (nvm) to install/manage Node versions
	curl -o- [https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.3/install.sh](https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.3/install.sh) | bash
	source ~/.bashrc
	nvm install 18
	nvm use 18

	# Verify Node.js and npm
	node -v
	npm -v

	# --- Ionic CLI ---
	npm install -g @ionic/cli

	# --- Docker + Docker Compose ---
	docker -v
	docker compose version

   ````


1. **Clone the repository**

   ```bash
   git clone https://github.com/bonoboprog/Chronogram.git
   cd Chronogram
   ```
   

2. **Install ngrok and start a tunnel**

   Install ngrok via Apt with the following command:


   ```bash
	curl -sSL https://ngrok-agent.s3.amazonaws.com/ngrok.asc \
  	  | sudo tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null \
  	  && echo "deb https://ngrok-agent.s3.amazonaws.com buster main" \
  	  | sudo tee /etc/apt/sources.list.d/ngrok.list \
          && sudo apt update \
          && sudo apt install ngrok
   ```

   Add your authtoken (If you don’t have an authtoken then [Sign up](https://dashboard.ngrok.com/signup) for a free account).

   ```bash
   ngrok config add-authtoken <YOUR_NGROK_AUTHTOKEN>
   ```
   
   Start an endpoint:

   ```bash
   ngrok http 80
   ```

3. **Start backend environment**

   ```bash
   ./setup_fresh_backend.sh
   ```

    This script will:

    - Stop and remove old containers
    - Build the backend (`build.sh`)
    - Start MySQL and Tomcat
    - Initialize the database with `schema.sql`

4. **Refresh backend after making code changes**

   ```bash
   ./refresh_tomcat_server.sh
   ```

5. **Set up the LLM with your API key 🔑**

   1. Go to [https://openrouter.ai](https://openrouter.ai)
   2. Click **Sign In** in the top-right corner and log in (you can use GitHub, Google, etc.)
   3. Go to the [API Keys Dashboard](https://openrouter.ai/keys)
   4. Click **"Create new key"**
   5. Copy your newly generated key
   6. Paste it into your `.env` file as follows:

      ```env
      LLM_API_KEY=your_openrouter_key_here
      ```

6. **Launch the app frontend**

   ```bash
   ionic build
   ionic serve
   ```
</details>

---

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

---

1. **After making EER/Workbench changes → forward engineer into MySQL container**

2. **Export the schema**

   ```bash
   ./export_schema.sh
   ```

3. **Version the schema**

   ```bash
   git add docker/init/schema.sql
   git commit -m "🔄 aggiornata struttura DB"
   git push
   ```

> \[!IMPORTANT]
> ⚠️ The `setup_fresh_backend.sh` script automatically loads `schema.sql` only on the first startup (empty volume).
>
> To force a reset:
>
> ```bash
> docker compose down -v
> ./setup_fresh_backend.sh
> ```

---

### 🤝 Collaborazione

Ogni collaboratore può:

```bash
git clone ...
./dev_setup.sh
```

Poi contribuire modificando:

* Backend (Java)
* Frontend (/mobile)
* Schema DB (`export_schema.sh`)

> \[!NOTE]
> 🧠 Importante: il file `schema.sql` è il punto di sincronizzazione per il DB!

---

## 👥 Contributors

| Name             | GitHub                                         |
| ---------------- | ---------------------------------------------- |
| Violeta Perez    | [@violetapd](https://github.com/violetapd)     |
| Paolo Simeone    | [@bonoboprog](https://github.com/bonoboprog)   |
| Giuseppe Alfieri | [@giusalfieri](https://github.com/giusalfieri) |

