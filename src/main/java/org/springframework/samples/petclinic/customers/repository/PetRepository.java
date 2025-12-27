package org.springframework.samples.petclinic.customers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {

    /**
     * 根據飼主 ID 查詢寵物
     */
    List<Pet> findByOwnerId(Integer ownerId);

    /**
     * 根據寵物類型查詢
     */
    List<Pet> findByType(PetType type);
}
