package dto;

public class DeliveryData {
    private String kladr_id;
    private String fias_id;
    private String boxberry_id;
    private String cdek_id;
    private String dpd_id;

    public DeliveryData(){}

    public DeliveryData(String fias_id, String kladr_id, String boxberry_id, String cdek_id, String dpd_id) {
        this.fias_id = fias_id;
        this.kladr_id = kladr_id;
        this.boxberry_id = boxberry_id;
        this.cdek_id = cdek_id;
        this.dpd_id = dpd_id;
    }

    public String getKladr_id() {
        return kladr_id;
    }

    public String getFias_id() {
        return fias_id;
    }

    public String getBoxberry_id() {
        return boxberry_id;
    }

    public String getCdek_id() {
        return cdek_id;
    }

    public String getDpd_id() {
        return dpd_id;
    }
}
