package org.springframework.samples.petclinic.customers.web.mapper;

import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.dto.OwnerDTO;
import org.springframework.samples.petclinic.customers.web.dto.PetDTO;

import java.util.List;
import java.util.stream.Collectors;

public class OwnerMapper {

    private OwnerMapper() {
        // Utility class
    }

    public static OwnerDTO toDTO(Owner owner) {
        if (owner == null) {
            return null;
        }

        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setAddress(owner.getAddress());
        dto.setCity(owner.getCity());
        dto.setTelephone(owner.getTelephone());

        if (owner.getPets() != null && !owner.getPets().isEmpty()) {
            List<PetDTO> petDTOs = owner.getPets().stream()
                .map(PetMapper::toDTO)
                .collect(Collectors.toList());
            dto.setPets(petDTOs);
        }

        return dto;
    }

    public static Owner toEntity(OwnerDTO dto) {
        if (dto == null) {
            return null;
        }

        Owner owner = new Owner();
        owner.setId(dto.getId());
        owner.setFirstName(dto.getFirstName());
        owner.setLastName(dto.getLastName());
        owner.setAddress(dto.getAddress());
        owner.setCity(dto.getCity());
        owner.setTelephone(dto.getTelephone());

        return owner;
    }
}
