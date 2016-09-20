package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension size;
    private double[][] altitude;
    private List<Tree> trees;
    private List<Road> roads;
    private float[] sunlight;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        this.size = new Dimension(width, depth);
        this.altitude = new double[width][depth];
        this.trees = new ArrayList<Tree>();
        this.roads = new ArrayList<Road>();
        this.sunlight = new float[3];
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return this.size;
    }

    public List<Tree> trees() {
        return this.trees;
    }

    public List<Road> roads() {
        return this.roads;
    }

    public float[] getSunlight() {
        return this.sunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        this.sunlight[0] = dx;
        this.sunlight[1] = dy;
        this.sunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        this.size = new Dimension(width, height);
        double[][] oldAlt = this.altitude;
        this.altitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                this.altitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return this.altitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        this.altitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * @param x
     * @param z
     * @return
     */
    public double getAltitude(double x, double z) {
        double altitude = 0;
        altitude = this.altitude[(int) x][(int) z];
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = getAltitude(x, z);
        Tree tree = new Tree(x, y, z);
        this.trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        this.roads.add(road);
    }

    /**
     * Draw the terrain in the world
     * @param gl
     */
    public void display(GL2 gl) {
        
//        float[] a = new float[4];
//        a[0] = a[1] = a[2] = 0.2f;
//        a[3] = 1.0f;
//        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, a, 0);
//
//        float[] d = new float[4];
//        d[0] = d[1] = d[2] = 0.5f;
//        d[3] = 1.0f;
//        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, d, 0);
//
//        float[] s = new float[4];
//        s[0] = s[1] = s[2] = 0.2f;
//        s[3] = 1.0f;
//        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, s, 0);

        gl.glBegin(GL.GL_TRIANGLES);
        {
            for (int x = 0; x < size.getHeight() - 1; x++) {
                for (int y = 0; y < size.getWidth() - 1; y++) {
                    double[] v1 = MathHelper.getVector(x, y, this.getAltitude(x, y), x + 1, y, this.getAltitude(x + 1, y));
                    double[] v2 = MathHelper.getVector(x, y, this.getAltitude(x, y), x, y + 1, this.getAltitude(x, y + 1));
                    double[] normal = MathHelper.crossProduct(v1, v2);
                    gl.glNormal3d(normal[0], normal[1], normal[2]);
                    gl.glVertex3d(x, y, this.getAltitude(x, y));
                    gl.glVertex3d(x + 1, y, this.getAltitude(x + 1, y));
                    gl.glVertex3d(x, y + 1, this.getAltitude(x, y + 1));
                    
                    v1 = MathHelper.getVector(x + 1, y + 1, this.getAltitude(x + 1, y + 1), x + 1, y, this.getAltitude(x + 1, y));
                    v2 = MathHelper.getVector(x + 1, y + 1, this.getAltitude(x + 1, y + 1), x, y + 1, this.getAltitude(x, y + 1));
                    normal = MathHelper.crossProduct(v1, v2);
                    gl.glNormal3d(normal[0], normal[1], normal[2]);
                    gl.glVertex3d(x + 1, y + 1, this.getAltitude(x + 1, y + 1));
                    gl.glVertex3d(x + 1, y, this.getAltitude(x + 1, y));
                    gl.glVertex3d(x, y + 1, this.getAltitude(x, y + 1));
                
                }
            }
        }
        gl.glEnd();
    }

    
}
