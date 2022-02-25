/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bundle_unpack;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test public void testReadCfg() {
        CfgReader classUnderTest = new CfgReader();
        classUnderTest.readOrder();
        assertEquals(classUnderTest.readBundleFormat().size(), 3);
        assertEquals(classUnderTest.readOrder().getItemList().size(), 5);
    }


}
