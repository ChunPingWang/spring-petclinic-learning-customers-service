package org.springframework.samples.petclinic.customers.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.samples.petclinic.customers.model.Owner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OwnerRepositoryTest {

    @Autowired
    private OwnerRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("測試：根據姓氏模糊查詢")
    void testFindByLastName() {
        // Given：準備測試資料
        Owner owner1 = new Owner("小明", "王", "台北市");
        Owner owner2 = new Owner("小華", "王", "新北市");
        Owner owner3 = new Owner("大偉", "李", "桃園市");

        entityManager.persist(owner1);
        entityManager.persist(owner2);
        entityManager.persist(owner3);
        entityManager.flush();

        // When：執行查詢
        List<Owner> result = repository.findByLastName("王");

        // Then：驗證結果
        assertThat(result).hasSize(2);
        assertThat(result).extracting("firstName")
            .containsExactlyInAnyOrder("小明", "小華");
    }

    @Test
    @DisplayName("測試：查詢不存在的姓氏應回傳空列表")
    void testFindByLastName_NotFound() {
        List<Owner> result = repository.findByLastName("不存在的姓");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("測試：檢查電話是否存在")
    void testExistsByTelephone() {
        // Given
        Owner owner = new Owner("小明", "王");
        owner.setTelephone("0912345678");
        entityManager.persist(owner);
        entityManager.flush();

        // When & Then
        assertThat(repository.existsByTelephone("0912345678")).isTrue();
        assertThat(repository.existsByTelephone("0999999999")).isFalse();
    }

    @Test
    @DisplayName("測試：根據 ID 查詢並載入寵物")
    void testFindByIdWithPets() {
        // Given
        Owner owner = new Owner("小明", "王");
        owner.setTelephone("0912345678");
        entityManager.persist(owner);
        entityManager.flush();

        // When
        var result = repository.findByIdWithPets(owner.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("小明");
    }
}
