package com.swiftcart.service;

import com.swiftcart.dto.AddressDtos.AddressRequest;
import com.swiftcart.dto.AddressDtos.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressResponse create(String email, AddressRequest request);
    List<AddressResponse> list(String email);
    AddressResponse get(String email, Long id);
    AddressResponse update(String email, Long id, AddressRequest request);
    void delete(String email, Long id);
    AddressResponse setDefault(String email, Long id);
}
