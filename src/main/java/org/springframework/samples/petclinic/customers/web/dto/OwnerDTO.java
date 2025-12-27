package org.springframework.samples.petclinic.customers.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 飼主資料傳輸物件（Data Transfer Object）
 *
 * 什麼是 DTO？
 *
 * DTO 是一種設計模式，用於在不同層之間傳輸資料。
 * 它是一個簡單的物件，只包含資料，沒有業務邏輯。
 *
 * 為什麼需要 DTO？為什麼不直接使用 Entity？
 *
 * 1. 資安考量：
 *    Entity 可能包含敏感資訊（如密碼雜湊、內部 ID）。
 *    使用 DTO 可以精確控制哪些資料要暴露給前端。
 *
 * 2. 避免循環引用：
 *    Entity 之間的雙向關聯（如 Owner ↔ Pet）會導致
 *    JSON 序列化時無限迴圈。DTO 可以打破這個循環。
 *
 * 3. 解耦前後端：
 *    如果資料庫結構改變，只需修改 Mapper，
 *    前端接收的 DTO 格式可以保持不變。
 *
 * 4. 優化資料傳輸：
 *    可以只傳輸必要的欄位，減少網路流量。
 *    例如：列表頁可能不需要寵物詳情。
 *
 * 5. 不同用途的資料格式：
 *    同一個 Entity 可能需要不同的 DTO：
 *    - OwnerListDTO: 列表頁，只需要基本資訊
 *    - OwnerDetailDTO: 詳情頁，包含所有資訊
 *    - OwnerCreateDTO: 新增時，不需要 ID
 *
 * @author Spring PetClinic
 */
@Getter  // Lombok: 自動產生所有欄位的 getter 方法
@Setter  // Lombok: 自動產生所有欄位的 setter 方法
@NoArgsConstructor  // Lombok: 產生無參數建構子（JSON 反序列化需要）
@AllArgsConstructor  // Lombok: 產生包含所有參數的建構子（方便測試）
public class OwnerDTO {

    /**
     * 飼主 ID
     *
     * 新增時此欄位為 null，由資料庫自動產生。
     * 更新和查詢時會有值。
     */
    private Integer id;

    /**
     * 名字（必填）
     *
     * @NotBlank: 驗證註解
     * - 不能是 null
     * - 不能是空字串 ""
     * - 不能只有空白 "   "
     *
     * message: 驗證失敗時的錯誤訊息
     */
    @NotBlank(message = "名字不能為空")
    private String firstName;

    /**
     * 姓氏（必填）
     */
    @NotBlank(message = "姓氏不能為空")
    private String lastName;

    /**
     * 地址（選填）
     *
     * 沒有驗證註解，表示此欄位可以為空。
     */
    private String address;

    /**
     * 城市（選填）
     */
    private String city;

    /**
     * 電話號碼
     *
     * @Pattern: 使用正規表達式驗證格式
     * - \\d: 數字（0-9）
     * - {10}: 恰好 10 個字元
     * - 所以 \\d{10} 表示「恰好 10 位數字」
     *
     * 正規表達式常用語法：
     * - \\d: 數字
     * - \\w: 字母、數字、底線
     * - \\s: 空白字元
     * - +: 一個或多個
     * - *: 零個或多個
     * - {n}: 恰好 n 個
     * - {n,m}: n 到 m 個
     */
    @Pattern(regexp = "\\d{10}", message = "電話必須是10位數字")
    private String telephone;

    /**
     * 寵物列表
     *
     * 初始化為空列表，避免 NullPointerException。
     * 這是防禦性程式設計的好習慣。
     *
     * 注意：這裡使用 PetDTO 而非 Pet Entity，
     * 避免了循環引用問題。
     */
    private List<PetDTO> pets = new ArrayList<>();
}
