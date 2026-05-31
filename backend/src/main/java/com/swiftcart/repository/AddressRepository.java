package com.swiftcart.repository;

import com.swiftcart.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserEmailOrderByDefaultAddressDesc(String email);
}
