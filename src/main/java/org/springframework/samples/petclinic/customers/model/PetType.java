package org.springframework.samples.petclinic.customers.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 寵物類型實體類別
 *
 * 這個類別代表寵物的種類（狗、貓、鳥、蜥蜴等）。
 *
 * 為什麼需要獨立的 PetType 表？
 *
 * 1. 資料一致性：
 *    如果直接用 String 存寵物類型，使用者可能會輸入：
 *    「狗」、「DOG」、「dog」、「小狗」、「犬」...
 *    這些都代表同一種動物，但會造成統計困難。
 *
 * 2. 下拉選單管理：
 *    前端通常會用下拉選單讓使用者選擇寵物類型，
 *    這些選項需要從資料庫讀取，方便管理和新增。
 *
 * 3. 國際化支援：
 *    未來如果要支援多語言，可以在這個表加入語言代碼。
 *
 * @author Spring PetClinic
 */
@Entity  // 標記為 JPA 實體
@Table(name = "types")  // 對應資料表 "types"
@Getter
@Setter
@NoArgsConstructor
public class PetType {

    /**
     * 寵物類型的唯一識別碼（主鍵）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 寵物類型的名稱
     * 例如：cat, dog, bird, lizard, snake, hamster
     */
    @Column(name = "name")
    private String name;

    /**
     * 建構子：建立指定名稱的寵物類型
     *
     * @param name 類型名稱
     */
    public PetType(String name) {
        this.name = name;
    }
}
