package org.springframework.samples.petclinic.customers.exception;

/**
 * 業務規則例外
 *
 * 當使用者的操作違反業務規則時拋出此例外。
 *
 * 對應 HTTP 狀態碼：400 Bad Request
 *
 * 使用情境範例：
 *
 * 1. 寵物生日是未來日期
 *    throw new BusinessRuleException("寵物生日不能是未來日期");
 *
 * 2. 寵物數量超過上限
 *    throw new BusinessRuleException("每個飼主最多只能登記 10 隻寵物");
 *
 * 3. 寵物名字重複
 *    throw new BusinessRuleException("寵物名字不能重複");
 *
 * 業務規則 vs 驗證錯誤
 *
 * 驗證錯誤（Validation Error）：
 * - 檢查資料格式，不需要查詢資料庫
 * - 例如：電話號碼必須是 10 位數字
 * - 由 @Valid 註解自動處理
 * - 拋出 MethodArgumentNotValidException
 *
 * 業務規則錯誤（Business Rule Error）：
 * - 需要查詢資料庫或進行複雜計算
 * - 例如：檢查寵物數量是否超過上限
 * - 在 Service 層手動檢查並拋出 BusinessRuleException
 *
 * @author Spring PetClinic
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * 建構子
     *
     * @param message 業務規則違反的說明
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}
