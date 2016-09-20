package ass2.spec;

public class MathHelper {


    /**
     * Get the vector between the two points
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public static double[] getVector(double x1, double y1, double z1, double x2, double y2, double z2) 
    {
        return new double[] { x2 - x1, y2 - y1, z2 - z1 };
    }
    
    /**
     * Normalize the input vector
     * @param vector to be normalized
     */
    public static double[] normalizeVector(double[] vector) 
    {
        double lenght = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        double[] normalized = new double[3];
        normalized[0] = vector[0] / lenght;
        normalized[1] = vector[1] / lenght; 
        normalized[2] = vector[2] / lenght;
        return normalized;
    }
    
    /**
     * Find the normalized cross product between two vectors
     * @param v
     * @param u
     * @return The normalized cross product between v and u
     */
    public static double[] crossProduct(double[] v, double[] u) 
    {
        double[] normal = new double[3];
        normal[0] = v[1] * u[2] - v[2] * u[1];
        normal[1] = v[2] * u[0] - v[0] * u[2];
        normal[2] = v[0] * u[1] - v[1] * u[0];
        normalizeVector(normal);
        return normal;
    }
}
