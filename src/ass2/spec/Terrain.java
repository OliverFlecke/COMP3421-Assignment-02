package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author Oliver Fleckenstein
 */
public class Terrain
{
    
    // Textures
    private String terrainTextureFileName = "src/textures/terrain/tileable_grass_00.png";
    private String terrainTextureExt = "png";
    
    private Dimension size;
    private double[][] altitude;
    private List<Tree> trees;
    private List<Road> roads;
    private Texture[] textures;
    public Sun sun;
    public Rain rain;
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
        sun = new Sun(this);
        rain = new Rain(this);
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
     * @param y
     * @return
     */
    public double getAltitude(double x, double y)
    {
        if (x < 0 || x >= this.getSize().getWidth() || y < 0 || y >= this.getSize().getHeight())
        {
            return 0;
        }
        double x_ratio = x % 1;
        double y_ratio = y % 1;
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(y);
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
        GL2 gl = drawable.getGL().getGL2();
        
        // Draw trees and roads
        for (Tree tree : this.getTrees()) 
        {
            tree.display(drawable);
        }
        
        for (Road road : this.getRoads())
        {
            road.display(drawable);
        }
        
        sun.drawSun(gl);
        
        if (rain.isRaining()) { rain.run(gl); }
        
        drawTerrain(gl);
    }

    private void drawTerrain(GL2 gl) {
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        float matAmbAndDif[] = {0.5f, 0.75f, 0.5f, 0.5f};
        
        // Material properties of teapot
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
     
        // Load the texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0].getTextureId());
        // Draw the terrain
        for (int x = 0; x < size.getHeight() - 1; x++) {
            for (int y = 0; y < size.getWidth() - 1; y++) {
                double[] v1 = MathHelper.getVector(x, y, this.getAltitude(x, y), x + 1, y, this.getAltitude(x + 1, y));
                double[] v2 = MathHelper.getVector(x, y, this.getAltitude(x, y), x, y + 1, this.getAltitude(x, y + 1));
                double[] normal = MathHelper.crossProduct(v1, v2);
                
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
        gl.glPopAttrib();
        gl.glPopMatrix();
    }

    /**
     * Setup all components and sub-component so they are ready to be drawn
     * @param drawable
     */
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        
        textures = new Texture[1];
        textures[0] = new Texture(gl, terrainTextureFileName, terrainTextureExt, true);

        sun.init(drawable);
        
        for (Tree tree : getTrees()) 
        {
            tree.setTerrain(this);
            tree.init(drawable);
        }
        
        for (Road road : getRoads())
        {
            road.setTerrain(this);
            road.init(drawable);
        }
        
        rain.init(drawable);
    }
}
