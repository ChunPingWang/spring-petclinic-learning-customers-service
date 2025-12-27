package org.springframework.samples.petclinic.customers.web.mapper;

import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.web.dto.PetDTO;

/**
 * 寵物物件轉換器（Mapper）
 *
 * 負責 Pet Entity 和 PetDTO 之間的轉換。
 *
 * 轉換策略：
 *
 * Entity → DTO（toDTO）：
 * - 展開關聯物件：PetType → typeId + typeName
 * - 展開關聯物件：Owner → ownerId
 * - 這樣 JSON 是扁平的，前端更容易處理
 *
 * DTO → Entity（toEntity）：
 * - 只複製基本欄位（id, name, birthDate）
 * - 不設定 type 和 owner，因為需要從資料庫查詢
 * - 關聯的設定由 Service 層處理
 *
 * @author Spring PetClinic
 */
public class PetMapper {

    /**
     * 私有建構子
     *
     * 工具類別的標準寫法：
     * 所有方法都是 static，不需要實例化。
     */
    private PetMapper() {
        // Utility class - 不允許實例化
    }

    /**
     * 將 Pet Entity 轉換為 PetDTO
     *
     * 處理邏輯：
     * 1. 複製基本屬性（id, name, birthDate）
     * 2. 展開 PetType 關聯為 typeId 和 typeName
     * 3. 展開 Owner 關聯為 ownerId
     *
     * 為什麼要「展開」關聯？
     * - 避免 JSON 巢狀太深
     * - 避免循環引用
     * - 前端可能只需要 ID，不需要完整物件
     *
     * @param pet Entity 物件
     * @return DTO 物件
     */
    public static PetDTO toDTO(Pet pet) {
        // 防禦性程式設計：處理 null 輸入
        if (pet == null) {
            return null;
        }

        // 建立 DTO 並複製基本屬性
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setBirthDate(pet.getBirthDate());

        // 展開 PetType 關聯
        // 需要檢查 null，因為 type 可能尚未設定
        if (pet.getType() != null) {
            dto.setTypeId(pet.getType().getId());
            dto.setTypeName(pet.getType().getName());
        }

        // 展開 Owner 關聯
        // 需要檢查 null，因為寵物可能還沒指定飼主
        if (pet.getOwner() != null) {
            dto.setOwnerId(pet.getOwner().getId());
        }

        return dto;
    }

    /**
     * 將 PetDTO 轉換為 Pet Entity
     *
     * 注意：這個轉換是「部分」的
     *
     * 只處理的欄位：
     * - id: 用於更新時識別
     * - name: 寵物名字
     * - birthDate: 生日
     *
     * 不處理的欄位：
     * - type: 需要根據 typeId 從資料庫查詢 PetType
     * - owner: 由 Service 層設定（透過 addPet 方法）
     *
     * 為什麼關聯不在 Mapper 處理？
     *
     * Mapper 是純粹的資料轉換，不應該：
     * - 查詢資料庫
     * - 依賴 Repository
     * - 包含業務邏輯
     *
     * 關聯的設定需要查詢資料庫，所以應該在 Service 層處理。
     * 這樣可以保持 Mapper 的簡單和可測試性。
     *
     * @param dto DTO 物件
     * @return Entity 物件（部分填充）
     */
    public static Pet toEntity(PetDTO dto) {
        // 防禦性程式設計：處理 null 輸入
        if (dto == null) {
            return null;
        }

        // 建立 Entity 並複製基本屬性
        Pet pet = new Pet();
        pet.setId(dto.getId());
        pet.setName(dto.getName());
        pet.setBirthDate(dto.getBirthDate());

        // 注意：不設定 type 和 owner
        // 這些關聯由 Service 層處理

        return pet;
    }
}
