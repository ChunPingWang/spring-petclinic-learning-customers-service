package org.springframework.samples.petclinic.customers.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.service.OwnerService;
import org.springframework.samples.petclinic.customers.web.dto.OwnerDTO;
import org.springframework.samples.petclinic.customers.web.dto.PetDTO;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerMapper;
import org.springframework.samples.petclinic.customers.web.mapper.PetMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    /**
     * 查詢飼主列表
     */
    @GetMapping
    public ResponseEntity<List<OwnerDTO>> searchOwners(
            @RequestParam(required = false) String lastName) {

        List<Owner> owners = lastName != null
            ? ownerService.findByLastName(lastName)
            : ownerService.findAll();

        List<OwnerDTO> dtos = owners.stream()
            .map(OwnerMapper::toDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * 分頁查詢飼主
     * 呼叫範例：GET /api/owners/page?page=0&size=20&sort=lastName,asc
     */
    @GetMapping("/page")
    public Page<OwnerDTO> findAll(Pageable pageable) {
        return ownerService.findAll(pageable)
            .map(OwnerMapper::toDTO);
    }

    /**
     * 根據 ID 查詢飼主
     */
    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerDTO> getOwner(@PathVariable Integer ownerId) {
        Owner owner = ownerService.findById(ownerId);
        return ResponseEntity.ok(OwnerMapper.toDTO(owner));
    }

    /**
     * 新增飼主
     */
    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody OwnerDTO dto) {
        Owner owner = OwnerMapper.toEntity(dto);
        Owner saved = ownerService.save(owner);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(OwnerMapper.toDTO(saved));
    }

    /**
     * 更新飼主
     */
    @PutMapping("/{ownerId}")
    public ResponseEntity<OwnerDTO> updateOwner(
            @PathVariable Integer ownerId,
            @Valid @RequestBody OwnerDTO dto) {
        Owner ownerDetails = OwnerMapper.toEntity(dto);
        Owner updated = ownerService.update(ownerId, ownerDetails);
        return ResponseEntity.ok(OwnerMapper.toDTO(updated));
    }

    /**
     * 刪除飼主
     */
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Integer ownerId) {
        ownerService.delete(ownerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 為飼主新增寵物
     */
    @PostMapping("/{ownerId}/pets")
    public ResponseEntity<PetDTO> addPet(
            @PathVariable Integer ownerId,
            @Valid @RequestBody PetDTO dto) {

        Pet pet = PetMapper.toEntity(dto);
        ownerService.addPet(ownerId, pet);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(PetMapper.toDTO(pet));
    }
}
