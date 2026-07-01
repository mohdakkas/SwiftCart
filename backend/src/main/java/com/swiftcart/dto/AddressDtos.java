package com.swiftcart.dto;

import com.swiftcart.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AddressDtos {
    private AddressDtos() {
    }

    public record AddressRequest(
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit phone number") String phoneNumber,
            @NotBlank @Size(max = 220) String addressLine1,
            @Size(max = 220) String addressLine2,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Size(max = 100) String state,
            @NotBlank @Pattern(regexp = "^\\d{5,6}$", message = "Enter a valid postal code") String postalCode,
            @NotBlank @Size(max = 80) String country,
            @NotNull AddressType addressType,
            Boolean defaultAddress
    ) {
    }

    public record AddressResponse(
            Long id,
            String fullName,
            String phoneNumber,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country,
            AddressType addressType,
            boolean defaultAddress
    ) {
    }
}
