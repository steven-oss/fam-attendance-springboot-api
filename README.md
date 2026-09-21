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

應用啟動後，在瀏覽器開啟 Swagger UI，或請求家人 API：

```bash
curl -s "http://localhost:8080/fam-attendance/gender-options"
```

應回傳性別選項 JSON 陣列。

## API 一覽

Base URL：`http://localhost:8080`（可由 `.env` 的 `SERVER_PORT` 調整）

| 方法 | 路徑 | 成功狀態 | 說明 |
|------|------|----------|------|
| GET | `/fam-attendance/page?page=1&pageSize=10` | 200 | 家人分頁列表 |
| GET | `/fam-attendance/gender-options` | 200 | 性別下拉選項（code / label） |
| POST | `/fam-attendance` | **201** | 新增家人 |
| GET | `/fam-attendance/{id}` | 200 | 單筆查詢 |
| PUT | `/fam-attendance/{id}` | 200 | 整筆更新（非 PATCH） |
| DELETE | `/fam-attendance/{id}` | **204** | 刪除（無 response body） |

查無資料時回 **404**（`NotFoundException`）。`name` 驗證失敗回 **400**（`@Valid`）。

### 請求 / 回應範例

**POST / PUT** 請求 body（`Content-Type: application/json`）：

```json
{
  "name": "王小明",
  "gender": "男",
  "phone": "0912345678",
  "address": "台北市"
}
```

- `name` 必填（`@NotBlank`）；其餘欄位可為 null。
- POST / PUT **不要**在 body 送 `id`（id 由資料庫產生或 URL 指定）。
- `gender` 建議送 **code**（`M` / `F` / `O`），與 `GET /fam-attendance/gender-options` 一致；亦可送中文 label（`男` / `女` / `其他`）。

**GET `/fam-attendance/gender-options`（200）** 範例：

```json
[
  { "code": "M", "label": "男" },
  { "code": "F", "label": "女" },
  { "code": "O", "label": "其他" }
]
```

**POST 成功回應（201）** — `FamilyMemberDto`：

```json
{
  "id": 1,
  "name": "王小明",
  "gender": "男",
  "phone": "0912345678",
  "address": "台北市"
}
```

**GET 分頁（200）** — `PageResponse`：

```json
{
  "records": [ { "id": 1, "name": "...", "gender": "...", "phone": "...", "address": "..." } ],
  "total": 1,
  "page": 1,
  "pageSize": 10,
  "totalPages": 1
}
```

### curl 範例

```bash
# 新增
curl -s -X POST http://localhost:8080/fam-attendance \
  -H 'Content-Type: application/json' \
  -d '{"name":"王小明","gender":"男","phone":"0912345678","address":"台北市"}'

# 查詢 id=1
curl -s http://localhost:8080/fam-attendance/1

# 刪除 id=1（成功無 body）
curl -s -o /dev/null -w "%{http_code}\n" -X DELETE http://localhost:8080/fam-attendance/1
```

## Postman

先確保應用已啟動（`./mvnw spring-boot:run`），再在 Postman 測 API。

### 方式 A：匯入專案內 Collection（建議）

1. Postman → **Import** → 選 `postman/fam-attendance-api.postman_collection.json`
2. 再 Import → `postman/fam-attendance-local.postman_environment.json`
3. 右上角 Environment 選 **FAM Attendance - Local**
4. **FamAttendance** 資料夾建議順序：
   - **GET 性別選項（下拉）**
   - **POST 新增家人** → 201；Collection 的 Tests 會把回應 `id` 寫入 `{{memberId}}`
   - **GET 家人 by id** → 200
   - **PUT 更新家人** → 200
   - **DELETE 刪除家人** → 204；再 GET 同 id 應 404

變數：

| 變數 | 說明 |
|------|------|
| `{{baseUrl}}` | 預設 `http://localhost:8080` |
| `{{memberId}}` | 要操作的家人 id（POST 成功後可自動更新） |

### 方式 B：從 OpenAPI 匯入（之後新增 API 會自動出現在文件）

1. 應用啟動後，Postman → **Import** → **Link**
2. 貼上：`http://localhost:8080/v3/api-docs`
3. 匯入後可一併產生請求

瀏覽器也可開 Swagger UI：`http://localhost:8080/swagger-ui.html`

### 手動建立單一請求

| 欄位 | 值 |
|------|-----|
| Method | `GET` |
| URL | `http://localhost:8080/fam-attendance/gender-options` |
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
├── controller/      # HTTP 入口（FamAttendanceController）
├── service/         # 業務邏輯（FamilyMemberService）
├── repository/      # Spring Data JPA（FamilyMemberRepository）
├── entity/          # JPA 實體（FamilyMember，Lombok）
│   └── enums/       # Gender（下拉選項，非資料表）
├── dto/             # Request / Response（Create、Update、FamilyMemberDto、PageResponse）
└── exception/       # NotFoundException → HTTP 404
```

資料表：`family_member`（欄位：`id`, `name`, `gender`, `phone`, `address`）。

請求流向：**Controller → Service → Repository → DB**；對外 JSON 使用 **DTO**，不直接暴露 Entity。

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
- Lombok（Entity）
- SpringDoc OpenAPI（Swagger UI、`/v3/api-docs`）

## 安全提醒

若密碼曾出現在截圖或聊天中，建議到 Aiven Console **重設 database password**，並只放在本機 `.env`。
