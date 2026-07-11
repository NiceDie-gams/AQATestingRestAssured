package util;

public enum ApiEndpoints {
    DELIVERY_ID_URL("https://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/delivery"),
    ADDRESS_STANDARTIZATION_URL("https://cleaner.dadata.ru/api/v1/clean/address");

    private final String path;

    ApiEndpoints(String path){
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }



}
