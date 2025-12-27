package org.springframework.samples.petclinic.customers.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDTO {

    private Integer id;

    @NotBlank(message = "名字不能為空")
    private String firstName;

    @NotBlank(message = "姓氏不能為空")
    private String lastName;

    private String address;

    private String city;

    @Pattern(regexp = "\\d{10}", message = "電話必須是10位數字")
    private String telephone;

    private List<PetDTO> pets = new ArrayList<>();
}
