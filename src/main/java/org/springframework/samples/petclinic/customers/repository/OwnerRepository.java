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

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    /**
     * 根據姓氏模糊查詢
     */
    @Query("SELECT DISTINCT owner FROM Owner owner " +
           "LEFT JOIN FETCH owner.pets " +
           "WHERE owner.lastName LIKE :lastName%")
    List<Owner> findByLastName(@Param("lastName") String lastName);

    /**
     * 根據 ID 查詢，並一次把寵物資料也抓出來
     */
    @Query("SELECT owner FROM Owner owner " +
           "LEFT JOIN FETCH owner.pets " +
           "WHERE owner.id = :id")
    Optional<Owner> findByIdWithPets(@Param("id") Integer id);

    /**
     * 檢查電話是否已存在
     */
    boolean existsByTelephone(String telephone);

    /**
     * 分頁查詢所有飼主
     */
    @Query("SELECT DISTINCT owner FROM Owner owner LEFT JOIN FETCH owner.pets")
    List<Owner> findAllWithPets();

    /**
     * 分頁查詢
     */
    Page<Owner> findAll(Pageable pageable);
}
