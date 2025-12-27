package org.springframework.samples.petclinic.customers.web.mapper;

import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.web.dto.PetDTO;

public class PetMapper {

    private PetMapper() {
        // Utility class
    }

    public static PetDTO toDTO(Pet pet) {
        if (pet == null) {
            return null;
        }

        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setBirthDate(pet.getBirthDate());

        if (pet.getType() != null) {
            dto.setTypeId(pet.getType().getId());
            dto.setTypeName(pet.getType().getName());
        }

        if (pet.getOwner() != null) {
            dto.setOwnerId(pet.getOwner().getId());
        }

        return dto;
    }

    public static Pet toEntity(PetDTO dto) {
        if (dto == null) {
            return null;
        }

        Pet pet = new Pet();
        pet.setId(dto.getId());
        pet.setName(dto.getName());
        pet.setBirthDate(dto.getBirthDate());

        return pet;
    }
}
