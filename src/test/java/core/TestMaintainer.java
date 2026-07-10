package core;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import tests.AddressStrandartizationTest;
import tests.DeliveryCodeTest;

@Suite
@SelectClasses({
        AddressStrandartizationTest.class,
        DeliveryCodeTest.class
})
public class TestMaintainer {

}
