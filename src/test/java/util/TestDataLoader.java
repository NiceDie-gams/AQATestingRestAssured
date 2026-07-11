package util;

import org.junit.jupiter.params.provider.Arguments;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.stream.Stream;

public class TestDataLoader {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = TestDataLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл config.properties не найден в classpath");
            }

            try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки config.properties", e);
        }
    }

    public static String getCorrectKladr(int index) {
        return properties.getProperty("correct.kladr." + index);
    }

    public static Stream<Arguments> getCorrectKladrArg() {
        return Stream.of(Arguments.of(properties.getProperty("correct.kladr.1")));
    }

    public static String getCorrectCdek(int index) {
        return properties.getProperty("correct.cdek." + index);
    }

    public static String getIncorrectKladr(int index){
        return properties.getProperty("incorrect.kladr." + index);
    }

    public static String getIncorrectCdek(int index){
        return properties.getProperty("incorrect.cdek." + index);
    }

    public static String getAddress(int index){
        return properties.getProperty("test.address."+index);
    }

    public static String getCorrectRegion(){
        return properties.getProperty("correct.region");
    }

    public static Stream<Arguments> getAddressForTest(){
        return Stream.of(Arguments.of(properties.getProperty("test.region.address")));
    }

    public static Stream<Arguments> getAddressAndRegion(){
        return Stream.of(
                Arguments.of(getAddress(1), getCorrectRegion()),
                Arguments.of(getAddress(2), getCorrectRegion()),
                Arguments.of(getAddress(3), getCorrectRegion()),
                Arguments.of(getAddress(4), getCorrectRegion()),
                Arguments.of(getAddress(5), getCorrectRegion()),
                Arguments.of(getAddress(6), getCorrectRegion()),
                Arguments.of(getAddress(7), getCorrectRegion()),
                Arguments.of(getAddress(8), getCorrectRegion()),
                Arguments.of(getAddress(9), getCorrectRegion()),
                Arguments.of(getAddress(10), getCorrectRegion())
                );
    }

    public static Stream<Arguments> getAllCorrectKladrAndCdek(){
        return Stream.of(
                Arguments.of (getCorrectKladr(1), getCorrectCdek(1)),
                Arguments.of (getCorrectKladr(2), getCorrectCdek(2)),
                Arguments.of (getCorrectKladr(3), getCorrectCdek(3)),
                Arguments.of (getCorrectKladr(4), getCorrectCdek(4)),
                Arguments.of (getCorrectKladr(5), getCorrectCdek(5))
        );
    }

    public static Stream<Arguments> getAllIncorrectKladrAndCdek(){
        return Stream.of(
                Arguments.of (getIncorrectKladr(1), getIncorrectCdek(1)),
                Arguments.of (getIncorrectKladr(2), getIncorrectCdek(2))
        );
    }
}
