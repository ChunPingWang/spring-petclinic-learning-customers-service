package org.springframework.samples.petclinic.customers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetTypeRepository extends JpaRepository<PetType, Integer> {

    /**
     * 根據名稱查詢寵物類型
     */
    Optional<PetType> findByName(String name);
}
