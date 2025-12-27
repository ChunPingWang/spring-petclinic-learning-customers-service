package org.springframework.samples.petclinic.customers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.customers.exception.BusinessRuleException;
import org.springframework.samples.petclinic.customers.exception.DuplicateResourceException;
import org.springframework.samples.petclinic.customers.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OwnerService {

    private final OwnerRepository ownerRepository;

    /**
     * 儲存飼主
     */
    public Owner save(Owner owner) {
        log.info("嘗試儲存飼主：{} {}", owner.getFirstName(), owner.getLastName());

        // 業務規則 1：電話號碼不能重複
        if (owner.getTelephone() != null && ownerRepository.existsByTelephone(owner.getTelephone())) {
            throw new DuplicateResourceException("此電話已被註冊");
        }

        // 業務規則 2：同一飼主不能有重複的寵物名字
        if (owner.getPets() != null && !owner.getPets().isEmpty()) {
            Set<String> petNames = owner.getPets().stream()
                .map(Pet::getName)
                .collect(Collectors.toSet());

            if (petNames.size() != owner.getPets().size()) {
                throw new BusinessRuleException("寵物名字不能重複");
            }
        }

        try {
            Owner saved = ownerRepository.save(owner);
            log.info("飼主儲存成功，ID：{}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("儲存飼主失敗：{}", owner, e);
            throw e;
        }
    }

    /**
     * 為飼主新增寵物
     */
    public void addPet(Integer ownerId, Pet pet) {
        Owner owner = ownerRepository.findByIdWithPets(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("找不到此飼主"));

        // 業務規則 3：寵物生日不能是未來
        if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("寵物生日不能是未來日期");
        }

        // 業務規則 4：每個飼主最多養 10 隻寵物
        if (owner.getPets().size() >= 10) {
            throw new BusinessRuleException("每個飼主最多只能登記 10 隻寵物");
        }

        // 業務規則 5：檢查寵物名字是否重複
        boolean nameExists = owner.getPets().stream()
            .anyMatch(p -> p.getName().equals(pet.getName()));
        if (nameExists) {
            throw new BusinessRuleException("寵物名字不能重複");
        }

        owner.addPet(pet);
        ownerRepository.save(owner);
        log.info("成功為飼主 {} 新增寵物 {}", ownerId, pet.getName());
    }

    /**
     * 根據 ID 查詢飼主
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "owners", key = "#id")
    public Owner findById(Integer id) {
        return ownerRepository.findByIdWithPets(id)
            .orElseThrow(() -> new ResourceNotFoundException("找不到此飼主"));
    }

    /**
     * 根據姓氏查詢飼主
     */
    @Transactional(readOnly = true)
    public List<Owner> findByLastName(String lastName) {
        return ownerRepository.findByLastName(lastName);
    }

    /**
     * 查詢所有飼主
     */
    @Transactional(readOnly = true)
    public List<Owner> findAll() {
        return ownerRepository.findAllWithPets();
    }

    /**
     * 分頁查詢所有飼主
     */
    @Transactional(readOnly = true)
    public Page<Owner> findAll(Pageable pageable) {
        return ownerRepository.findAll(pageable);
    }

    /**
     * 更新飼主資料
     */
    public Owner update(Integer id, Owner ownerDetails) {
        Owner owner = findById(id);

        owner.setFirstName(ownerDetails.getFirstName());
        owner.setLastName(ownerDetails.getLastName());
        owner.setAddress(ownerDetails.getAddress());
        owner.setCity(ownerDetails.getCity());

        // 如果電話有變更，需檢查是否重複
        if (ownerDetails.getTelephone() != null
            && !ownerDetails.getTelephone().equals(owner.getTelephone())) {
            if (ownerRepository.existsByTelephone(ownerDetails.getTelephone())) {
                throw new DuplicateResourceException("此電話已被註冊");
            }
            owner.setTelephone(ownerDetails.getTelephone());
        }

        return ownerRepository.save(owner);
    }

    /**
     * 刪除飼主
     */
    public void delete(Integer id) {
        Owner owner = findById(id);
        ownerRepository.delete(owner);
        log.info("已刪除飼主，ID：{}", id);
    }
}
