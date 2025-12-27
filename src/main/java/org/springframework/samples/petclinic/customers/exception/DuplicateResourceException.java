package org.springframework.samples.petclinic.customers.exception;

/**
 * 重複資源例外
 *
 * 當嘗試建立或更新的資源與現有資源衝突時拋出此例外。
 *
 * 對應 HTTP 狀態碼：409 Conflict
 *
 * HTTP 409 vs 400 的區別：
 *
 * 400 Bad Request（壞請求）：
 * - 請求本身有問題（格式錯誤、邏輯不合理）
 * - 例如：日期是未來、數量超過上限
 * - 使用 BusinessRuleException
 *
 * 409 Conflict（衝突）：
 * - 請求本身沒問題，但與現有資料衝突
 * - 例如：電話號碼已被使用、帳號已存在
 * - 使用 DuplicateResourceException
 *
 * 使用情境範例：
 *
 * 1. 新增飼主時電話已被使用
 *    throw new DuplicateResourceException("此電話已被註冊");
 *
 * 2. 更新飼主時新電話與其他人重複
 *    throw new DuplicateResourceException("此電話已被其他飼主使用");
 *
 * 3. 使用者名稱已存在
 *    throw new DuplicateResourceException("此帳號已被註冊");
 *
 * 前端處理建議：
 *
 * 當收到 409 錯誤時，前端應該：
 * 1. 顯示錯誤訊息給使用者
 * 2. 讓使用者修改重複的欄位（如換一個電話號碼）
 * 3. 重新提交表單
 *
 * @author Spring PetClinic
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * 建構子
     *
     * @param message 說明哪個資源重複
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}
