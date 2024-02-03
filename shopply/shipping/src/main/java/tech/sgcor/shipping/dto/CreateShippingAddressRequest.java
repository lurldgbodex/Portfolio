package tech.sgcor.shipping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateShippingAddressRequest {
    @NotBlank(message = "address is required")
    private String address;
    @NotBlank(message = "city is required")
    private String city;
    @NotBlank(message = "postal_code is required")
    private String postal_code;
}
