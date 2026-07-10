package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressStandartizationData {
    private String result;
    private String region_with_type;
    private String region_type;
    private String region_type_full;
    private String region;
    private String street_with_type;
    private String street_type;
    private String street_type_full;
    private String street;
    private String house;
    private String flat;

    public AddressStandartizationData(String result, String flat, String house, String street, String street_type_full, String street_type, String street_with_type, String region, String region_type_full, String region_type, String region_with_type) {
        this.result = result;
        this.flat = flat;
        this.house = house;
        this.street = street;
        this.street_type_full = street_type_full;
        this.street_type = street_type;
        this.street_with_type = street_with_type;
        this.region = region;
        this.region_type_full = region_type_full;
        this.region_type = region_type;
        this.region_with_type = region_with_type;
    }

    public AddressStandartizationData(){}

    public String getResult() {
        return result;
    }

    public String getRegion_with_type() {
        return region_with_type;
    }

    public String getRegion_type() {
        return region_type;
    }

    public String getRegion_type_full() {
        return region_type_full;
    }

    public String getRegion() {
        return region;
    }

    public String getStreet_with_type() {
        return street_with_type;
    }

    public String getStreet_type() {
        return street_type;
    }

    public String getStreet_type_full() {
        return street_type_full;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public String getFlat() {
        return flat;
    }
}
