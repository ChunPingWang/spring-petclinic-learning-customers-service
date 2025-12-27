package org.springframework.samples.petclinic.customers.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.customers.exception.ErrorResponse;
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

/**
 * 飼主 REST API 控制器
 *
 * Controller 層是三層式架構的最外層，負責處理 HTTP 請求。
 *
 * Controller 的職責：
 * 1. 接收 HTTP 請求
 * 2. 驗證輸入資料格式（使用 @Valid）
 * 3. 呼叫 Service 層處理業務邏輯
 * 4. 將結果轉換為適當的 HTTP 回應
 *
 * Controller 不應該包含業務邏輯！
 * 如果你發現自己在 Controller 寫 if-else 判斷業務規則，
 * 那應該把這些邏輯移到 Service 層。
 *
 * REST API 設計原則：
 * - GET: 查詢資料（不會修改伺服器狀態）
 * - POST: 新增資料
 * - PUT: 更新資料（完整更新）
 * - PATCH: 部分更新資料
 * - DELETE: 刪除資料
 *
 * OpenAPI 註解說明：
 * - @Tag: 為 API 分組，相同 tag 的 API 會顯示在一起
 * - @Operation: 描述單一 API 端點
 * - @ApiResponses: 定義可能的回應狀態
 * - @Parameter: 描述參數
 *
 * @author Spring PetClinic
 */
@RestController  // 標記為 REST 控制器，回傳值會自動轉為 JSON
@RequestMapping("/api/owners")  // 所有端點的基礎路徑
@RequiredArgsConstructor  // Lombok: 自動產生建構子注入
@Tag(name = "飼主管理", description = "飼主（Owner）的 CRUD 操作 API")
public class OwnerController {

    /**
     * 飼主業務邏輯服務
     */
    private final OwnerService ownerService;

