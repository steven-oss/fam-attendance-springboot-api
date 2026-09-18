# fam-attendance-springboot-api

Spring Boot 3.4 + Java 17 + **Aiven PostgreSQL** 的考勤 API 後端專案。

## 需求

- **Java 17**（本機若出現 `Unable to locate a Java Runtime`，見下方「Java 17」）
- **Maven 3.9+**（或使用專案內 `./mvnw`）
- **Aiven** 上的 PostgreSQL 服務（本專案預設走雲端 DB，不用本機 Docker）

### Java 17（macOS + Homebrew）

Homebrew 的 `openjdk@17` 不會自動註冊到系統，執行 Maven 前請先：

```bash
source scripts/java-env.sh
java -version   # 應顯示 17.x
```

若要**每次開終端都生效**，可加入 `~/.zshrc`：

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="$JAVA_HOME/bin:$PATH"
```

或用 Temurin 安裝檔（需輸入 macOS 密碼）：`brew install --cask temurin@17`

## 快速開始

### 1. 設定 Aiven 連線

在 Aiven Console 開啟 **Connection information**，複製 Host、Port、Database、User、Password。

```bash
cp .env.example .env
# 編輯 .env，填入 Aiven 的連線資訊
```

`.env` 已在 `.gitignore`，**請勿把密碼 commit 進 Git**。

應用啟動時會自動讀取專案根目錄的 `.env`（`spring.config.import`）。

| 變數 | 說明 |
|------|------|
| `POSTGRES_HOST` | Aiven Host |
| `POSTGRES_PORT` | 通常為 `27618` 等非 5432 埠 |
| `POSTGRES_DB` | 例如 `defaultdb` |
| `POSTGRES_USER` | 例如 `avnadmin` |
| `POSTGRES_PASSWORD` | Aiven 產生的密碼 |
| `POSTGRES_SSL_MODE` | Aiven 必填，使用 `require` |

JDBC 會組成：

`jdbc:postgresql://{host}:{port}/{db}?sslmode=require`

### 2. 執行應用

```bash
source scripts/java-env.sh
./mvnw spring-boot:run
```

`dev` profile 下 JPA 使用 `ddl-auto: update`（在 Aiven 上自動建表/更新 schema，開發用）。

### 3. 驗證

```bash
curl http://localhost:8080/health
# {"status":"UP"}
```

## Postman

先確保應用已啟動（`./mvnw spring-boot:run`），再在 Postman 測 API。

### 方式 A：匯入專案內 Collection（建議）

1. Postman → **Import** → 選 `postman/fam-attendance-api.postman_collection.json`
2. 再 Import → `postman/fam-attendance-local.postman_environment.json`
3. 右上角 Environment 選 **FAM Attendance - Local**
4. 執行 **Health → GET Health**，應得到 `{"status":"UP"}`

Collection 使用變數 `{{baseUrl}}`，預設 `http://localhost:8080`（與 `.env` 的 `SERVER_PORT` 一致時不需改）。

### 方式 B：從 OpenAPI 匯入（之後新增 API 會自動出現在文件）

1. 應用啟動後，Postman → **Import** → **Link**
2. 貼上：`http://localhost:8080/v3/api-docs`
3. 匯入後可一併產生請求

瀏覽器也可開 Swagger UI：`http://localhost:8080/swagger-ui.html`

### 手動建立單一請求

| 欄位 | 值 |
|------|-----|
| Method | `GET` |
| URL | `http://localhost:8080/health` |
| Headers | 無（目前 API 不需 Token） |

若 `curl` / Postman 連不上，代表後端還沒起來或 port 不對，請看 `spring-boot:run` 終端機的錯誤訊息。

## 本機 Docker PostgreSQL（可選）

若改回本機開發，可改用 `docker compose up -d`，並在 `.env` 改為 `localhost:5432` 等本機參數。預設流程以 **Aiven** 為主。

## 測試

使用 H2 記憶體資料庫（`test` profile），**不會**連 Aiven：

```bash
./mvnw test
```

## 專案結構

```
src/main/java/com/fam/attendance/
├── FamAttendanceApplication.java
├── config/          # Spring 設定（OpenAPI 等）
├── controller/      # HTTP 入口，只負責參數與回應
├── service/         # 業務邏輯
├── repository/      # 資料庫存取（Spring Data JPA）
├── entity/          # JPA 實體 ↔ 資料表
└── dto/             # API 回傳物件（不直接暴露 Entity）
```

請求流向：**Controller → Service → Repository → DB**，Entity 只在 Service 與 Repository 之間使用，對外回傳 **DTO**。

## 常用指令

```bash
./mvnw spring-boot:run
./mvnw clean package
./mvnw test
```

## 技術棧

- Spring Boot 3.4.5
- Java 17
- Spring Data JPA
- PostgreSQL（Aiven，SSL）
- Spring Validation、DevTools

## 安全提醒

若密碼曾出現在截圖或聊天中，建議到 Aiven Console **重設 database password**，並只放在本機 `.env`。
