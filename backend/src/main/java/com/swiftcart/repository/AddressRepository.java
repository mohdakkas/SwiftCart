package com.swiftcart.repository;

import com.swiftcart.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserEmailOrderByDefaultAddressDescUpdatedAtDesc(String email);
    Optional<Address> findByIdAndUserEmail(Long id, String email);
}
