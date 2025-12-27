package org.springframework.samples.petclinic.customers.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.customers.exception.BusinessRuleException;
import org.springframework.samples.petclinic.customers.exception.DuplicateResourceException;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.repository.OwnerRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    @DisplayName("測試：新增寵物時檢查名字重複")
    void testAddPet_DuplicateName() {
        // Given：飼主已經有一隻叫「小白」的狗
        Owner owner = new Owner("小明", "王");
        Pet existingPet = new Pet("小白", LocalDate.of(2020, 5, 10));
        owner.addPet(existingPet);

        when(ownerRepository.findByIdWithPets(1)).thenReturn(Optional.of(owner));

        // When & Then：嘗試新增另一隻叫「小白」的貓，應該要報錯
        Pet duplicatePet = new Pet("小白", LocalDate.of(2021, 3, 15));

        assertThatThrownBy(() -> ownerService.addPet(1, duplicatePet))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("寵物名字不能重複");

        // 驗證：不應該呼叫 save
        verify(ownerRepository, never()).save(any());
    }

    @Test
    @DisplayName("測試：寵物生日不能是未來")
    void testAddPet_FutureBirthDate() {
        Owner owner = new Owner("小明", "王");
        when(ownerRepository.findByIdWithPets(1)).thenReturn(Optional.of(owner));

        Pet futurePet = new Pet("時光機", LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> ownerService.addPet(1, futurePet))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("寵物生日不能是未來日期");
    }

    @Test
    @DisplayName("測試：每個飼主最多養 10 隻寵物")
    void testAddPet_MaxPetsExceeded() {
        // Given：飼主已經有 10 隻寵物
        Owner owner = new Owner("小明", "王");
        for (int i = 1; i <= 10; i++) {
            Pet pet = new Pet("寵物" + i, LocalDate.of(2020, 1, i));
            owner.addPet(pet);
        }

        when(ownerRepository.findByIdWithPets(1)).thenReturn(Optional.of(owner));

        // When & Then
        Pet newPet = new Pet("第11隻", LocalDate.of(2021, 1, 1));

        assertThatThrownBy(() -> ownerService.addPet(1, newPet))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("每個飼主最多只能登記 10 隻寵物");
    }

    @Test
    @DisplayName("測試：電話號碼不能重複")
    void testSave_DuplicateTelephone() {
        // Given
        Owner owner = new Owner("小明", "王");
        owner.setTelephone("0912345678");

        when(ownerRepository.existsByTelephone("0912345678")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> ownerService.save(owner))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("此電話已被註冊");

        verify(ownerRepository, never()).save(any());
    }

    @Test
    @DisplayName("測試：成功新增寵物")
    void testAddPet_Success() {
        // Given
        Owner owner = new Owner("小明", "王");
        when(ownerRepository.findByIdWithPets(1)).thenReturn(Optional.of(owner));
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        // When
        Pet newPet = new Pet("小黑", LocalDate.of(2021, 5, 10));
        ownerService.addPet(1, newPet);

        // Then
        verify(ownerRepository).save(owner);
    }
}
