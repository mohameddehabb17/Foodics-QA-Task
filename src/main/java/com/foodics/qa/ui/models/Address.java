package com.foodics.qa.ui.models;

import java.util.Objects;

/**
 * Immutable shipping address model used in checkout automation.
 */
public class Address {
    private final String fullName;
    private final String phone;
    private final String street;
    private final String building;
    private final String city;
    private final String district;
    private final String governorate;
    private final String landmark;

    /**
     * Creates a fully defined address model.
     *
     * @param fullName recipient full name
     * @param phone recipient phone number
     * @param street street name
     * @param building building identifier
     * @param city city name
     * @param district district name
     * @param governorate governorate name
     * @param landmark nearby landmark
     */
    public Address(String fullName, String phone, String street, String building,
                   String city, String district, String governorate, String landmark) {
        this.fullName = Objects.requireNonNull(fullName, "fullName is required");
        this.phone = Objects.requireNonNull(phone, "phone is required");
        this.street = Objects.requireNonNull(street, "street is required");
        this.building = Objects.requireNonNull(building, "building is required");
        this.city = Objects.requireNonNull(city, "city is required");
        this.district = Objects.requireNonNull(district, "district is required");
        this.governorate = Objects.requireNonNull(governorate, "governorate is required");
        this.landmark = Objects.requireNonNull(landmark, "landmark is required");
    }

    /**
     * @return recipient full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @return recipient phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @return street name
     */
    public String getStreet() {
        return street;
    }

    /**
     * @return building identifier
     */
    public String getBuilding() {
        return building;
    }

    /**
     * @return city name
     */
    public String getCity() {
        return city;
    }

    /**
     * @return district name
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @return governorate name
     */
    public String getGovernorate() {
        return governorate;
    }

    /**
     * @return nearby landmark
     */
    public String getLandmark() {
        return landmark;
    }
}

