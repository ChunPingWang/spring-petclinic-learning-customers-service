package org.springframework.samples.petclinic.customers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.web.dto.OwnerDTO;
import org.springframework.samples.petclinic.customers.web.dto.PetDTO;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("測試：新增飼主的完整流程")
    void testCreateOwner() throws Exception {
        OwnerDTO dto = new OwnerDTO();
        dto.setFirstName("小美");
        dto.setLastName("陳");
        dto.setAddress("台中市西屯區");
        dto.setCity("台中市");
        dto.setTelephone("0912345670");

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("小美"))
            .andExpect(jsonPath("$.lastName").value("陳"));
    }

    @Test
    @DisplayName("測試：電話格式錯誤應回傳 400")
    void testCreateOwner_InvalidTelephone() throws Exception {
        OwnerDTO dto = new OwnerDTO();
        dto.setFirstName("小美");
        dto.setLastName("陳");
        dto.setTelephone("12345");  // 錯誤格式

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("測試：查詢所有飼主")
    void testGetAllOwners() throws Exception {
        mockMvc.perform(get("/api/owners"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("測試：根據姓氏查詢飼主")
    void testSearchOwnersByLastName() throws Exception {
        // 先新增一個飼主
        OwnerDTO dto = new OwnerDTO();
        dto.setFirstName("小明");
        dto.setLastName("測試姓");
        dto.setTelephone("0988776655");

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());

        // 查詢
        mockMvc.perform(get("/api/owners")
                .param("lastName", "測試姓"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("測試姓"));
    }

    @Test
    @DisplayName("測試：為飼主新增寵物")
    void testAddPetToOwner() throws Exception {
        // 先新增一個飼主
        OwnerDTO ownerDto = new OwnerDTO();
        ownerDto.setFirstName("小明");
        ownerDto.setLastName("王");
        ownerDto.setTelephone("0911223344");

        String ownerResponse = mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerDto)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        OwnerDTO createdOwner = objectMapper.readValue(ownerResponse, OwnerDTO.class);

        // 新增寵物
        PetDTO petDto = new PetDTO();
        petDto.setName("小黑");
        petDto.setBirthDate(LocalDate.of(2021, 5, 10));

        mockMvc.perform(post("/api/owners/" + createdOwner.getId() + "/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("小黑"));
    }

    @Test
    @DisplayName("測試：查詢不存在的飼主應回傳 404")
    void testGetOwner_NotFound() throws Exception {
        mockMvc.perform(get("/api/owners/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("測試：更新飼主資料")
    void testUpdateOwner() throws Exception {
        // 先新增一個飼主
        OwnerDTO dto = new OwnerDTO();
        dto.setFirstName("原名");
        dto.setLastName("王");
        dto.setTelephone("0955667788");

        String response = mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        OwnerDTO createdOwner = objectMapper.readValue(response, OwnerDTO.class);

        // 更新
        createdOwner.setFirstName("新名");
        createdOwner.setCity("高雄市");

        mockMvc.perform(put("/api/owners/" + createdOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdOwner)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("新名"))
            .andExpect(jsonPath("$.city").value("高雄市"));
    }

    @Test
    @DisplayName("測試：刪除飼主")
    void testDeleteOwner() throws Exception {
        // 先新增一個飼主
        OwnerDTO dto = new OwnerDTO();
        dto.setFirstName("待刪除");
        dto.setLastName("李");
        dto.setTelephone("0944556677");

        String response = mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        OwnerDTO createdOwner = objectMapper.readValue(response, OwnerDTO.class);

        // 刪除
        mockMvc.perform(delete("/api/owners/" + createdOwner.getId()))
            .andExpect(status().isNoContent());

        // 確認已刪除
        mockMvc.perform(get("/api/owners/" + createdOwner.getId()))
            .andExpect(status().isNotFound());
    }
}
