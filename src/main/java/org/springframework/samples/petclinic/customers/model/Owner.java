package org.springframework.samples.petclinic.customers.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 飼主實體類別
 *
 * 這個類別代表寵物醫院系統中的「飼主」資料。
 * 使用 JPA 註解將此類別對應到資料庫的 owners 資料表。
 *
 * 資料表結構：
 * - id: 主鍵，自動產生
 * - first_name: 名字
 * - last_name: 姓氏
 * - address: 地址
 * - city: 城市
 * - telephone: 電話（必須是10位數字）
 *
 * 關聯關係：
 * - 一個飼主可以擁有多隻寵物（一對多關係）
 *
 * @author Spring PetClinic
 */
@Entity  // 標記此類別為 JPA 實體，會對應到資料庫的一張表
@Table(name = "owners")  // 指定對應的資料表名稱為 "owners"
@Getter  // Lombok: 自動產生所有欄位的 getter 方法
@Setter  // Lombok: 自動產生所有欄位的 setter 方法
@NoArgsConstructor  // Lombok: 自動產生無參數建構子（JPA 必需）
public class Owner {

    /**
     * 飼主的唯一識別碼（主鍵）
     * 使用 IDENTITY 策略讓資料庫自動產生 ID
     */
    @Id  // 標記此欄位為主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ID 由資料庫自動產生（1, 2, 3...）
    private Integer id;

    /**
     * 飼主的名字
     * 不能為空白，對應資料庫欄位 first_name
     */
    @Column(name = "first_name")  // 指定對應的資料庫欄位名稱
    @NotBlank(message = "名字不能為空")  // 驗證：不能是 null 或空白字串
    private String firstName;

    /**
     * 飼主的姓氏
     * 不能為空白，對應資料庫欄位 last_name
     */
    @Column(name = "last_name")
    @NotBlank(message = "姓氏不能為空")
    private String lastName;

    /**
     * 飼主的地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 飼主所在的城市
     */
    @Column(name = "city")
    private String city;

    /**
     * 飼主的電話號碼
     * 必須是10位數字，使用正規表達式驗證
     *
     * 正規表達式說明：
     * - \\d 表示數字（0-9）
     * - {10} 表示剛好10個字元
     */
    @Column(name = "telephone")
    @Pattern(regexp = "\\d{10}", message = "電話必須是10位數字")
    private String telephone;

    /**
     * 此飼主擁有的所有寵物
     *
     * 關聯設定說明：
     * - @OneToMany: 一對多關係，一個飼主可以有多隻寵物
     * - cascade = ALL: 連鎖操作，對飼主的操作會影響其寵物
     *   例如：刪除飼主時，會一併刪除其所有寵物
     * - mappedBy = "owner": 關聯由 Pet 類別的 owner 欄位維護
     * - fetch = LAZY: 延遲載入，查詢飼主時不會立即載入寵物資料
     *   只有在實際存取 pets 時才會觸發查詢
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Pet> pets = new HashSet<>();

    /**
     * 建構子：建立只有姓名的飼主
     *
     * @param firstName 名字
     * @param lastName 姓氏
     */
    public Owner(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * 建構子：建立有姓名和城市的飼主
     *
     * @param firstName 名字
     * @param lastName 姓氏
     * @param city 城市
     */
    public Owner(String firstName, String lastName, String city) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
    }

    /**
     * 新增寵物到此飼主
     *
     * 這個方法維護雙向關聯：
     * 1. 將寵物加入飼主的寵物集合
     * 2. 設定寵物的主人為此飼主
     *
     * @param pet 要新增的寵物
     */
    public void addPet(Pet pet) {
        pets.add(pet);       // 將寵物加入集合
        pet.setOwner(this);  // 設定寵物的主人為自己（維護雙向關聯）
    }

    /**
     * 從此飼主移除寵物
     *
     * @param pet 要移除的寵物
     */
    public void removePet(Pet pet) {
        pets.remove(pet);    // 從集合移除寵物
        pet.setOwner(null);  // 清除寵物的主人（維護雙向關聯）
    }
}
