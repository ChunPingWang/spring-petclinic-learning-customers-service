package org.springframework.samples.petclinic.customers.exception;

/**
 * 資源找不到例外
 *
 * 當使用者請求的資源不存在時拋出此例外。
 * 例如：查詢不存在的飼主 ID。
 *
 * 對應 HTTP 狀態碼：404 Not Found
 *
 * 為什麼要自訂例外？
 *
 * 1. 語意清楚：
 *    ResourceNotFoundException 比一般的 RuntimeException
 *    更能清楚表達「找不到資源」的意圖。
 *
 * 2. 分類處理：
 *    GlobalExceptionHandler 可以根據例外類型
 *    決定回傳什麼 HTTP 狀態碼。
 *
 * 3. 攜帶資訊：
 *    可以在例外中包含詳細資訊（如資源名稱、ID），
 *    方便除錯和記錄日誌。
 *
 * 為什麼繼承 RuntimeException 而非 Exception？
 *
 * RuntimeException 是「非受檢例外」（Unchecked Exception）：
 * - 不需要在方法簽章宣告 throws
 * - 不需要強制 try-catch
 * - 程式碼更簡潔
 *
 * 在 Spring 中，業務例外通常使用 RuntimeException，
 * 因為可以透過 @ExceptionHandler 統一處理。
 *
 * @author Spring PetClinic
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * 建構子：使用自訂訊息
     *
     * 使用範例：
     * throw new ResourceNotFoundException("找不到此飼主");
     *
     * @param message 錯誤訊息
     */
    public ResourceNotFoundException(String message) {
        // 呼叫父類別建構子，設定錯誤訊息
        super(message);
    }

    /**
     * 建構子：根據資源名稱和 ID 自動產生訊息
     *
     * 使用範例：
     * throw new ResourceNotFoundException("Owner", 123);
     * // 產生訊息："Owner with id 123 not found"
     *
     * 這種設計讓錯誤訊息格式統一，方便前端處理。
     *
     * @param resourceName 資源名稱（如 Owner, Pet）
     * @param id 資源 ID
     */
    public ResourceNotFoundException(String resourceName, Integer id) {
        // String.format: 格式化字串
        // %s: 字串佔位符
        // %d: 整數佔位符
        super(String.format("%s with id %d not found", resourceName, id));
    }
}
