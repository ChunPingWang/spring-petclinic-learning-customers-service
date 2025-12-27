package org.springframework.samples.petclinic.customers.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 寵物實體類別
 *
 * 這個類別代表寵物醫院系統中的「寵物」資料。
 * 每隻寵物都屬於一個飼主（多對一關係）。
 * 每隻寵物都有一個類型（狗、貓、鳥等）。
 *
 * 資料表結構：
 * - id: 主鍵，自動產生
 * - name: 寵物名字
 * - birth_date: 出生日期
 * - type_id: 寵物類型（外鍵，關聯到 types 表）
 * - owner_id: 飼主（外鍵，關聯到 owners 表）
 *
 * @author Spring PetClinic
 */
@Entity  // 標記此類別為 JPA 實體
@Table(name = "pets")  // 對應資料表 "pets"
@Getter
@Setter
@NoArgsConstructor
public class Pet {

    /**
     * 寵物的唯一識別碼（主鍵）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 寵物的名字
     */
    @Column(name = "name")
    private String name;

    /**
     * 寵物的出生日期
     *
     * @DateTimeFormat: 指定日期格式為 yyyy-MM-dd（例如：2023-05-15）
     * 這個註解用於 Spring MVC 的資料綁定，將字串轉換為 LocalDate
     */
    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /**
     * 寵物的類型（狗、貓、鳥等）
     *
     * @ManyToOne: 多對一關係，多隻寵物可以是同一種類型
     * @JoinColumn: 指定外鍵欄位名稱為 type_id
     *
     * 為什麼要用獨立的 PetType 表？
     * - 統一管理寵物類型，避免使用者輸入不一致的資料
     * - 例如：「狗」、「DOG」、「dog」、「小狗」都表示同一種類型
     * - 使用外鍵可以確保資料的一致性
     */
    @ManyToOne
    @JoinColumn(name = "type_id")
    private PetType type;

    /**
     * 寵物的飼主
     *
     * @ManyToOne: 多對一關係，多隻寵物可以屬於同一個飼主
     * @JoinColumn: 指定外鍵欄位名稱為 owner_id
     *
     * 這是雙向關聯的「多」方，由 Owner.pets 的 mappedBy 指向此欄位
     */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    /**
     * 建構子：建立只有名字和生日的寵物
     *
     * @param name 寵物名字
     * @param birthDate 出生日期
     */
    public Pet(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    /**
     * 建構子：建立有名字、生日和類型的寵物
     *
     * @param name 寵物名字
     * @param birthDate 出生日期
     * @param type 寵物類型
     */
    public Pet(String name, LocalDate birthDate, PetType type) {
        this.name = name;
        this.birthDate = birthDate;
        this.type = type;
    }
}
