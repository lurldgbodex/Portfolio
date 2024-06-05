package tech.sgcor.shipping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.sgcor.shipping.dto.CreateShippingAddressRequest;
import tech.sgcor.shipping.dto.ShippingAddressDto;
import tech.sgcor.shipping.model.ShippingAddress;
import tech.sgcor.shipping.repository.ShippingAddressRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingService {
    private final ShippingAddressRepository shippingAddressRepository;

    public List<ShippingAddressDto> getShippingAddressesByUser(String userId) {
        List<ShippingAddress> addresses = shippingAddressRepository.findByUserId(userId);

        return addresses.stream().map((address ->
                ShippingAddressDto
                        .builder()
                        .id(address.getId())
                        .user_id(address.getUserId())
                        .address(address.getAddress())
                        .city(address.getCity())
                        .postal_code(address.getAddress())
                        .build()
        )).toList();
    }

    public ShippingAddressDto addShippingAddress(String userId, CreateShippingAddressRequest request) {
        ShippingAddress address = new ShippingAddress();
        address.setUserId(userId);
        address.setAddress(request.getAddress());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostal_code());

        address = shippingAddressRepository.save(address);

        return ShippingAddressDto
                .builder()
                .id(address.getId())
                .user_id(address.getUserId())
                .address(address.getAddress())
                .city(address.getCity())
                .postal_code(address.getPostalCode())
                .build();
    }
}
