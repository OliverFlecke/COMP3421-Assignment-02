package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
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
    private List<Enemy> enemies;
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
        this.enemies = new ArrayList<Enemy>();
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
    
    public List<Enemy> getEnemies() {
        return this.enemies;
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
        Tree tree = new Tree(x, y, z, this);
        this.trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine, this);
        this.roads.add(road);
    }
    

    /**
     * Add an enemy 
     * @param x
     * @param z
     */
    public void addEnemy(double x, double z) {
        Enemy enemy = new Enemy(x, z, this);
        this.enemies.add(enemy);
       
    }


    private void drawTerrain(GL2 gl) {
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);

        // Material properties of the terrain
        float ambAndDif[] = {0.5f, 0.75f, 0.5f, 0.5f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, ambAndDif,0);
     
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
     * Setup the light properly.
     * This also calculates the moving/dynamic lightning if it is enabled
     * @param gl
     */
    public void setUpLighting(GL2 gl) 
    {
        // Set the sky color (clear color) based on time of day
        float[] clearColor = sun.getSkyColor();
        gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        
        // Light property vectors.
//        float lightAmb[] = { 0.5f, 0.5f, 0.5f, 1.0f };
//        float lightDifAndSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float lightAmb[] = sun.getAmbientLight();
        float lightDifAndSpec[] = sun.getLight();
        float globAmb[] = { 0.0f, 0.0f, 0.0f, 0.0f };
        
        gl.glEnable(GL2.GL_LIGHT0);
        // Set light properties.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmb,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, sun.getPosition(), 0);
        
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE); // Enable two-sided lighting.
        
        if (sun.isTimeDynamic())
            sun.tickTimeOfDay();
    }
    
    /**
     * Draw the terrain in the world
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        
        setUpLighting(gl);
        
        // Draw trees and roads
        for (Tree tree : this.getTrees()) 
            tree.display(drawable);
        
        for (Road road : this.getRoads())
            road.display(drawable);
        
        for (Enemy enemy : this.getEnemies())
            enemy.display(drawable);
        
        sun.drawSun(gl);
        
        if (rain.isRaining()) { rain.run(gl); }
        
        drawTerrain(gl);
    }
    
    /**
     * Setup all components and sub-component so they are ready to be drawn
     * @param drawable
     */
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        
        // Enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
//        gl.glEnable(GL2.GL_NORMALIZE);
        
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
        
        for (Enemy enemy : getEnemies())
        {
            enemy.setTerrain(this);
            enemy.init(drawable);
        }
        
        rain.init(drawable);
    }
}
