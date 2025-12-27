package org.springframework.samples.petclinic.customers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 寵物資料存取介面（Repository）
 *
 * 負責寵物資料的 CRUD 操作。
 * 繼承 JpaRepository 自動獲得基本的資料庫操作方法。
 *
 * @author Spring PetClinic
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {

    /**
     * 根據飼主 ID 查詢所有寵物
     *
     * Spring Data JPA 會根據方法名稱自動產生查詢：
     * - find: 查詢
     * - By: 根據...
     * - OwnerId: Pet 類別中 owner.id 的路徑
     *
     * 等同於 SQL: SELECT * FROM pets WHERE owner_id = ?
     *
     * @param ownerId 飼主 ID
     * @return 該飼主的所有寵物
     */
    List<Pet> findByOwnerId(Integer ownerId);

    /**
     * 根據寵物類型查詢所有寵物
     *
     * @param type 寵物類型
     * @return 該類型的所有寵物
     */
    List<Pet> findByType(PetType type);
}
