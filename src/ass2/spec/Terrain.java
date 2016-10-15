package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author Oliver Fleckenstein
 */
public class Terrain {
    
    // Textures
    private String textureFileName1 = "src/textures/tileable_grass_00.png";
    private String textureExt1 = "png";
    
    private Dimension size;
    private double[][] altitude;
    private List<Tree> trees;
    private List<Road> roads;
    private Texture[] textures;
    
    // Variables for lightning
    private float[] dynamic_sunlight;
    private int light_slices = 250;
    private int light_step = 0;
    private float light_radius = 10f;
    private float[] static_sunlight;
    private boolean dynamic_lightning = true;

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
        this.static_sunlight = new float[3];
        this.dynamic_sunlight = new float[3];
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension getSize() {
        return this.size;
    }

    public List<Tree> getTrees() {
        return this.trees;
    }

    public List<Road> getRoads() {
        return this.roads;
    }

    /**
     * @return The current position of the sun
     */
    public float[] getSunlight() {
        if (dynamic_lightning)
        {
            return this.dynamic_sunlight;
        }
        else 
        {
            return this.static_sunlight;
        }
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
        this.static_sunlight[0] = dx;
        this.static_sunlight[1] = dy;
        this.static_sunlight[2] = dz;        
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
     * TO BE COMPLETED - Should be completed
     * 
     * @param x
     * @param z
     * @return
     */
    public double getAltitude(double x, double z)
    {
        double x_ratio = x % 1;
        double y_ratio = z % 1;
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(z);
        int x1 = x0;
        int y1 = y0;
        if (x0 < this.getSize().getWidth() - 1) x1 = x0 + 1;
        if (y0 < this.getSize().getHeight() - 1) y1 = y0 + 1;
        
        // Interpolation along the x-axis
        double alt0 = ((1 - x_ratio) * this.altitude[x0][y0]) + (x_ratio * this.altitude[x1][y0]);
        double alt1 = ((1 - x_ratio) * this.altitude[x0][y1]) + (x_ratio * this.altitude[x1][y1]);
        return ((1 - y_ratio) * alt0) + (y_ratio * alt1);
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
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) 
    {
        calculateDynamicSunlightPosition();
        GL2 gl = drawable.getGL().getGL2();
        
        // Load the texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0].getTextureId());
        
        // Draw the terrain
        for (int x = 0; x < size.getHeight() - 1; x++) {
            for (int y = 0; y < size.getWidth() - 1; y++) {
                double[] v1 = MathHelper.getVector(x, y, this.getAltitude(x, y), x + 1, y, this.getAltitude(x + 1, y));
                double[] v2 = MathHelper.getVector(x, y, this.getAltitude(x, y), x, y + 1, this.getAltitude(x, y + 1));
                double[] normal = MathHelper.crossProduct(v1, v2);
//                System.out.println("First normal: (x,y,z): (" + normal[0] + "," + normal[1] + "," + normal[2] + ")");
//                gl.glBegin(GL2.GL_LINES);
//                {
//                    gl.glVertex3d(x, y, this.getAltitude(x, y));
//                    gl.glVertex3d(x + normal[0], y + normal[1], this.getAltitude(x, y) + normal[2]);
//                }
//                gl.glEnd();
                
                
                gl.glBegin(GL2.GL_TRIANGLES);
                {
                    gl.glNormal3d(normal[0], normal[1], normal[2]);
                    gl.glTexCoord2d(0, 0);
                    gl.glVertex3d(x, y, this.getAltitude(x, y));
                    gl.glTexCoord2d(1, 0);
                    gl.glVertex3d(x + 1, y, this.getAltitude(x + 1, y));
                    gl.glTexCoord2d(0.5, 1);
                    gl.glVertex3d(x, y + 1, this.getAltitude(x, y + 1));
                }
                gl.glEnd();
                
                v1 = MathHelper.getVector(x + 1, y + 1, this.getAltitude(x + 1, y + 1), x + 1, y, this.getAltitude(x + 1, y));
                v2 = MathHelper.getVector(x + 1, y + 1, this.getAltitude(x + 1, y + 1), x, y + 1, this.getAltitude(x, y + 1));
                normal = MathHelper.crossProduct(v2, v1);
//                gl.glBegin(GL2.GL_LINES);
//                {
//                    gl.glVertex3d(x + 1, y + 1, this.getAltitude(x, y));
//                    gl.glVertex3d(x + 1 + normal[0], y + 1 + normal[1], this.getAltitude(x, y + 1) + normal[2]);
//                }
//                gl.glEnd();
                
//                System.out.println("second normal: (x,y,z): (" + normal[0] + "," + normal[1] + "," + normal[2] + ")");
                gl.glBegin(GL2.GL_TRIANGLES);
                {
                    gl.glNormal3d(normal[0], normal[1], normal[2]);
                    gl.glTexCoord2d(0, 0);
                    gl.glVertex3d(x + 1, y + 1, this.getAltitude(x + 1, y + 1));
                    gl.glTexCoord2d(1, 0);
                    gl.glVertex3d(x, y + 1, this.getAltitude(x, y + 1));
                    gl.glTexCoord2d(0.5, 1);
                    gl.glVertex3d(x + 1, y, this.getAltitude(x + 1, y));
                }
                gl.glEnd();
            }
        }
        
        gl.glTranslatef(getSunlight()[0], getSunlight()[1], getSunlight()[2]);
        gl.glColor3d(1, 1, 0);
        Game.glut.glutSolidSphere(1, 100, 100);
        gl.glTranslatef(-getSunlight()[0], -getSunlight()[1], -getSunlight()[2]);
        
        for (Tree tree : this.trees) 
        {
            tree.display(drawable);
        }
        
        for (Road road : this.getRoads())
        {
            road.display(drawable);
        }
    }
    
    /**
     * Calculates the dynamic position of the sun 
     */
    private void calculateDynamicSunlightPosition()
    {
        double light_angle = light_step * (2 * Math.PI / light_slices);
        dynamic_sunlight[0] = (float) (getSize().getWidth() / 2 + light_radius * Math.cos(light_angle));
        dynamic_sunlight[1] = (float) (getSize().getHeight() / 2 + light_radius * Math.sin(light_angle));
        dynamic_sunlight[2] = 5;
        light_step++;
    }

    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        
        textures = new Texture[1];
        textures[0] = new Texture(gl, textureFileName1, textureExt1, true);
        
        for (Tree tree : getTrees()) 
        {
            tree.setTerrain(this);
            tree.init(drawable);
        }
        
        for (Road road : getRoads())
        {
            road.init(drawable);
        }
    }

    /**
     * Switch the lightning between dynamic and static
     */
    public void switchLightning() 
    {
        this.dynamic_lightning = !this.dynamic_lightning;
    }

    
}
