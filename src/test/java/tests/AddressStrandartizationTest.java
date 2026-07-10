package tests;

import dto.AddressStandartizationData;
import dto.AddressStandartizationMinData;
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
                .header("X-Secret", dotenv.get("SECRET_KEY")) // Нативно добавил сюда секретку ибо запрос платный и требует секретного ключа
                .body(addresses)
                .when()
                .post("/v1/clean/address")
                .then().log().status().and().log().body()
                .extract().body()
                .jsonPath().getObject("[0]", AddressStandartizationData.class);

        assertTrue(element.getResult().contains(element.getRegion_with_type()), "Ошибка. Назавние региона не совпадает в result");
        assertTrue(element.getResult().contains(element.getStreet_with_type()), "Ошибка. Название улицы не совпадает в result");
    }

    @Test
    public void correctRegionRecognizeTest(){
        /*
        Тест
        Проверка на то верно ли программа опредлеяет регион для разных адресов из одного региона.
        Создадим массив адресов из одного региона. Прогоним каждый адрес в запрос на DaData.ru.
        В результате получим массив в котором все значения должны быть одинаковы, иначе тест провален.
         */
        Dotenv dotenv = Dotenv.load();

        Specification.installSpecification(Specification.responseSpecOK(),
                Specification.requestSpec(URL, dotenv.get("API_KEY")));

        String[] queries = {
                "киров ленина 15/2",
                "киров октябрьский 118",
                "киров карла либкнехта 182",
                "кирово-чепецк первомайская 6",
                "слободской советская 10",
                "вятские поляны ленина 25",
                "котельнич карла маркса 7",
                "омутинск кирова 12",
                "яранск свободы 30", //Если в Яранкс добавить еще одну р то город будет определяться как Москва, возможно можно сказать что тест нашел какую-то ошибку ведь по сути при каверкании названия ответ приходит просто null
                "суна большевиков 5а"
        };;

        List<AddressStandartizationMinData> results = new ArrayList<>();
        for (String query : queries) {
            List<String> address = Arrays.asList(query);
            results.add(given()
                    .header("X-Secret", dotenv.get("SECRET_KEY")) // Нативно добавил сюда секретку ибо запрос платный и требует секретного ключа
                    .body(address)
                    .when()
                    .post("/v1/clean/address")
                    .then().log().status().and().log().body()
                    .extract().body()
                    .jsonPath().getObject("[0]", AddressStandartizationMinData.class));
            //Проверка достоверности региона внес ее сюда, чтобы как раз
            //использовать для этого pojo-класс, чтобы можно было отследить строку с результатом преобразования
            assertTrue(results.getLast().getRegion_with_type().equals("Кировская обл"),
                    "Регион определился неверно, вместо [Кировская обл] получено " +
                            results.getLast().getRegion_with_type() +
                            " |Резлуьтат преобразования: " + results.getLast().getResult());
        }

    }
}
