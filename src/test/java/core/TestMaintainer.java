package core;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import tests.AddressStrandartizationTest;
import tests.DeliveryCodeTest;
import tests.FioHintApiTest;

@Suite
@SelectClasses({
        AddressStrandartizationTest.class,
        DeliveryCodeTest.class,
        FioHintApiTest.class
})
public class TestMaintainer {

}
