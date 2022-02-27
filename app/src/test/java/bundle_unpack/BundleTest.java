package bundle_unpack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BundleTest {
    String type = "IMG";
    NumPrice[] npArray;

    Bundle bundleTest;

    @Before
    public void setUp() throws Exception {
        npArray = new NumPrice[3];
        npArray[0] = new NumPrice(3, 570);
        npArray[1] = new NumPrice(5, 900);
        npArray[2] = new NumPrice(9, 1530);

        bundleTest  = new Bundle(type, npArray);
    }

    @After
    public void tearDown() throws Exception {
        for(int i=0; i<npArray.length; i++)
            npArray[i] = null;
        bundleTest = null;
    }

    @Test
    public void calBreakdown() {
        assertEquals(bundleTest.calBreakdown(2), new BundleBreakdown("IMG", 2, 3, 570, new int[]{1, 0, 0}));
        assertEquals(bundleTest.calBreakdown(3), new BundleBreakdown("IMG", 3, 3, 570, new int[]{1, 0, 0}));
        assertEquals(bundleTest.calBreakdown(5), new BundleBreakdown("IMG", 5, 5, 900, new int[]{0, 1, 0}));
        assertEquals(bundleTest.calBreakdown(7), new BundleBreakdown("IMG", 7, 8, 1470, new int[]{1, 1, 0}));
        assertEquals(bundleTest.calBreakdown(8), new BundleBreakdown("IMG", 8, 8, 1470, new int[]{1, 1, 0}));
        assertEquals(bundleTest.calBreakdown(9), new BundleBreakdown("IMG", 9, 9, 1530, new int[]{0, 0, 1}));
        assertEquals(bundleTest.calBreakdown(13), new BundleBreakdown("IMG", 13, 13, 2370, new int[]{1, 2, 0}));
        assertEquals(bundleTest.calBreakdown(20), new BundleBreakdown("IMG", 20, 20, 3570, new int[]{2, 1, 1}));
    }
}