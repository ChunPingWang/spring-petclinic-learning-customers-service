package org.springframework.samples.petclinic.customers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 寵物類型資料存取介面（Repository）
 *
 * 負責寵物類型資料的 CRUD 操作。
 *
 * @author Spring PetClinic
 */
@Repository
public interface PetTypeRepository extends JpaRepository<PetType, Integer> {

    /**
     * 根據名稱查詢寵物類型
     *
     * @param name 類型名稱（例如：cat, dog, bird）
     * @return 包含寵物類型的 Optional（找不到時為空）
     */
    Optional<PetType> findByName(String name);
}
