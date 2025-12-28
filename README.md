# Spring PetClinic Customers Service 完整教學

> 一個專為初學者設計的 Spring Boot 微服務教學專案

## 這個專案是什麼？

想像你開了一間寵物醫院，你需要一個系統來管理：
- **飼主資料**：誰帶寵物來看病？住哪裡？電話幾號？
- **寵物資料**：叫什麼名字？什麼品種？幾歲了？

這個專案就是用來管理這些資料的「後端服務」。

---

## 目錄

1. [給完全新手的說明](#給完全新手的說明)
2. [專案架構圖解](#專案架構圖解)
3. [如何啟動專案](#如何啟動專案)
4. [Docker 容器化部署](#docker-容器化部署)
5. [Swagger API 文件](#swagger-api-文件)
6. [三層式架構詳解](#三層式架構詳解)
7. [程式碼逐行解說](#程式碼逐行解說)
8. [API 使用教學](#api-使用教學)
9. [單元測試教學](#單元測試教學)
10. [常見問題 FAQ](#常見問題-faq)

---

## 給完全新手的說明

### 什麼是「後端」？

你平常用的 APP 或網站，畫面上看到的按鈕、圖片、文字，那是「前端」。

但當你按下「送出」按鈕，資料要存到哪裡？從哪裡讀取？這些看不見的工作，就是「後端」在處理。

**打個比方：**
- 前端 = 餐廳的服務生（跟客人互動）
- 後端 = 廚房的廚師（真正做菜的人）

### 什麼是「微服務」？

傳統的程式，所有功能都塞在一起，就像一間什麼都賣的雜貨店。

微服務的概念是：**把功能拆開，各自獨立**。

- 「客戶管理」是一個服務
- 「預約管理」是另一個服務
- 「付款管理」又是另一個服務

**為什麼要這樣做？**

假設你的「預約功能」出 bug 了：
- 傳統做法：整個系統都要停下來修
- 微服務做法：只有預約服務需要修，其他功能照常運作

這就是為什麼大公司（Netflix、Amazon）都用微服務架構。

---

## 專案架構圖解

### 整體流程

```
使用者發送請求
      ↓
┌─────────────────────────────────────────────────────────────┐
│                      Controller 層                           │
│   （接收請求，就像餐廳櫃檯，負責接單）                          │
│   檔案：OwnerController.java                                  │
└─────────────────────────────────────────────────────────────┘
      ↓
┌─────────────────────────────────────────────────────────────┐
│                       Service 層                             │
│   （處理邏輯，就像廚房，負責做菜）                              │
│   檔案：OwnerService.java                                     │
│   這裡會檢查：電話有沒有重複？寵物數量超過上限了嗎？              │
└─────────────────────────────────────────────────────────────┘
      ↓
┌─────────────────────────────────────────────────────────────┐
│                     Repository 層                            │
│   （存取資料庫，就像倉庫管理員，負責拿食材）                     │
│   檔案：OwnerRepository.java                                  │
└─────────────────────────────────────────────────────────────┘
      ↓
┌─────────────────────────────────────────────────────────────┐
│                        資料庫                                 │
│   （儲存所有資料的地方）                                       │
│   這個專案用 H2 資料庫（一個輕量級的測試用資料庫）               │
└─────────────────────────────────────────────────────────────┘
```

### 檔案結構

```
spring-petclinic-customers-service/
│
├── pom.xml                          ← 專案設定檔（像是食譜的材料清單）
├── mvnw / mvnw.cmd                  ← Maven Wrapper（不需安裝 Maven）
├── Dockerfile                       ← Docker 映像建構設定
├── docker-compose.yml               ← Docker Compose 設定
├── .dockerignore                    ← Docker 忽略檔案清單
│
├── src/main/java/.../customers/
│   │
│   ├── model/                       ← 【資料模型】定義資料長什麼樣子
│   │   ├── Owner.java               ← 飼主的資料結構
│   │   ├── Pet.java                 ← 寵物的資料結構
│   │   └── PetType.java             ← 寵物種類（狗、貓、鳥...）
│   │
│   ├── repository/                  ← 【資料庫存取】跟資料庫溝通
│   │   ├── OwnerRepository.java
│   │   ├── PetRepository.java
│   │   └── PetTypeRepository.java
│   │
│   ├── service/                     ← 【業務邏輯】處理商業規則
│   │   └── OwnerService.java
│   │
│   ├── web/                         ← 【網頁層】處理 HTTP 請求
│   │   ├── OwnerController.java     ← API 入口點（含 OpenAPI 註解）
│   │   ├── GlobalExceptionHandler.java  ← 錯誤處理
│   │   ├── dto/                     ← 資料傳輸物件
│   │   │   ├── OwnerDTO.java
│   │   │   └── PetDTO.java
│   │   └── mapper/                  ← 資料轉換器
│   │       ├── OwnerMapper.java
│   │       └── PetMapper.java
│   │
│   ├── config/                      ← 【設定類別】
│   │   └── OpenApiConfig.java       ← Swagger/OpenAPI 設定
│   │
│   ├── exception/                   ← 【例外處理】定義錯誤類型
│   │   ├── ResourceNotFoundException.java
│   │   ├── BusinessRuleException.java
│   │   ├── DuplicateResourceException.java
│   │   └── ErrorResponse.java
│   │
│   ├── DataInitializer.java         ← 初始資料載入器
│   └── CustomersServiceApplication.java  ← 程式的起點
│
├── src/main/resources/
│   └── application.yml              ← 應用程式設定（含詳細中文註解）
│
└── src/test/java/.../customers/     ← 【測試程式】
    ├── repository/OwnerRepositoryTest.java
    ├── service/OwnerServiceTest.java
    └── web/OwnerControllerIntegrationTest.java
```

---

## 如何啟動專案

### 前置需求

1. **安裝 Java 17 或以上版本**
   - 到 [Adoptium](https://adoptium.net/) 下載
   - 安裝後，打開終端機輸入 `java -version` 確認

2. **Maven（可選）**
   - 本專案包含 Maven Wrapper（`mvnw`），不需要另外安裝 Maven
   - 如果想安裝，到 [Maven 官網](https://maven.apache.org/download.cgi) 下載

### 啟動步驟

```bash
# 1. 進入專案資料夾
cd spring-petclinic-customers-service

# 2. 編譯專案（使用 Maven Wrapper）
./mvnw clean compile        # Linux/Mac
mvnw.cmd clean compile      # Windows

# 3. 啟動服務
./mvnw spring-boot:run      # Linux/Mac
mvnw.cmd spring-boot:run    # Windows
```

看到這行訊息就代表成功了：
```
Started CustomersServiceApplication in X.XXX seconds
```

### 測試是否成功

打開瀏覽器，輸入：
```
http://localhost:8081/api/owners
```

如果看到 JSON 格式的資料，恭喜你成功了！

**更方便的方式：使用 Swagger UI**
```
http://localhost:8081/swagger-ui.html
```
這會開啟一個互動式的 API 測試介面！

### 查看資料庫

這個專案使用 H2 資料庫，你可以透過網頁介面查看：

1. 打開瀏覽器：`http://localhost:8081/h2-console`
2. JDBC URL 輸入：`jdbc:h2:mem:petclinic`
3. 使用者名稱：`sa`
4. 密碼：（留空）
5. 點擊 Connect

---

## Docker 容器化部署

### 什麼是 Docker？

Docker 是一個容器化平台，可以將應用程式和其依賴打包成一個「容器」。

**為什麼要用 Docker？**

| 問題 | Docker 如何解決 |
|------|----------------|
| 「在我電腦可以跑啊」 | 容器確保所有環境都一樣 |
| 部署太慢 | 容器啟動只需幾秒鐘 |
| 服務互相影響 | 每個容器都是獨立的 |
| 需要擴展 | 多開幾個容器就好 |

### 前置需求

安裝 Docker Desktop：
- [Windows](https://docs.docker.com/desktop/install/windows-install/)
- [Mac](https://docs.docker.com/desktop/install/mac-install/)
- [Linux](https://docs.docker.com/engine/install/)

確認安裝成功：
```bash
docker --version
docker-compose --version
```

### 使用 Docker Compose（推薦）

```bash
# 建構並啟動容器（第一次會比較久）
docker-compose up --build

# 在背景執行
docker-compose up -d --build

# 查看日誌
docker-compose logs -f

# 停止容器
docker-compose down
```

### 使用 Docker 指令

```bash
# 1. 建構映像
docker build -t petclinic/customers-service:latest .

# 2. 執行容器
docker run -d \
  --name petclinic-customers \
  -p 8081:8081 \
  petclinic/customers-service:latest

# 3. 查看容器狀態
docker ps

# 4. 查看日誌
docker logs -f petclinic-customers

# 5. 停止容器
docker stop petclinic-customers

# 6. 刪除容器
docker rm petclinic-customers
```

### 測試是否成功

容器啟動後，打開瀏覽器：

| 網址 | 說明 |
|------|------|
| http://localhost:8081/api/owners | API 端點 |
| http://localhost:8081/swagger-ui.html | Swagger UI |
| http://localhost:8081/actuator/health | 健康檢查 |

### Dockerfile 說明

本專案使用**多階段建構**（Multi-stage Build）：

```
┌─────────────────────────────────────┐
│  第一階段：建構階段 (builder)         │
│  - 使用 JDK 映像                     │
│  - 下載依賴                          │
│  - 編譯程式碼                        │
│  - 打包成 JAR                        │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│  第二階段：執行階段                   │
│  - 使用較小的 JRE 映像               │
│  - 只複製 JAR 檔案                   │
│  - 最終映像較小（約 300MB）           │
└─────────────────────────────────────┘
```

### 常用 Docker 指令

| 指令 | 說明 |
|------|------|
| `docker images` | 列出所有映像 |
| `docker ps` | 列出執行中的容器 |
| `docker ps -a` | 列出所有容器 |
| `docker logs <容器>` | 查看容器日誌 |
| `docker exec -it <容器> sh` | 進入容器 |
| `docker system prune` | 清理未使用的資源 |

---

## Swagger API 文件

### 什麼是 Swagger？

Swagger 是一套 API 文件工具，可以：
1. **自動產生文件**：從程式碼註解自動產生 API 文件
2. **互動式測試**：直接在網頁上測試 API，不需要用 curl
3. **標準格式**：產生 OpenAPI 3.0 規範的文件

### 存取方式

啟動服務後，打開瀏覽器：

| 網址 | 說明 |
|------|------|
| http://localhost:8081/swagger-ui.html | Swagger UI 互動介面 |
| http://localhost:8081/v3/api-docs | OpenAPI JSON 格式 |
| http://localhost:8081/v3/api-docs.yaml | OpenAPI YAML 格式 |

### Swagger UI 使用教學

1. **開啟 Swagger UI**：http://localhost:8081/swagger-ui.html

2. **查看 API 列表**：所有 API 會依照分類（Tag）顯示

3. **測試 API**：
   - 點擊想測試的 API（如 `GET /api/owners`）
   - 點擊 **Try it out** 按鈕
   - 填入參數（如果需要）
   - 點擊 **Execute** 執行
   - 查看回應結果

### API 分類

| 分類 | 說明 |
|------|------|
| 飼主管理 | 飼主的 CRUD 操作（新增、查詢、更新、刪除） |

### OpenAPI 註解說明

在 Controller 中使用的 OpenAPI 註解：

```java
@Tag(name = "飼主管理", description = "飼主的 CRUD 操作 API")
public class OwnerController {

    @Operation(summary = "查詢所有飼主", description = "取得所有飼主列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查詢成功"),
        @ApiResponse(responseCode = "404", description = "找不到資料")
    })
    @GetMapping
    public List<OwnerDTO> findAll() { ... }
}
```

| 註解 | 用途 |
|------|------|
| `@Tag` | API 分類標籤 |
| `@Operation` | 描述單一 API 端點 |
| `@ApiResponses` | 定義可能的回應狀態碼 |
| `@Parameter` | 描述參數 |

---

## 三層式架構詳解

### 為什麼要分層？

想像你在經營一間餐廳：

| 角色 | 程式中的對應 | 負責的事 |
|------|-------------|---------|
| 服務生 | Controller | 接收客人點餐、送餐給客人 |
| 廚師 | Service | 決定怎麼做菜、確認食材夠不夠 |
| 倉管 | Repository | 從冰箱拿食材、把食材放回去 |

**分層的好處：**

1. **各司其職**：服務生不用會做菜，廚師不用管客人
2. **容易修改**：換一個服務生，不影響廚房運作
3. **方便測試**：可以單獨測試廚師的手藝，不用真的開店

### 第一層：Model（資料模型）

這一層定義「資料長什麼樣子」。

就像你要記錄一個人的資料，你會寫：
- 姓名
- 電話
- 地址

在程式裡，我們用 `class`（類別）來定義：

```java
// Owner.java - 飼主的資料結構
@Entity                          // 告訴程式：這是一張資料表
@Table(name = "owners")          // 資料表的名字叫 "owners"
public class Owner {

    @Id                          // 這是主鍵（每筆資料的唯一識別碼）
    @GeneratedValue              // 自動產生編號
    private Integer id;          // 飼主編號

    private String firstName;    // 名字
    private String lastName;     // 姓氏
    private String address;      // 地址
    private String city;         // 城市
    private String telephone;    // 電話

    @OneToMany                   // 一對多關係：一個飼主可以有很多寵物
    private Set<Pet> pets;       // 這個飼主的所有寵物
}
```

**關於 `@Pattern` 驗證：**

```java
@Pattern(regexp = "\\d{10}", message = "電話必須是10位數字")
private String telephone;
```

這行的意思是：電話必須是 10 個數字，不能有其他字元。

為什麼要這樣做？因為使用者會輸入各種奇怪的格式：
- `0912-345-678`（有破折號）
- `+886912345678`（有國碼）
- `(02)2345-6789`（市話格式）

如果不統一格式，之後要查詢、統計都會很麻煩。

**資料驗證的原則：在資料進入資料庫之前就要擋掉錯誤的格式！**

### 第二層：Repository（資料存取）

這一層負責「跟資料庫溝通」。

```java
// OwnerRepository.java
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    // 根據姓氏查詢飼主
    @Query("SELECT owner FROM Owner owner WHERE owner.lastName LIKE :lastName%")
    List<Owner> findByLastName(@Param("lastName") String lastName);
}
```

**什麼是 JpaRepository？**

`JpaRepository` 是 Spring 提供的工具，它已經幫你寫好了基本的資料庫操作：

| 方法 | 功能 |
|-----|------|
| `save(entity)` | 新增或更新一筆資料 |
| `findById(id)` | 根據 ID 查詢 |
| `findAll()` | 查詢所有資料 |
| `delete(entity)` | 刪除一筆資料 |
| `count()` | 計算總共有幾筆 |

你只要「繼承」它，這些功能就自動可以用了！

**關於 N+1 查詢問題：**

這是一個很重要的效能觀念。

假設你要顯示 100 個飼主，每個飼主都要顯示他的寵物：

❌ **不好的做法：**
```java
List<Owner> owners = ownerRepository.findAll();  // 1 次查詢
for (Owner owner : owners) {
    owner.getPets();  // 每個飼主又查一次，共 100 次
}
// 總共：1 + 100 = 101 次查詢！
```

✅ **好的做法：**
```java
@Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.pets")
List<Owner> findAllWithPets();  // 1 次查詢就把所有資料抓出來
```

`LEFT JOIN FETCH` 會一次把飼主和寵物的資料都查出來，只需要 1 次查詢。

### 第三層：Service（業務邏輯）

這一層負責「處理商業規則」。

什麼是商業規則？就是你的應用程式「規定」要怎麼做的事情：
- 電話號碼不能重複
- 每個飼主最多養 10 隻寵物
- 寵物的生日不能是未來的日期

```java
// OwnerService.java
@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public Owner save(Owner owner) {
        // 規則 1：電話不能重複
        if (ownerRepository.existsByTelephone(owner.getTelephone())) {
            throw new DuplicateResourceException("此電話已被註冊");
        }

        // 規則 2：寵物名字不能重複
        Set<String> petNames = owner.getPets().stream()
            .map(Pet::getName)
            .collect(Collectors.toSet());

        if (petNames.size() != owner.getPets().size()) {
            throw new BusinessRuleException("寵物名字不能重複");
        }

        return ownerRepository.save(owner);
    }

    public void addPet(Integer ownerId, Pet pet) {
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("找不到此飼主"));

        // 規則 3：寵物生日不能是未來
        if (pet.getBirthDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("寵物生日不能是未來日期");
        }

        // 規則 4：最多養 10 隻
        if (owner.getPets().size() >= 10) {
            throw new BusinessRuleException("每個飼主最多只能登記 10 隻寵物");
        }

        owner.addPet(pet);
        ownerRepository.save(owner);
    }
}
```

**為什麼不把邏輯寫在 Controller？**

1. **可重用**：如果你有兩個 Controller 都需要「新增飼主」，邏輯只要寫一次
2. **好測試**：Service 可以單獨測試，不需要啟動整個網站
3. **好維護**：規則改了只要改 Service，不用到處找

### 第四層：Controller（網頁控制器）

這一層負責「接收 HTTP 請求」。

```java
// OwnerController.java
@RestController                      // 告訴 Spring：這是一個 REST API 控制器
@RequestMapping("/api/owners")       // 所有路徑都以 /api/owners 開頭
public class OwnerController {

    private final OwnerService ownerService;

    // GET /api/owners - 查詢所有飼主
    @GetMapping
    public ResponseEntity<List<OwnerDTO>> searchOwners(
            @RequestParam(required = false) String lastName) {

        List<Owner> owners = lastName != null
            ? ownerService.findByLastName(lastName)
            : ownerService.findAll();

        List<OwnerDTO> dtos = owners.stream()
            .map(OwnerMapper::toDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // POST /api/owners - 新增飼主
    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody OwnerDTO dto) {
        Owner owner = OwnerMapper.toEntity(dto);
        Owner saved = ownerService.save(owner);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(OwnerMapper.toDTO(saved));
    }

    // POST /api/owners/{ownerId}/pets - 新增寵物
    @PostMapping("/{ownerId}/pets")
    public ResponseEntity<PetDTO> addPet(
            @PathVariable Integer ownerId,
            @Valid @RequestBody PetDTO dto) {

        Pet pet = PetMapper.toEntity(dto);
        ownerService.addPet(ownerId, pet);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(PetMapper.toDTO(pet));
    }
}
```

**常用的註解說明：**

| 註解 | 意思 | 範例 |
|-----|------|------|
| `@GetMapping` | 處理 GET 請求（查詢） | 查詢所有飼主 |
| `@PostMapping` | 處理 POST 請求（新增） | 新增一個飼主 |
| `@PutMapping` | 處理 PUT 請求（更新） | 修改飼主資料 |
| `@DeleteMapping` | 處理 DELETE 請求（刪除） | 刪除一個飼主 |
| `@PathVariable` | 從網址取得參數 | `/owners/123` 中的 `123` |
| `@RequestParam` | 從查詢字串取得參數 | `?lastName=王` 中的 `王` |
| `@RequestBody` | 從請求內容取得 JSON 資料 | 新增時傳入的飼主資料 |
| `@Valid` | 自動驗證資料格式 | 檢查電話是否為 10 位數 |

### 什麼是 DTO？為什麼需要它？

DTO = Data Transfer Object（資料傳輸物件）

**問題：為什麼不直接把 Entity 傳給前端？**

假設你的 `Owner` 裡面有密碼欄位：

```java
public class Owner {
    private Integer id;
    private String firstName;
    private String password;  // 密碼！不能給前端看！
}
```

如果直接回傳 Entity，密碼就會被傳出去，這是很危險的！

**解決方案：用 DTO 只傳需要的欄位**

```java
// OwnerDTO.java - 只有需要傳輸的欄位
public class OwnerDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    // 沒有 password！
}
```

**另一個好處：避免循環參考**

```java
public class Owner {
    private Set<Pet> pets;  // Owner 有 Pet
}

public class Pet {
    private Owner owner;    // Pet 有 Owner
}
```

當程式把 Owner 轉成 JSON 時：
1. 轉換 Owner
2. 發現有 pets，開始轉換 Pet
3. 發現 Pet 有 owner，又開始轉換 Owner
4. 又發現有 pets...（無限循環！程式爆掉）

用 DTO 可以控制要轉換哪些欄位，避免這個問題。

---

## 程式碼逐行解說

### Owner.java 完整解說

```java
package org.springframework.samples.petclinic.customers.model;

// 匯入需要的類別
import jakarta.persistence.*;           // JPA 相關的註解
import jakarta.validation.constraints.*; // 驗證用的註解
import lombok.*;                         // 減少樣板程式碼

import java.util.HashSet;
import java.util.Set;

@Entity                                 // ① 標記這是一個 JPA 實體（對應資料表）
@Table(name = "owners")                 // ② 指定資料表名稱
@Getter @Setter                         // ③ Lombok：自動產生 getter/setter
@NoArgsConstructor                      // ④ Lombok：自動產生無參數建構子
public class Owner {

    @Id                                 // ⑤ 這是主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ⑥ 自動產生 ID
    private Integer id;

    @Column(name = "first_name")        // ⑦ 對應資料庫欄位名稱
    @NotBlank(message = "名字不能為空") // ⑧ 驗證：不能是空白
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "姓氏不能為空")
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "telephone")
    @Pattern(regexp = "\\d{10}", message = "電話必須是10位數字")  // ⑨ 正規表達式驗證
    private String telephone;

    @OneToMany(                         // ⑩ 一對多關係
        cascade = CascadeType.ALL,      // ⑪ 連鎖操作：刪飼主時一起刪寵物
        mappedBy = "owner",             // ⑫ 關聯的欄位名稱
        fetch = FetchType.LAZY          // ⑬ 延遲載入：用到才查詢
    )
    private Set<Pet> pets = new HashSet<>();

    // 建構子
    public Owner(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // 新增寵物的方法（維護雙向關聯）
    public void addPet(Pet pet) {
        pets.add(pet);           // 把寵物加到清單
        pet.setOwner(this);      // 設定寵物的主人是自己
    }
}
```

**重點說明：**

| 編號 | 註解/程式碼 | 用途 |
|------|------------|------|
| ① | `@Entity` | 告訴 JPA 這個 class 要對應到資料表 |
| ② | `@Table(name = "owners")` | 指定資料表名稱為 `owners` |
| ③ | `@Getter @Setter` | Lombok 自動產生 `getFirstName()`、`setFirstName()` 等方法 |
| ④ | `@NoArgsConstructor` | 自動產生 `public Owner() {}` |
| ⑤ | `@Id` | 標記這個欄位是主鍵 |
| ⑥ | `@GeneratedValue` | ID 會自動產生（1, 2, 3...） |
| ⑦ | `@Column` | 指定對應的資料庫欄位名稱 |
| ⑧ | `@NotBlank` | 驗證不能是空白或 null |
| ⑨ | `@Pattern` | 用正規表達式驗證格式 |
| ⑩ | `@OneToMany` | 一個飼主對應多隻寵物 |
| ⑪ | `cascade` | 對飼主的操作會連帶影響寵物 |
| ⑫ | `mappedBy` | 關聯是由 Pet 的 `owner` 欄位維護 |
| ⑬ | `LAZY` | 延遲載入，查飼主時不會自動查寵物 |

### OwnerService.java 完整解說

```java
package org.springframework.samples.petclinic.customers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// ... 其他 import

@Slf4j                              // ① Lombok：自動產生 log 物件
@Service                            // ② 標記這是一個 Service 元件
@RequiredArgsConstructor            // ③ 自動產生建構子注入
@Transactional                      // ④ 整個 class 都在交易中執行
public class OwnerService {

    private final OwnerRepository ownerRepository;  // ⑤ 注入 Repository

    /**
     * 儲存飼主
     */
    public Owner save(Owner owner) {
        // ⑥ 記錄日誌
        log.info("嘗試儲存飼主：{} {}", owner.getFirstName(), owner.getLastName());

        // ⑦ 業務規則：電話不能重複
        if (owner.getTelephone() != null &&
            ownerRepository.existsByTelephone(owner.getTelephone())) {
            throw new DuplicateResourceException("此電話已被註冊");
        }

        // ⑧ 業務規則：寵物名字不能重複
        if (owner.getPets() != null && !owner.getPets().isEmpty()) {
            Set<String> petNames = owner.getPets().stream()
                .map(Pet::getName)
                .collect(Collectors.toSet());

            if (petNames.size() != owner.getPets().size()) {
                throw new BusinessRuleException("寵物名字不能重複");
            }
        }

        // ⑨ 執行儲存
        try {
            Owner saved = ownerRepository.save(owner);
            log.info("飼主儲存成功，ID：{}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("儲存飼主失敗：{}", owner, e);  // ⑩ 記錄錯誤
            throw e;
        }
    }

    /**
     * 新增寵物
     */
    public void addPet(Integer ownerId, Pet pet) {
        // ⑪ 查詢飼主，找不到就拋出例外
        Owner owner = ownerRepository.findByIdWithPets(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("找不到此飼主"));

        // ⑫ 業務規則：生日不能是未來
        if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("寵物生日不能是未來日期");
        }

        // ⑬ 業務規則：最多 10 隻寵物
        if (owner.getPets().size() >= 10) {
            throw new BusinessRuleException("每個飼主最多只能登記 10 隻寵物");
        }

        // ⑭ 業務規則：名字不能重複
        boolean nameExists = owner.getPets().stream()
            .anyMatch(p -> p.getName().equals(pet.getName()));
        if (nameExists) {
            throw new BusinessRuleException("寵物名字不能重複");
        }

        // ⑮ 執行新增
        owner.addPet(pet);
        ownerRepository.save(owner);
        log.info("成功為飼主 {} 新增寵物 {}", ownerId, pet.getName());
    }

    /**
     * 根據 ID 查詢（使用快取）
     */
    @Transactional(readOnly = true)    // ⑯ 唯讀交易，效能較好
    @Cacheable(value = "owners", key = "#id")  // ⑰ 快取結果
    public Owner findById(Integer id) {
        return ownerRepository.findByIdWithPets(id)
            .orElseThrow(() -> new ResourceNotFoundException("找不到此飼主"));
    }
}
```

**重點說明：**

| 編號 | 程式碼 | 說明 |
|------|--------|------|
| ① | `@Slf4j` | 自動產生 `log` 變數，可以用 `log.info()` 記錄日誌 |
| ② | `@Service` | 告訴 Spring 這是一個 Service，會被自動管理 |
| ③ | `@RequiredArgsConstructor` | 自動產生建構子，注入 `final` 欄位 |
| ④ | `@Transactional` | 資料庫交易：全部成功才儲存，有錯誤就全部取消 |
| ⑤ | `private final` | 用建構子注入，比 `@Autowired` 更推薦 |
| ⑥ | `log.info()` | 記錄一般資訊 |
| ⑦-⑭ | 業務規則 | 檢查各種條件，不符合就拋出例外 |
| ⑮ | `owner.addPet(pet)` | 呼叫 Owner 的方法，維護雙向關聯 |
| ⑯ | `readOnly = true` | 告訴資料庫這個操作只讀不寫，可以優化效能 |
| ⑰ | `@Cacheable` | 快取結果，下次查詢相同 ID 就不用再查資料庫 |

---

## API 使用教學

### 測試方式

有兩種方式可以測試 API：

1. **Swagger UI（推薦）**：打開 http://localhost:8081/swagger-ui.html
   - 圖形化介面，操作直覺
   - 可以直接看到參數說明和回應格式

2. **curl 指令**：適合自動化測試或 CLI 使用者

### 使用 curl 測試（終端機）

```bash
# 1. 查詢所有飼主
curl http://localhost:8081/api/owners

# 2. 根據姓氏查詢
curl "http://localhost:8081/api/owners?lastName=王"

# 3. 查詢單一飼主
curl http://localhost:8081/api/owners/1

# 4. 新增飼主
curl -X POST http://localhost:8081/api/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "小明",
    "lastName": "王",
    "address": "台北市信義區",
    "city": "台北市",
    "telephone": "0912345678"
  }'

# 5. 為飼主新增寵物
curl -X POST http://localhost:8081/api/owners/1/pets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "小黑",
    "birthDate": "2021-05-10"
  }'

# 6. 更新飼主資料
curl -X PUT http://localhost:8081/api/owners/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "大明",
    "lastName": "王",
    "address": "新北市板橋區",
    "city": "新北市",
    "telephone": "0912345678"
  }'

# 7. 刪除飼主
curl -X DELETE http://localhost:8081/api/owners/1

# 8. 分頁查詢
curl "http://localhost:8081/api/owners/page?page=0&size=10&sort=lastName,asc"
```

### API 列表

| 方法 | 路徑 | 說明 | 範例 |
|------|------|------|------|
| GET | `/api/owners` | 查詢所有飼主 | |
| GET | `/api/owners?lastName=王` | 根據姓氏查詢 | 查詢姓王的飼主 |
| GET | `/api/owners/{id}` | 查詢單一飼主 | `/api/owners/1` |
| POST | `/api/owners` | 新增飼主 | 見上方範例 |
| PUT | `/api/owners/{id}` | 更新飼主 | `/api/owners/1` |
| DELETE | `/api/owners/{id}` | 刪除飼主 | `/api/owners/1` |
| POST | `/api/owners/{id}/pets` | 新增寵物 | `/api/owners/1/pets` |
| GET | `/api/owners/page` | 分頁查詢 | `?page=0&size=10` |

### HTTP 狀態碼說明

| 狀態碼 | 意思 | 什麼時候會出現 |
|--------|------|---------------|
| 200 OK | 成功 | 查詢、更新成功 |
| 201 Created | 已建立 | 新增成功 |
| 204 No Content | 無內容 | 刪除成功 |
| 400 Bad Request | 請求錯誤 | 資料格式不對（如電話不是 10 位） |
| 404 Not Found | 找不到 | 查詢的 ID 不存在 |
| 409 Conflict | 衝突 | 電話號碼已被使用 |
| 500 Internal Server Error | 伺服器錯誤 | 程式出 bug 了 |

---

## 單元測試教學

### 為什麼要寫測試？

想像你在考試：
- 沒有測試 = 交卷後才知道自己錯了
- 有測試 = 每寫一題就知道對不對

**測試的好處：**
1. 提早發現問題
2. 修改程式碼時，確保沒有弄壞原本的功能
3. 當作程式的說明書（看測試就知道程式該怎麼用）

### 測試的三個階段：Given-When-Then

```java
@Test
void testAddPet_DuplicateName() {
    // Given（準備）：飼主已經有一隻叫「小白」的狗
    Owner owner = new Owner("小明", "王");
    Pet existingPet = new Pet("小白", LocalDate.of(2020, 5, 10));
    owner.addPet(existingPet);

    when(ownerRepository.findByIdWithPets(1)).thenReturn(Optional.of(owner));

    // When（執行）：嘗試新增另一隻叫「小白」的貓
    Pet duplicatePet = new Pet("小白", LocalDate.of(2021, 3, 15));

    // Then（驗證）：應該要報錯
    assertThatThrownBy(() -> ownerService.addPet(1, duplicatePet))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessageContaining("寵物名字不能重複");
}
```

### 三種測試類型

#### 1. Repository 測試

測試資料庫操作是否正確。

```java
@DataJpaTest  // 只啟動資料庫相關的元件，測試快速
class OwnerRepositoryTest {

    @Autowired
    private OwnerRepository repository;

    @Test
    @DisplayName("測試：根據姓氏模糊查詢")
    void testFindByLastName() {
        // Given
        repository.save(new Owner("小明", "王", "台北市"));
        repository.save(new Owner("小華", "王", "新北市"));
        repository.save(new Owner("大偉", "李", "桃園市"));

        // When
        List<Owner> result = repository.findByLastName("王");

        // Then
        assertThat(result).hasSize(2);  // 應該找到 2 個姓王的
    }
}
```

#### 2. Service 測試

測試業務邏輯是否正確。使用 Mock 模擬 Repository。

```java
@ExtendWith(MockitoExtension.class)  // 使用 Mockito 框架
class OwnerServiceTest {

    @Mock                             // 模擬的 Repository
    private OwnerRepository ownerRepository;

    @InjectMocks                      // 要測試的 Service
    private OwnerService ownerService;

    @Test
    @DisplayName("測試：寵物生日不能是未來")
    void testAddPet_FutureBirthDate() {
        // Given
        Owner owner = new Owner("小明", "王");
        when(ownerRepository.findByIdWithPets(1)).thenReturn(Optional.of(owner));

        // When：新增一隻生日是明天的寵物
        Pet futurePet = new Pet("時光機", LocalDate.now().plusDays(1));

        // Then：應該報錯
        assertThatThrownBy(() -> ownerService.addPet(1, futurePet))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("寵物生日不能是未來日期");
    }
}
```

**什麼是 Mock？**

Mock 就是「假的」物件。

當你測試 Service 時，你不想真的去查資料庫（那樣太慢了），所以用 Mock 來「假裝」有資料庫。

```java
// 告訴 Mock：當呼叫 findByIdWithPets(1) 時，回傳這個 owner
when(ownerRepository.findByIdWithPets(1)).thenReturn(Optional.of(owner));
```

#### 3. Controller 測試（整合測試）

測試完整的 HTTP 請求流程。

```java
@SpringBootTest                      // 啟動完整的應用程式
@AutoConfigureMockMvc                // 自動設定 MockMvc
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;         // 模擬 HTTP 請求

    @Autowired
    private ObjectMapper objectMapper;  // JSON 轉換器

    @Test
    @DisplayName("測試：新增飼主的完整流程")
    void testCreateOwner() throws Exception {
        OwnerDTO dto = new OwnerDTO();
        dto.setFirstName("小美");
        dto.setLastName("陳");
        dto.setTelephone("0912345670");

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())           // 期望 201
            .andExpect(jsonPath("$.id").exists())      // 期望有 id
            .andExpect(jsonPath("$.firstName").value("小美"));  // 期望名字正確
    }

    @Test
    @DisplayName("測試：電話格式錯誤應回傳 400")
    void testCreateOwner_InvalidTelephone() throws Exception {
        OwnerDTO dto = new OwnerDTO();
        dto.setFirstName("小美");
        dto.setLastName("陳");
        dto.setTelephone("12345");  // 錯誤格式！只有 5 位

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())        // 期望 400
            .andExpect(jsonPath("$.errors").exists()); // 期望有錯誤訊息
    }
}
```

### 執行測試

```bash
# 執行所有測試（使用 Maven Wrapper）
./mvnw test              # Linux/Mac
mvnw.cmd test            # Windows

# 執行特定測試類別
./mvnw test -Dtest=OwnerServiceTest

# 執行特定測試方法
./mvnw test -Dtest=OwnerServiceTest#testAddPet_DuplicateName
```

---

## 常見問題 FAQ

### Q1：什麼是 Spring Boot？

Spring Boot 是一個 Java 框架，讓你可以快速建立應用程式。

它幫你處理了很多繁瑣的設定，你只要專注在寫業務邏輯就好。

### Q2：@Autowired 和建構子注入有什麼差別？

```java
// 方式一：@Autowired（不推薦）
@Autowired
private OwnerRepository ownerRepository;

// 方式二：建構子注入（推薦）
private final OwnerRepository ownerRepository;

public OwnerService(OwnerRepository ownerRepository) {
    this.ownerRepository = ownerRepository;
}

// 方式三：用 Lombok 簡化（最推薦）
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
}
```

建構子注入的好處：
1. 欄位可以是 `final`，不會被意外修改
2. 更容易寫測試
3. 如果忘記注入，編譯時就會報錯

### Q3：什麼是 JPA？什麼是 Hibernate？

- **JPA**（Java Persistence API）：一套規範，定義了「Java 程式應該怎麼跟資料庫溝通」
- **Hibernate**：JPA 的一種實作，真正幫你執行 SQL 的工具

就像「USB」是規範，「SanDisk 隨身碟」是實作。

### Q4：為什麼要用 Lombok？

Lombok 可以自動產生重複的程式碼。

沒有 Lombok：
```java
public class Owner {
    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // 還有 lastName、address、city、telephone...
    // 每個欄位都要寫 getter 和 setter，超級煩！
}
```

有 Lombok：
```java
@Getter @Setter
public class Owner {
    private String firstName;
    private String lastName;
    private String address;
    // 完成！Lombok 會自動產生所有 getter/setter
}
```

### Q5：Optional 是什麼？

`Optional` 是一個容器，用來處理「可能沒有值」的情況。

```java
// 舊的寫法：可能會 NullPointerException
Owner owner = ownerRepository.findById(1);
if (owner != null) {
    System.out.println(owner.getFirstName());
}

// 用 Optional 的寫法：更安全
Optional<Owner> ownerOpt = ownerRepository.findById(1);
ownerOpt.ifPresent(owner -> System.out.println(owner.getFirstName()));

// 或者：找不到就拋出例外
Owner owner = ownerRepository.findById(1)
    .orElseThrow(() -> new ResourceNotFoundException("找不到"));
```

### Q6：Stream API 是什麼？

Stream 是 Java 8 引入的功能，讓你可以用更簡潔的方式處理集合。

```java
// 傳統寫法
List<String> names = new ArrayList<>();
for (Pet pet : pets) {
    names.add(pet.getName());
}

// Stream 寫法
List<String> names = pets.stream()
    .map(Pet::getName)
    .collect(Collectors.toList());
```

常用的 Stream 操作：
- `filter()`：過濾
- `map()`：轉換
- `collect()`：收集結果
- `forEach()`：逐一處理
- `anyMatch()`：是否有任何一個符合條件
- `count()`：計算數量

---

## 延伸學習

學完這個專案後，你可以繼續學習：

1. **Spring Security**：處理登入、權限
2. **Spring Cloud**：微服務進階（服務發現、負載平衡）
3. **Docker**：容器化部署
4. **Kubernetes**：容器編排
5. **Redis**：快取
6. **RabbitMQ / Kafka**：訊息佇列

---

## 專案功能總覽

這個專案包含以下功能：

| 功能 | 說明 |
|------|------|
| REST API | 飼主與寵物的 CRUD 操作 |
| 資料驗證 | 使用 Bean Validation 驗證輸入 |
| 例外處理 | 全域例外處理，統一錯誤回應格式 |
| API 文件 | Swagger UI 互動式文件 |
| 健康檢查 | Spring Boot Actuator 端點 |
| 快取 | 使用 Spring Cache 快取查詢結果 |
| 單元測試 | Repository、Service、Controller 測試 |
| 容器化 | Docker / Docker Compose 支援 |

### 技術棧

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Boot Actuator（健康檢查）
- H2 Database（開發環境）
- Lombok
- SpringDoc OpenAPI（Swagger）
- JUnit 5 + Mockito
- Docker / Docker Compose

---

## 結語

恭喜你看完這份教學！

記住幾個重點：
1. **先搞清楚資料結構，程式邏輯自然就清楚了**
2. **三層式架構：Controller → Service → Repository**
3. **業務邏輯放在 Service，不要放在 Controller**
4. **用 DTO 傳輸資料，不要直接傳 Entity**
5. **寫測試！沒寫測試的程式碼就像開車不繫安全帶**
6. **用 Swagger 產生 API 文件，前後端協作更順暢**

最重要的是：**能用簡單的方式解決問題，就不要用複雜的方式**。

Happy Coding!
