package tests;

import dto.AddressStandartizationData;
import dto.AddressStandartizationMinData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.Specification;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


public class AddressStrandartizationTest {

    private final static String URL = "https://cleaner.dadata.ru/api";

    @BeforeEach
    public void setUp() {
        RestAssured.reset();
    }

    @ParameterizedTest(name="Тестовый адрес {0}")
    @MethodSource("util.TestDataLoader#getAddressForTest")
    @DisplayName("Проверка соотеветствия адреса в разных полях ответа от API")
    public void checkAddressStandartizationCorrectStreet(String address){
        /*
        Тестироваться будет API endpoint по которому передавая (почему-то) массив строк
        Суть данного теста в проверке, что по полученному значению в result после
        выполнения запроса совпадают с полем result поля street_with_type и region_with type.
        Таким образом мы проверям верный парсинг итогового востановленного результата.
         */

        Dotenv dotenv = Dotenv.load(); // Загрузка .env

        List<String> addresses = Arrays.asList(address);

        /*
        Странно что несмотря на то что параметр является массивом, при этом сам массив по сути передать нельзя
        и даже не смотря на то что в ответе видно, что вернувшийся JSON содержит массив, как бы намекая, что все
        таки можно передать массив строк и получить ответ в виде массива, но сделать такового мне не удалось.
        */

        AddressStandartizationData element = given()
                .spec(Specification.requestSpec(URL, dotenv.get("API_KEY")))
                .header("X-Secret", dotenv.get("SECRET_KEY")) // Нативно добавил сюда секретку ибо запрос платный и требует секретного ключа
                .body(addresses)
                .when()
                .post("/v1/clean/address")
                .then()
                .spec(Specification.responseSpecOK())
                .log().status().and().log().body()
                .extract().body()
                .jsonPath().getObject("[0]", AddressStandartizationData.class);

        assertTrue(element.getResult().contains(element.getRegion_with_type()), "Ошибка. Назавние региона не совпадает в result");
        assertTrue(element.getResult().contains(element.getStreet_with_type()), "Ошибка. Название улицы не совпадает в result");
    }

    @ParameterizedTest(name="Проверка адреса [{0}], корректный регион [{1}]")
    @MethodSource("util.TestDataLoader#getAddressAndRegion")
    @DisplayName("Проверка, что все адреса принадлежат одному региону")
    public void correctRegionRecognizeTest(String address, String correctRegion){
        /*
        Тест
        Проверка на то верно ли программа опредлеяет регион для разных адресов из одного региона.
        Создадим массив адресов из одного региона. Прогоним каждый адрес в запрос на DaData.ru.
        В результате получим массив в котором все значения должны быть одинаковы, иначе тест провален.
         */
        Dotenv dotenv = Dotenv.load();

        List<AddressStandartizationMinData> results = new ArrayList<>();
        List<String> addressInArray = Arrays.asList(address);
            results.add(given()
                    .spec(Specification.requestSpec(URL, dotenv.get("API_KEY")))
                    .header("X-Secret", dotenv.get("SECRET_KEY")) // Нативно добавил сюда секретку ибо запрос платный и требует секретного ключа
                    .body(addressInArray)
                    .when()
                    .post("/v1/clean/address")
                    .then()
                    .spec(Specification.responseSpecOK())
                    .log().status().and().log().body()
                    .extract().body()
                    .jsonPath().getObject("[0]", AddressStandartizationMinData.class));

            //Проверка достоверности региона внес ее сюда, чтобы как раз
            //использовать для этого pojo-класс, чтобы можно было отследить строку с результатом преобразования
            assertTrue(results.getLast().getRegion_with_type().equals(correctRegion),
                    "Регион определился неверно, вместо Кировская обл получено " +
                            results.getLast().getRegion_with_type() +
                            " |Резлуьтат преобразования: " + results.getLast().getResult());
        }

    }
