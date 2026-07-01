package com.swiftcart.service.impl;

import com.swiftcart.dto.AddressDtos.AddressRequest;
import com.swiftcart.dto.AddressDtos.AddressResponse;
import com.swiftcart.entity.Address;
import com.swiftcart.entity.User;
import com.swiftcart.enums.AddressType;
import com.swiftcart.exception.BusinessException;
import com.swiftcart.exception.ResourceNotFoundException;
import com.swiftcart.repository.AddressRepository;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AddressResponse create(String email, AddressRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Address address = new Address();
        address.setUser(user);
        apply(address, request);
        boolean firstAddress = addressRepository.findByUserEmailOrderByDefaultAddressDescUpdatedAtDesc(email).isEmpty();
        if (firstAddress || Boolean.TRUE.equals(request.defaultAddress())) {
            clearDefaults(email);
            markDefault(address, true);
        }
        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> list(String email) {
        return addressRepository.findByUserEmailOrderByDefaultAddressDescUpdatedAtDesc(email).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse get(String email, Long id) {
        return toResponse(address(email, id));
    }

    @Override
    public AddressResponse update(String email, Long id, AddressRequest request) {
        Address address = address(email, id);
        apply(address, request);
        if (Boolean.TRUE.equals(request.defaultAddress())) {
            clearDefaults(email);
            markDefault(address, true);
        }
        return toResponse(address);
    }

    @Override
    public void delete(String email, Long id) {
        Address address = address(email, id);
        boolean wasDefault = isDefault(address);
        addressRepository.delete(address);
        if (wasDefault) {
            addressRepository.findByUserEmailOrderByDefaultAddressDescUpdatedAtDesc(email).stream().findFirst()
                    .ifPresent(next -> markDefault(next, true));
        }
    }

    @Override
    public AddressResponse setDefault(String email, Long id) {
        Address address = address(email, id);
        clearDefaults(email);
        markDefault(address, true);
        return toResponse(address);
    }

    private Address address(String email, Long id) {
        return addressRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    private void apply(Address address, AddressRequest request) {
        String fullName = request.fullName().trim();
        String phoneNumber = request.phoneNumber().trim();
        String addressLine1 = request.addressLine1().trim();
        String addressLine2 = blankToNull(request.addressLine2());
        String city = request.city().trim();
        String state = request.state().trim();
        String postalCode = request.postalCode().trim();
        String country = request.country().trim();
        AddressType addressType = request.addressType();

        address.setFullName(fullName);
        address.setRecipientName(fullName);
        address.setPhoneNumber(phoneNumber);
        address.setLegacyPhone(phoneNumber);
        address.setAddressLine1(addressLine1);
        address.setLegacyLine1(addressLine1);
        address.setAddressLine2(addressLine2);
        address.setLegacyLine2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        address.setAddressType(addressType);
        address.setLabel(toLabel(addressType));

        if (Boolean.FALSE.equals(request.defaultAddress()) && isDefault(address)) {
            long count = addressRepository.findByUserEmailOrderByDefaultAddressDescUpdatedAtDesc(address.getUser().getEmail()).size();
            if (count <= 1) {
                throw new BusinessException("At least one default address is required");
            }
            markDefault(address, false);
        }
    }

    private void clearDefaults(String email) {
        addressRepository.findByUserEmailOrderByDefaultAddressDescUpdatedAtDesc(email)
                .forEach(address -> markDefault(address, false));
    }

    private void markDefault(Address address, boolean value) {
        address.setDefaultAddress(value);
        address.setLegacyDefaultAddress(value);
    }

    private boolean isDefault(Address address) {
        return address.isDefaultAddress() || address.isLegacyDefaultAddress();
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                firstNonBlank(address.getFullName(), address.getRecipientName()),
                firstNonBlank(address.getPhoneNumber(), address.getLegacyPhone()),
                firstNonBlank(address.getAddressLine1(), address.getLegacyLine1()),
                firstNonBlank(address.getAddressLine2(), address.getLegacyLine2()),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.getAddressType() == null ? fromLabel(address.getLabel()) : address.getAddressType(),
                isDefault(address)
        );
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }

    private String toLabel(AddressType type) {
        if (type == null) {
            return AddressType.HOME.name();
        }
        return switch (type) {
            case HOME -> "Home";
            case OFFICE -> "Office";
            case OTHER -> "Other";
        };
    }

    private AddressType fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return AddressType.HOME;
        }
        return switch (label.trim().toUpperCase()) {
            case "OFFICE" -> AddressType.OFFICE;
            case "OTHER" -> AddressType.OTHER;
            default -> AddressType.HOME;
        };
    }
}
