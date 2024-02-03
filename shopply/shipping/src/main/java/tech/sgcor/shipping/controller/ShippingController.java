package tech.sgcor.shipping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.shipping.dto.CreateShippingAddressRequest;
import tech.sgcor.shipping.dto.ShippingAddressDto;
import tech.sgcor.shipping.service.ShippingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shipping")
public class ShippingController {
    private final ShippingService shippingService;

    @GetMapping("/addresses/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ShippingAddressDto> getShippingAddresses(@PathVariable(name = "userId") String userId) {
        return shippingService.getShippingAddressesByUser(userId);
    }

    @PostMapping("/address/{userId}/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ShippingAddressDto addShippingAddress(@PathVariable(name = "userId") String id,
                                                                 @RequestBody @Valid CreateShippingAddressRequest request) {
        return shippingService.addShippingAddress(id, request);
    }
}
