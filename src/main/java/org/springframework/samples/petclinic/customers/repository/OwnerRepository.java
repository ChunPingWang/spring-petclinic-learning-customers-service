package org.springframework.samples.petclinic.customers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 飼主資料存取介面（Repository）
 *
 * 這個介面負責與資料庫溝通，執行 CRUD（新增、查詢、更新、刪除）操作。
 *
 * 繼承 JpaRepository 的好處：
 * Spring Data JPA 會自動實作以下常用方法，不需要寫任何程式碼：
 * - save(entity): 新增或更新一筆資料
 * - findById(id): 根據 ID 查詢
 * - findAll(): 查詢所有資料
 * - delete(entity): 刪除一筆資料
 * - count(): 計算資料總數
 * - existsById(id): 檢查 ID 是否存在
 *
 * JpaRepository<Owner, Integer> 參數說明：
 * - Owner: 實體類別的型別
 * - Integer: 主鍵（ID）的型別
 *
 * @author Spring PetClinic
 */
@Repository  // 標記此介面為 Spring 的 Repository 元件，會被自動掃描並註冊
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    /**
     * 根據姓氏模糊查詢飼主
     *
     * 使用 JPQL（Java Persistence Query Language）自訂查詢。
     *
     * 查詢說明：
     * - SELECT DISTINCT owner: 選取不重複的飼主
     * - LEFT JOIN FETCH owner.pets: 一次查詢時同時載入寵物資料（解決 N+1 問題）
     * - WHERE owner.lastName LIKE :lastName%: 姓氏以指定字串開頭
     *
     * 什麼是 N+1 問題？
     * 如果不用 JOIN FETCH，查詢 100 個飼主會執行：
     * - 1 次查詢飼主列表
     * - 100 次查詢每個飼主的寵物
     * = 總共 101 次查詢！
     *
     * 使用 JOIN FETCH 後，只需要 1 次查詢就能取得所有資料。
     *
     * @param lastName 姓氏（支援模糊查詢，例如 "王" 可以找到 "王小明"、"王大華"）
     * @return 符合條件的飼主列表
     */
    @Query("SELECT DISTINCT owner FROM Owner owner " +
           "LEFT JOIN FETCH owner.pets " +
           "WHERE owner.lastName LIKE :lastName%")
    List<Owner> findByLastName(@Param("lastName") String lastName);

    /**
     * 根據 ID 查詢飼主，並一次載入寵物資料
     *
     * 使用 LEFT JOIN FETCH 確保查詢飼主時一併取得寵物資料，
     * 避免延遲載入造成的額外查詢。
     *
     * @param id 飼主 ID
     * @return 包含飼主的 Optional（找不到時為空）
     */
    @Query("SELECT owner FROM Owner owner " +
           "LEFT JOIN FETCH owner.pets " +
           "WHERE owner.id = :id")
    Optional<Owner> findByIdWithPets(@Param("id") Integer id);

    /**
     * 檢查電話號碼是否已存在
     *
     * Spring Data JPA 會根據方法名稱自動產生查詢：
     * - exists: 檢查是否存在
     * - By: 根據...
     * - Telephone: 欄位名稱
     *
     * 等同於 SQL: SELECT EXISTS(SELECT 1 FROM owners WHERE telephone = ?)
     *
     * @param telephone 電話號碼
     * @return true 表示電話已存在，false 表示不存在
     */
    boolean existsByTelephone(String telephone);

    /**
     * 查詢所有飼主，並一次載入寵物資料
     *
     * @return 所有飼主（包含寵物資料）
     */
    @Query("SELECT DISTINCT owner FROM Owner owner LEFT JOIN FETCH owner.pets")
    List<Owner> findAllWithPets();

    /**
     * 分頁查詢所有飼主
     *
     * Spring Data JPA 內建分頁支援，只要傳入 Pageable 參數即可。
     *
     * 使用範例：
     * - GET /api/owners/page?page=0&size=10 → 第 1 頁，每頁 10 筆
     * - GET /api/owners/page?page=1&size=20 → 第 2 頁，每頁 20 筆
     * - GET /api/owners/page?sort=lastName,asc → 依姓氏升冪排序
     *
     * @param pageable 分頁參數（頁碼、每頁筆數、排序方式）
     * @return 分頁結果，包含資料和分頁資訊
     */
    Page<Owner> findAll(Pageable pageable);
}
