package org.springframework.samples.petclinic.customers.web.mapper;

import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.web.dto.OwnerDTO;
import org.springframework.samples.petclinic.customers.web.dto.PetDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 飼主物件轉換器（Mapper）
 *
 * Mapper 的職責是在 Entity 和 DTO 之間進行轉換。
 *
 * 為什麼需要 Mapper？
 *
 * 1. 單一職責原則（Single Responsibility Principle）：
 *    轉換邏輯集中在一個地方，方便維護。
 *    如果 Entity 或 DTO 結構改變，只需修改 Mapper。
 *
 * 2. 程式碼重用：
 *    多個 Controller 或 Service 可以共用同一個 Mapper。
 *
 * 3. 測試更容易：
 *    可以單獨測試轉換邏輯，不需要啟動整個應用程式。
 *
 * 進階選項：MapStruct
 *
 * 如果專案中有很多 Mapper，手動撰寫會很繁瑣。
 * 可以使用 MapStruct 函式庫自動產生 Mapper：
 *
 * @Mapper
 * public interface OwnerMapper {
 *     OwnerDTO toDTO(Owner owner);
 *     Owner toEntity(OwnerDTO dto);
 * }
 *
 * MapStruct 會在編譯時自動產生實作程式碼。
 *
 * @author Spring PetClinic
 */
public class OwnerMapper {

    /**
     * 私有建構子
     *
     * 這是一個工具類別（Utility Class），
     * 所有方法都是 static，不需要建立實例。
     *
     * 將建構子設為 private 可以：
     * 1. 防止其他程式碼建立實例
     * 2. 明確表達「這是工具類別」的意圖
     */
    private OwnerMapper() {
        // Utility class - 不允許實例化
    }

    /**
     * 將 Entity 轉換為 DTO
     *
     * 用於查詢操作：從資料庫取得資料後，轉換為前端需要的格式。
     *
     * @param owner Entity 物件（來自資料庫）
     * @return DTO 物件（給前端使用）
     */
    public static OwnerDTO toDTO(Owner owner) {
        // 防禦性程式設計：檢查 null
        if (owner == null) {
            return null;
        }

        // 建立新的 DTO 物件
        OwnerDTO dto = new OwnerDTO();

        // 複製基本欄位
        // 注意：這裡是值的複製，不是參考的複製
        dto.setId(owner.getId());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setAddress(owner.getAddress());
        dto.setCity(owner.getCity());
        dto.setTelephone(owner.getTelephone());

        // 處理寵物列表（一對多關聯）
        if (owner.getPets() != null && !owner.getPets().isEmpty()) {
            // 使用 Stream API 將每個 Pet Entity 轉換為 PetDTO
            List<PetDTO> petDTOs = owner.getPets().stream()
                .map(PetMapper::toDTO)  // 方法參考：呼叫 PetMapper.toDTO()
                .collect(Collectors.toList());  // 收集結果為 List
            dto.setPets(petDTOs);
        }

        return dto;
    }

    /**
     * 將 DTO 轉換為 Entity
     *
     * 用於新增/更新操作：將前端傳入的資料轉換為資料庫實體。
     *
     * 注意事項：
     * 1. 這裡不處理寵物列表的轉換
     *    因為新增飼主時通常不會同時新增寵物
     *
     * 2. 寵物的新增是透過獨立的 API（POST /owners/{id}/pets）
     *    這樣的設計更符合 REST 的資源概念
     *
     * @param dto DTO 物件（來自前端）
     * @return Entity 物件（準備存入資料庫）
     */
    public static Owner toEntity(OwnerDTO dto) {
        // 防禦性程式設計：檢查 null
        if (dto == null) {
            return null;
        }

        // 建立新的 Entity 物件
        Owner owner = new Owner();

        // 複製基本欄位
        owner.setId(dto.getId());
        owner.setFirstName(dto.getFirstName());
        owner.setLastName(dto.getLastName());
        owner.setAddress(dto.getAddress());
        owner.setCity(dto.getCity());
        owner.setTelephone(dto.getTelephone());

        // 注意：不處理 pets 列表
        // 寵物透過 OwnerService.addPet() 獨立新增

        return owner;
    }
}
