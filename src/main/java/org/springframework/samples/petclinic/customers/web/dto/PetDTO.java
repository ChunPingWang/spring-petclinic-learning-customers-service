package org.springframework.samples.petclinic.customers.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {

    private Integer id;

    @NotBlank(message = "寵物名字不能為空")
    private String name;

    private LocalDate birthDate;

    private String typeName;

    private Integer typeId;

    private Integer ownerId;
}
