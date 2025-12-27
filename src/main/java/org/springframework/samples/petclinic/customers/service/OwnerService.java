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

/**
 * 飼主業務邏輯服務類別
 *
 * Service 層是三層式架構的核心，負責處理所有業務邏輯。
 *
 * 為什麼需要 Service 層？
 *
 * 1. 業務邏輯集中管理：
 *    所有的商業規則都寫在這裡，方便維護和修改。
 *    例如：電話不能重複、寵物數量上限、生日驗證等。
 *
 * 2. 與 Controller 分離：
 *    Controller 只負責接收 HTTP 請求和回傳結果，
 *    不處理業務邏輯。這樣即使 API 規格改變，
 *    業務邏輯也不需要修改。
 *
 * 3. 方便單元測試：
 *    可以單獨測試 Service 層，不需要啟動 Web Server，
 *    測試速度更快，也更容易模擬各種情境。
 *
 * 4. 交易管理：
 *    使用 @Transactional 確保資料一致性，
 *    如果操作過程中發生錯誤，會自動回滾所有變更。
 *
 * @author Spring PetClinic
 */
@Slf4j  // Lombok: 自動產生 log 物件，用於記錄日誌
@Service  // 標記此類別為 Spring 的 Service 元件
@RequiredArgsConstructor  // Lombok: 自動產生包含所有 final 欄位的建構子（用於依賴注入）
@Transactional  // 所有方法預設都在資料庫交易中執行
public class OwnerService {

    /**
     * 飼主資料存取物件
     *
     * 使用建構子注入（Constructor Injection）而非 @Autowired：
     * - 欄位可以是 final，確保不會被意外修改
     * - 依賴關係更明確，更容易進行單元測試
     * - 如果忘記注入，編譯時就會報錯
     */
    private final OwnerRepository ownerRepository;

    /**
     * 儲存飼主
     *
     * 業務規則：
     * 1. 電話號碼不能與現有飼主重複
     * 2. 同一飼主的寵物名字不能重複
     *
     * @param owner 要儲存的飼主資料
     * @return 儲存後的飼主（包含自動產生的 ID）
     * @throws DuplicateResourceException 如果電話號碼已存在
     * @throws BusinessRuleException 如果寵物名字重複
     */
    public Owner save(Owner owner) {
        // 記錄操作日誌（方便問題追蹤）
        log.info("嘗試儲存飼主：{} {}", owner.getFirstName(), owner.getLastName());

        // 業務規則 1：電話號碼不能重複
        // 先檢查電話是否已被其他飼主使用
        if (owner.getTelephone() != null && ownerRepository.existsByTelephone(owner.getTelephone())) {
            throw new DuplicateResourceException("此電話已被註冊");
        }

        // 業務規則 2：同一飼主不能有重複的寵物名字
        // 使用 Stream API 收集所有寵物名字到 Set（Set 不允許重複）
        // 如果 Set 的大小與原始列表不同，表示有重複的名字
        if (owner.getPets() != null && !owner.getPets().isEmpty()) {
            Set<String> petNames = owner.getPets().stream()
                .map(Pet::getName)  // 取得每隻寵物的名字
                .collect(Collectors.toSet());  // 收集到 Set

            if (petNames.size() != owner.getPets().size()) {
                throw new BusinessRuleException("寵物名字不能重複");
            }
        }

        // 執行儲存，並處理可能的例外
        try {
            Owner saved = ownerRepository.save(owner);
            log.info("飼主儲存成功，ID：{}", saved.getId());
            return saved;
        } catch (Exception e) {
            // 記錄錯誤日誌（注意：不要記錄敏感資訊如密碼）
            log.error("儲存飼主失敗：{}", owner, e);
            throw e;  // 重新拋出例外，讓上層處理
        }
    }

