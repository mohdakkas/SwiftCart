package com.swiftcart.entity;

import com.swiftcart.enums.AddressType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 120)
    private String fullName;

    @Column(name = "recipient_name", length = 120)
    private String recipientName;

    @Column(length = 20)
    private String phoneNumber;

    @Column(name = "phone", length = 20)
    private String legacyPhone;

    @Column(length = 220)
    private String addressLine1;

    @Column(name = "line1", length = 220)
    private String legacyLine1;

    @Column(length = 220)
    private String addressLine2;

    @Column(name = "line2", length = 220)
    private String legacyLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 80)
    private String country = "India";

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AddressType addressType = AddressType.HOME;

    @Column(name = "label", length = 40)
    private String label;

    @Column(name = "is_default")
    private boolean defaultAddress;

    @Column(name = "default_address")
    private boolean legacyDefaultAddress;
}

