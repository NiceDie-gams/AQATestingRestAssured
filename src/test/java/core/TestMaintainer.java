package core;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import tests.AddressStrandartizationTest;
import tests.DeliveryCodeTest;
import tests.FioHintTest;

@Suite
@SelectClasses({
        AddressStrandartizationTest.class,
        DeliveryCodeTest.class,
        FioHintTest.class
})
public class TestMaintainer {

}
