package tech.sgcor.shipping.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShippingAddressDto {
    private Long id;
    private String user_id;
    private String address;
    private String city;
    private String postal_code;
}
