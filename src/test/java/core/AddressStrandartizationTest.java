package core;

import api.AddressStandartizationData;
import api.DeliveryData;
import api.Specification;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddressStrandartizationTest {

    private final static String URL = "https://cleaner.dadata.ru/api";

    @Before
    public void setUp() {
        RestAssured.reset();
    }

    @Test
    public void checkAddressStandartizationCorrectStreet(){
        /*
        Тестироваться будет API endpoint по которому передавая (почему-то) массив строк
        Суть данного теста в проверке, что по полученному значению в result после
        выполнения запроса совпадают с полем result поля street_with_type и region_with type.
        Таким образом мы проверям верный парсинг итогового востановленного результата.
         */

        Dotenv dotenv = Dotenv.load(); // Загрузка .env

        Specification.installSpecification(Specification.responseSpecOK(),
                Specification.requestSpec(URL, dotenv.get("API_KEY"))); //спецификации для легкой жизни

        String[] query ={"мск сухонска 11/-89"};
        List<String> addresses = Arrays.asList(query);

        /*
        Странно что несмотря на то что параметр является массивом, при этом сам массив по сути передать нельзя
        и даже не смотря на то что в ответе видно, что вернувшийся JSON содержит массив, как бы намекая, что все
        таки можно передать массив строк и получить ответ в виде массива, но сделать такового мне не удалось.
        */

        AddressStandartizationData element = given()
                .given()
                .header("X-Secret", dotenv.get("SECRET_KEY")) // Нативно добавил сюда секретку ибо запрос платный и требует секретного ключа
                .body(addresses)
                .when()
                .post("/v1/clean/address")
                .then().log().status().and().log().body()
                .extract().body()
                .jsonPath().getObject("[0]", AddressStandartizationData.class);

        assertTrue("Ошибка. Назавние региона не совпадает в result", element.getResult().contains(element.getRegion_with_type()));
        assertTrue("Ошибка. Название улицы не совпадает в result",element.getResult().contains(element.getStreet_with_type()));
    }
}
