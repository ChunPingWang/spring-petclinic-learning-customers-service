package org.springframework.samples.petclinic.customers.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 寵物資料傳輸物件（Data Transfer Object）
 *
 * 這個 DTO 用於在 Controller 和前端之間傳輸寵物資料。
 *
 * 與 Pet Entity 的差異：
 *
 * 1. 關聯處理不同：
 *    - Pet Entity: type 是 PetType 物件
 *    - PetDTO: type 拆成 typeId 和 typeName
 *
 *    這樣做的好處是 JSON 更扁平、更容易處理。
 *
 * 2. 沒有雙向關聯：
 *    - Pet Entity: 有 owner 物件參考
 *    - PetDTO: 只有 ownerId
 *
 *    避免序列化時的循環引用問題。
 *
 * @author Spring PetClinic
 */
@Getter  // Lombok: 產生 getter 方法
@Setter  // Lombok: 產生 setter 方法
@NoArgsConstructor  // Lombok: 無參數建構子
@AllArgsConstructor  // Lombok: 全參數建構子
public class PetDTO {

    /**
     * 寵物 ID
     *
     * 新增時為 null，由資料庫自動產生。
     */
    private Integer id;

    /**
     * 寵物名字（必填）
     *
     * @NotBlank 確保名字不能為空。
     */
    @NotBlank(message = "寵物名字不能為空")
    private String name;

    /**
     * 寵物生日
     *
     * 使用 Java 8 的 LocalDate：
     * - 只包含日期，不包含時間
     * - 不可變（immutable），執行緒安全
     * - 比舊的 java.util.Date 更好用
     *
     * Spring 會自動將 JSON 的日期字串轉換為 LocalDate。
     * 預設格式：yyyy-MM-dd（例如：2020-01-15）
     */
    private LocalDate birthDate;

    /**
     * 寵物類型名稱
     *
     * 用於回傳資料時顯示類型名稱（如：cat, dog）。
     * 這是唯讀欄位，新增寵物時使用 typeId。
     */
    private String typeName;

    /**
     * 寵物類型 ID
     *
     * 用於新增或更新寵物時指定類型。
     * 前端傳入類型 ID，後端根據 ID 查詢對應的 PetType。
     */
    private Integer typeId;

    /**
     * 飼主 ID
     *
     * 用於識別這隻寵物屬於哪個飼主。
     * 這是外鍵的 DTO 表示方式。
     *
     * 為什麼用 ID 而不是完整的 OwnerDTO？
     * 1. 避免循環引用（Owner 有 Pet 列表，Pet 又有 Owner）
     * 2. 減少資料傳輸量
     * 3. 前端通常只需要 ID 來建立連結
     */
    private Integer ownerId;
}
