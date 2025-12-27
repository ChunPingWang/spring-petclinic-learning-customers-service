package org.springframework.samples.petclinic.customers.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.customers.exception.BusinessRuleException;
import org.springframework.samples.petclinic.customers.exception.DuplicateResourceException;
import org.springframework.samples.petclinic.customers.exception.ErrorResponse;
import org.springframework.samples.petclinic.customers.exception.ResourceNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全域例外處理器
 *
 * 這個類別負責處理整個應用程式中拋出的例外，
 * 將它們轉換為適當的 HTTP 回應。
 *
 * 為什麼需要全域例外處理？
 *
 * 1. 統一錯誤回應格式：
 *    所有錯誤都以相同的 JSON 格式回傳，前端容易處理。
 *
 * 2. 避免敏感資訊洩漏：
 *    如果不處理例外，Spring 會回傳完整的堆疊追蹤，
 *    可能包含敏感資訊（資料庫結構、內部路徑等）。
 *
 * 3. 程式碼更簡潔：
 *    Controller 不需要處理例外，專注於正常流程即可。
 *
 * HTTP 狀態碼說明：
 * - 200 OK: 成功
 * - 201 Created: 成功建立資源
 * - 204 No Content: 成功，無回傳內容
 * - 400 Bad Request: 請求格式錯誤
 * - 404 Not Found: 找不到資源
 * - 409 Conflict: 資源衝突（如重複的電話號碼）
 * - 500 Internal Server Error: 伺服器內部錯誤
 *
 * @author Spring PetClinic
 */
@RestControllerAdvice  // 標記為全域例外處理器，適用於所有 @RestController
public class GlobalExceptionHandler {

    /**
     * 處理「資源找不到」例外
     *
     * 觸發情境：查詢不存在的飼主 ID
     *
     * @param ex 例外物件
     * @return HTTP 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)  // HTTP 404
            .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * 處理「業務規則」例外
     *
     * 觸發情境：
     * - 寵物生日是未來日期
     * - 寵物數量超過上限
     * - 寵物名字重複
     *
     * @param ex 例外物件
     * @return HTTP 400 Bad Request
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)  // HTTP 400
            .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * 處理「重複資源」例外
     *
     * 觸發情境：新增或更新時電話號碼已被使用
     *
     * @param ex 例外物件
     * @return HTTP 409 Conflict
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)  // HTTP 409
            .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * 處理「驗證錯誤」例外
     *
     * 觸發情境：@Valid 驗證失敗
     * - 電話號碼格式不正確
     * - 必填欄位為空
     *
     * 這個例外由 Spring Validation 自動拋出，
     * 當 @Valid 標記的參數驗證失敗時觸發。
     *
     * @param ex 例外物件
     * @return HTTP 400 Bad Request，包含所有驗證錯誤訊息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // 從例外中提取所有欄位錯誤訊息
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()  // 取得所有欄位錯誤
            .stream()
            .map(FieldError::getDefaultMessage)  // 取得錯誤訊息
            .collect(Collectors.toList());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("驗證失敗", errors));
    }

    /**
     * 處理所有未預期的例外（兜底處理）
     *
     * 這是最後一道防線，處理所有未被其他 Handler 捕獲的例外。
     *
     * 注意事項：
     * - 不要將完整的例外訊息回傳給前端，可能包含敏感資訊
     * - 應該記錄完整的例外日誌，方便除錯
     * - 給前端的訊息應該簡潔，不透露內部實作細節
     *
     * @param ex 例外物件
     * @return HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        // 在實際專案中，這裡應該記錄日誌
        // log.error("未預期的錯誤", ex);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)  // HTTP 500
            .body(new ErrorResponse("伺服器內部錯誤：" + ex.getMessage()));
    }
}
