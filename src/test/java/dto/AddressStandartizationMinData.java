package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressStandartizationMinData {
    private String result;
    private String region_with_type;

    public AddressStandartizationMinData(String result, String region_with_type) {
        this.result = result;
        this.region_with_type = region_with_type;
    }

    public AddressStandartizationMinData(){}

    public String getResult() {
        return result;
    }

    public String getRegion_with_type() {
        return region_with_type;
    }
}
