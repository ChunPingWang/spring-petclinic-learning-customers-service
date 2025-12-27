package org.springframework.samples.petclinic.customers.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 錯誤回應物件
 *
 * 統一的錯誤回應格式，用於 API 錯誤回應的 JSON body。
 *
 * 為什麼需要統一格式？
 *
 * 1. 前端容易處理：
 *    前端可以統一處理所有錯誤，不需要針對不同錯誤寫不同的解析邏輯。
 *
 * 2. API 文件更清晰：
 *    使用者知道錯誤回應的結構，方便整合。
 *
 * 3. 除錯更容易：
 *    包含時間戳記，方便追蹤問題。
 *
 * JSON 回應範例：
 *
 * 單一錯誤：
 * {
 *   "message": "找不到此飼主",
 *   "timestamp": "2024-01-15T10:30:00",
 *   "errors": null
 * }
 *
 * 多個驗證錯誤：
 * {
 *   "message": "驗證失敗",
 *   "timestamp": "2024-01-15T10:30:00",
 *   "errors": [
 *     "名字不能為空",
 *     "電話必須是10位數字"
 *   ]
 * }
 *
 * @author Spring PetClinic
 */
@Getter  // Lombok: 產生 getter（JSON 序列化需要）
@Setter  // Lombok: 產生 setter
@NoArgsConstructor  // Lombok: 無參數建構子
@AllArgsConstructor  // Lombok: 全參數建構子
public class ErrorResponse {

    /**
     * 主要錯誤訊息
     *
     * 對於單一錯誤，這就是錯誤說明。
     * 對於多個驗證錯誤，這是總結訊息（如「驗證失敗」）。
     */
    private String message;

    /**
     * 錯誤發生的時間戳記
     *
     * 使用 LocalDateTime 而非 Date：
     * - Java 8 新的日期時間 API
     * - 不可變（immutable）
     * - 執行緒安全
     *
     * 用途：
     * - 除錯時可以查看錯誤發生的確切時間
     * - 與日誌檔案的時間比對
     */
    private LocalDateTime timestamp;

    /**
     * 詳細的錯誤列表
     *
     * 主要用於驗證錯誤，當多個欄位驗證失敗時，
     * 每個錯誤訊息會是列表中的一個元素。
     *
     * 範例：["名字不能為空", "電話必須是10位數字"]
     *
     * 如果只有單一錯誤，這個欄位通常是 null。
     */
    private List<String> errors;

    /**
     * 建構子：單一錯誤訊息
     *
     * 用於一般錯誤（ResourceNotFoundException, BusinessRuleException 等）
     *
     * 會自動設定時間戳記為「現在」。
     *
     * @param message 錯誤訊息
     */
    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();  // 記錄錯誤發生時間
    }

    /**
     * 建構子：多個錯誤訊息
     *
     * 用於驗證錯誤（MethodArgumentNotValidException）
     *
     * 會自動設定時間戳記為「現在」。
     *
     * @param message 主要訊息（如「驗證失敗」）
     * @param errors 詳細的錯誤列表
     */
    public ErrorResponse(String message, List<String> errors) {
        this.message = message;
        this.timestamp = LocalDateTime.now();  // 記錄錯誤發生時間
        this.errors = errors;
    }
}