    /**
     * 查詢飼主列表
     *
     * HTTP 方法：GET
     * 端點：/api/owners 或 /api/owners?lastName=王
     *
     * @param lastName （可選）姓氏篩選條件
     * @return 飼主列表
     *
     * @RequestParam: 從 URL 查詢參數取得值
     * - required = false: 此參數是可選的
     * - 範例：GET /api/owners?lastName=王
     */
    @Operation(
        summary = "查詢飼主列表",
        description = "取得所有飼主，或根據姓氏篩選"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查詢成功")
    })
    @GetMapping
    public ResponseEntity<List<OwnerDTO>> searchOwners(
            @Parameter(description = "姓氏篩選條件（可選）", example = "王")
            @RequestParam(required = false) String lastName) {

        // 根據是否有姓氏參數，決定查詢方式
        List<Owner> owners = lastName != null
            ? ownerService.findByLastName(lastName)
            : ownerService.findAll();

        // 將 Entity 轉換為 DTO
        // 為什麼要轉換？請參考 OwnerDTO 的說明
        List<OwnerDTO> dtos = owners.stream()
            .map(OwnerMapper::toDTO)  // 使用 Mapper 轉換每個元素
            .collect(Collectors.toList());

        // ResponseEntity.ok(): 回傳 HTTP 200 OK
        return ResponseEntity.ok(dtos);
    }

    /**
     * 分頁查詢飼主
     *
     * HTTP 方法：GET
     * 端點：/api/owners/page?page=0&size=10&sort=lastName,asc
     *
     * @param pageable Spring 自動解析的分頁參數
     *                 - page: 頁碼（從 0 開始）
     *                 - size: 每頁筆數
     *                 - sort: 排序欄位和方向
     * @return 分頁結果
     */
    @Operation(
        summary = "分頁查詢飼主",
        description = "支援分頁和排序的飼主查詢"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查詢成功")
    })
    @GetMapping("/page")
    public Page<OwnerDTO> findAll(
            @Parameter(description = "分頁參數（page, size, sort）")
            Pageable pageable) {
        // Page.map(): 將分頁中的每個元素轉換為 DTO
        return ownerService.findAll(pageable)
            .map(OwnerMapper::toDTO);
    }

    /**
     * 根據 ID 查詢單一飼主
     *
     * HTTP 方法：GET
     * 端點：/api/owners/{ownerId}
     * 範例：GET /api/owners/1
     *
     * @param ownerId 飼主 ID（從 URL 路徑取得）
     * @return 飼主資料
     *
     * @PathVariable: 從 URL 路徑取得參數
     * - /api/owners/123 中的 123 會被綁定到 ownerId
     */
    @Operation(
        summary = "查詢單一飼主",
        description = "根據飼主 ID 取得詳細資料"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查詢成功"),
        @ApiResponse(responseCode = "404", description = "找不到飼主",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerDTO> getOwner(
            @Parameter(description = "飼主 ID", example = "1")
            @PathVariable Integer ownerId) {
        Owner owner = ownerService.findById(ownerId);
        return ResponseEntity.ok(OwnerMapper.toDTO(owner));
    }

    /**
     * 新增飼主
     *
     * HTTP 方法：POST
     * 端點：/api/owners
     *
     * 請求範例：
     * POST /api/owners
     * Content-Type: application/json
     * {
     *   "firstName": "王",
     *   "lastName": "小明",
     *   "telephone": "0912345678"
     * }
     *
     * @param dto 飼主資料（從請求 body 取得）
     * @return 新增後的飼主（包含自動產生的 ID）
     *
     * @Valid: 觸發驗證，檢查 DTO 中的驗證註解（如 @NotBlank, @Pattern）
     * @RequestBody: 將請求的 JSON body 轉換為 Java 物件
     */
    @Operation(
        summary = "新增飼主",
        description = "建立新的飼主資料"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "建立成功"),
        @ApiResponse(responseCode = "400", description = "驗證失敗",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "電話號碼已被使用",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(
            @Valid @RequestBody OwnerDTO dto) {
        // 將 DTO 轉換為 Entity
        Owner owner = OwnerMapper.toEntity(dto);

        // 呼叫 Service 儲存
        Owner saved = ownerService.save(owner);

        // 回傳 HTTP 201 Created，表示資源已成功建立
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(OwnerMapper.toDTO(saved));
    }

    /**
     * 更新飼主資料
     *
     * HTTP 方法：PUT
     * 端點：/api/owners/{ownerId}
     *
     * @param ownerId 飼主 ID
     * @param dto 新的飼主資料
     * @return 更新後的飼主
     */
    @Operation(
        summary = "更新飼主",
        description = "更新現有飼主的資料"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "驗證失敗",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "找不到飼主",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "電話號碼已被使用",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{ownerId}")
    public ResponseEntity<OwnerDTO> updateOwner(
            @Parameter(description = "飼主 ID", example = "1")
            @PathVariable Integer ownerId,
            @Valid @RequestBody OwnerDTO dto) {
        Owner ownerDetails = OwnerMapper.toEntity(dto);
        Owner updated = ownerService.update(ownerId, ownerDetails);
        return ResponseEntity.ok(OwnerMapper.toDTO(updated));
    }

    /**
     * 刪除飼主
     *
     * HTTP 方法：DELETE
     * 端點：/api/owners/{ownerId}
     *
     * @param ownerId 飼主 ID
     * @return HTTP 204 No Content（表示成功但無回傳內容）
     */
    @Operation(
        summary = "刪除飼主",
        description = "刪除飼主及其所有寵物"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "刪除成功"),
        @ApiResponse(responseCode = "404", description = "找不到飼主",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(
            @Parameter(description = "飼主 ID", example = "1")
            @PathVariable Integer ownerId) {
        ownerService.delete(ownerId);
        // noContent(): 回傳 HTTP 204，表示成功刪除，無需回傳內容
        return ResponseEntity.noContent().build();
    }

    /**
     * 為飼主新增寵物
     *
     * HTTP 方法：POST
     * 端點：/api/owners/{ownerId}/pets
     *
     * 這是一個巢狀資源的設計：
     * - 寵物屬於飼主，所以路徑是 /owners/{id}/pets
     * - 這樣的 URL 設計清楚表達了資源的從屬關係
     *
     * @param ownerId 飼主 ID
     * @param dto 寵物資料
     * @return 新增的寵物
     */
    @Operation(
        summary = "新增寵物",
        description = "為指定飼主新增一隻寵物"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "建立成功"),
        @ApiResponse(responseCode = "400", description = "驗證失敗或業務規則錯誤",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "找不到飼主",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{ownerId}/pets")
    public ResponseEntity<PetDTO> addPet(
            @Parameter(description = "飼主 ID", example = "1")
            @PathVariable Integer ownerId,
            @Valid @RequestBody PetDTO dto) {

        // 將 DTO 轉換為 Entity
        Pet pet = PetMapper.toEntity(dto);

        // 呼叫 Service 新增寵物
        ownerService.addPet(ownerId, pet);

        // 回傳 HTTP 201 Created
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(PetMapper.toDTO(pet));
    }
}
