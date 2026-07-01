package com.swiftcart.controller;

import com.swiftcart.dto.AddressDtos.AddressRequest;
import com.swiftcart.dto.AddressDtos.AddressResponse;
import com.swiftcart.dto.ApiResponse;
import com.swiftcart.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/addresses", "/api/v1/addresses"})
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(Authentication authentication, @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Address added", addressService.create(authentication.getName(), request)));
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> list(Authentication authentication) {
        return ApiResponse.ok("Addresses loaded", addressService.list(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> get(Authentication authentication, @PathVariable Long id) {
        return ApiResponse.ok("Address loaded", addressService.get(authentication.getName(), id));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(Authentication authentication, @PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return ApiResponse.ok("Address updated", addressService.update(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long id) {
        addressService.delete(authentication.getName(), id);
        return ApiResponse.ok("Address deleted", null);
    }

    @PutMapping("/{id}/default")
    public ApiResponse<AddressResponse> setDefault(Authentication authentication, @PathVariable Long id) {
        return ApiResponse.ok("Default address updated", addressService.setDefault(authentication.getName(), id));
    }
}
