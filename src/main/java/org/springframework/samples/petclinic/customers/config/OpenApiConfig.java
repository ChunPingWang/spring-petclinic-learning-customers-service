package org.springframework.samples.petclinic.customers.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger 設定類別
 *
 * 什麼是 Swagger / OpenAPI？
 *
 * Swagger 是一套 API 文件工具，可以：
 * 1. 自動產生 API 文件（從程式碼註解）
 * 2. 提供互動式 UI 測試 API
 * 3. 產生 OpenAPI 規範的 JSON/YAML
 *
 * OpenAPI 是 API 描述的標準規範（前身是 Swagger Specification）。
 * 現在 Swagger 指的是工具，OpenAPI 指的是規範。
 *
 * 存取方式：
 * - Swagger UI: http://localhost:8081/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8081/v3/api-docs
 * - OpenAPI YAML: http://localhost:8081/v3/api-docs.yaml
 *
 * 為什麼需要 API 文件？
 *
 * 1. 前後端協作：
 *    前端開發者可以查看 API 規格，不需要問後端
 *
 * 2. 測試便利：
 *    Swagger UI 可以直接測試 API，不需要 curl 或 Postman
 *
 * 3. 自動保持同步：
 *    文件從程式碼產生，不會與實際 API 脫節
 *
 * 4. 標準格式：
 *    OpenAPI 規範可以被其他工具使用（如產生客戶端程式碼）
 *
 * @author Spring PetClinic
 */
@Configuration  // 標記為設定類別，Spring 會自動載入
public class OpenApiConfig {

    /**
     * 自訂 OpenAPI 文件資訊
     *
     * @Bean: 告訴 Spring 這個方法回傳的物件要註冊為 Bean
     *
     * @return OpenAPI 設定物件
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            // API 基本資訊
            .info(new Info()
                // API 標題
                .title("Spring PetClinic Customers Service API")
                // API 版本
                .version("1.0.0")
                // API 描述（支援 Markdown）
                .description("""
                    寵物診所客戶服務 API

                    這個 API 提供以下功能：
                    - 飼主（Owner）的 CRUD 操作
                    - 寵物（Pet）管理
                    - 寵物類型（PetType）查詢

                    ## 認證

                    目前此 API 不需要認證。

                    ## 錯誤處理

                    API 使用標準 HTTP 狀態碼：
                    - 200: 成功
                    - 201: 建立成功
                    - 400: 請求錯誤（驗證失敗、業務規則錯誤）
                    - 404: 找不到資源
                    - 409: 資源衝突（重複的電話號碼）
                    - 500: 伺服器錯誤
                    """)
                // 聯絡資訊
                .contact(new Contact()
                    .name("Spring PetClinic Team")
                    .url("https://github.com/spring-petclinic")
                    .email("petclinic@example.com"))
                // 授權資訊
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            // 伺服器列表
            .servers(List.of(
                new Server()
                    .url("http://localhost:8081")
                    .description("開發環境"),
                new Server()
                    .url("https://api.petclinic.example.com")
                    .description("生產環境")
            ));
    }
}
