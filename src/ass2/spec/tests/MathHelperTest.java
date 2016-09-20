package ass2.spec.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ass2.spec.MathHelper;

public class MathHelperTest {
    private final double DELTA = 0.001;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getVector_test()
    {
        int x1 = 0, y1 = 1, z1 = 5;
        int x2 = 1, y2 = 3, z2 = 0;
        
        double[] vector = MathHelper.getVector(x1, y1, z1, x2, y2, z2);
        
        assertEquals(1, vector[0], DELTA);
        assertEquals(2, vector[1], DELTA);
        assertEquals(-5, vector[2], DELTA);
    }

    @Test 
    public void normilizeVector_test() 
    {
        double[] vector = new double[] { 1, 1, 1 };
        double[] normalized = MathHelper.normalizeVector(vector);
        
        assertEquals(1 / Math.sqrt(3), normalized[0], DELTA);
        assertEquals(1 / Math.sqrt(3), normalized[1], DELTA);
        assertEquals(1 / Math.sqrt(3), normalized[2], DELTA);
    }
    
    @Test 
    public void getCrossProduct_test()
    {
        double[] v = new double[] { 1, 0, 0 };
        double[] u = new double[] { 0, 1, 0 };
        
        double[] k = MathHelper.crossProduct(v, u);
        
        assertEquals(0, k[0], DELTA);
        assertEquals(0, k[1], DELTA);
        assertEquals(1, k[2], DELTA);
        
        v = new double[] { 1, 0, 0 };
        u = new double[] { 0, 0, 1 };
        
        k = MathHelper.crossProduct(v, u);
        
        assertEquals(0, k[0], DELTA);
        assertEquals(-1, k[1], DELTA);
        assertEquals(0, k[2], DELTA);
        
        
        v = new double[] { 0, 1, 0 };
        u = new double[] { 0, 0, 1 };
        
        k = MathHelper.crossProduct(v, u);
        
        assertEquals(1, k[0], DELTA);
        assertEquals(0, k[1], DELTA);
        assertEquals(0, k[2], DELTA);
        
        k = MathHelper.crossProduct(u, v);
        
        assertEquals(-1, k[0], DELTA);
        assertEquals(0, k[1], DELTA);
        assertEquals(0, k[2], DELTA);
        
    }
}