    /**
     * 為飼主新增寵物
     *
     * 業務規則：
     * 1. 飼主必須存在
     * 2. 寵物生日不能是未來的日期
     * 3. 每個飼主最多只能登記 10 隻寵物
     * 4. 同一飼主的寵物名字不能重複
     *
     * @param ownerId 飼主 ID
     * @param pet 要新增的寵物
     * @throws ResourceNotFoundException 如果找不到飼主
     * @throws BusinessRuleException 如果違反業務規則
     */
    public void addPet(Integer ownerId, Pet pet) {
        // 查詢飼主，找不到則拋出例外
        // orElseThrow: 如果 Optional 是空的，就執行 lambda 並拋出例外
        Owner owner = ownerRepository.findByIdWithPets(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("找不到此飼主"));

        // 業務規則 3：寵物生日不能是未來
        // LocalDate.now() 取得今天的日期
        // isAfter() 檢查日期是否在指定日期之後
        if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("寵物生日不能是未來日期");
        }

        // 業務規則 4：每個飼主最多養 10 隻寵物
        if (owner.getPets().size() >= 10) {
            throw new BusinessRuleException("每個飼主最多只能登記 10 隻寵物");
        }

        // 業務規則 5：檢查寵物名字是否重複
        // anyMatch: 檢查是否有任何元素符合條件
        boolean nameExists = owner.getPets().stream()
            .anyMatch(p -> p.getName().equals(pet.getName()));
        if (nameExists) {
            throw new BusinessRuleException("寵物名字不能重複");
        }

        // 新增寵物並儲存
        // addPet 方法會維護雙向關聯
        owner.addPet(pet);
        ownerRepository.save(owner);
        log.info("成功為飼主 {} 新增寵物 {}", ownerId, pet.getName());
    }

    /**
     * 根據 ID 查詢飼主
     *
     * @Transactional(readOnly = true): 標記此方法只讀取資料，
     * 資料庫可以進行優化（例如不需要建立交易日誌）
     *
     * @Cacheable: 快取查詢結果
     * - value = "owners": 快取的名稱
     * - key = "#id": 使用 ID 作為快取的 key
     * 下次查詢相同 ID 時，會直接從快取取得，不需要查詢資料庫
     *
     * @param id 飼主 ID
     * @return 飼主資料
     * @throws ResourceNotFoundException 如果找不到飼主
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "owners", key = "#id")
    public Owner findById(Integer id) {
        return ownerRepository.findByIdWithPets(id)
            .orElseThrow(() -> new ResourceNotFoundException("找不到此飼主"));
    }

    /**
     * 根據姓氏查詢飼主
     *
     * @param lastName 姓氏
     * @return 符合條件的飼主列表
     */
    @Transactional(readOnly = true)
    public List<Owner> findByLastName(String lastName) {
        return ownerRepository.findByLastName(lastName);
    }

    /**
     * 查詢所有飼主
     *
     * @return 所有飼主列表
     */
    @Transactional(readOnly = true)
    public List<Owner> findAll() {
        return ownerRepository.findAllWithPets();
    }

    /**
     * 分頁查詢所有飼主
     *
     * @param pageable 分頁參數
     * @return 分頁結果
     */
    @Transactional(readOnly = true)
    public Page<Owner> findAll(Pageable pageable) {
        return ownerRepository.findAll(pageable);
    }

    /**
     * 更新飼主資料
     *
     * @param id 飼主 ID
     * @param ownerDetails 新的飼主資料
     * @return 更新後的飼主
     * @throws ResourceNotFoundException 如果找不到飼主
     * @throws DuplicateResourceException 如果新電話號碼已被使用
     */
    public Owner update(Integer id, Owner ownerDetails) {
        // 先查詢現有飼主
        Owner owner = findById(id);

        // 更新基本資料
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
     *
     * 注意：由於 Owner 與 Pet 的關聯設定了 cascade = ALL，
     * 刪除飼主時會一併刪除其所有寵物。
     *
     * @param id 飼主 ID
     * @throws ResourceNotFoundException 如果找不到飼主
     */
    public void delete(Integer id) {
        Owner owner = findById(id);
        ownerRepository.delete(owner);
        log.info("已刪除飼主，ID：{}", id);
    }
}
