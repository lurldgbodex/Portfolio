package tech.sgcor.customer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String first_name;
    private String last_name;
    private String other_name;
    private String email;

}
