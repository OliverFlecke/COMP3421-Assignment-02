package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private String textureFileName = "src/textures/tree.jpg";
    private String textureExt = "jpg";
    private Texture texture;
    
    
    private double[] myPos;
    private double radius = 0.25;
    private Terrain terrain;
    
    private int slices = 50;
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
    /**
     * Function to display the tree
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glColor3f(1, 1, 1);
        double x = this.getPosition()[0];
        double y = this.getPosition()[2];
        double z = this.getPosition()[1];
        
        double z_bottom = this.getTerrain().getAltitude(x, y);
        double z_top = this.getTerrain().getAltitude(x, y) + z;
        
        // Draw a line to see were the center of the tree should be
        gl.glBegin(GL2.GL_LINES);
        {
            gl.glVertex3d(x, y, z_bottom);
            gl.glVertex3d(x, y, z_top);
        }
        gl.glEnd();
        
        
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
        //Front circle
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        {
             gl.glNormal3d(0, 0, -1);
             gl.glVertex3d(x, y, z_bottom);
             double angleStep = 2*Math.PI / slices;
             for (int i = 0; i <= slices ; i++) {
                 double angle = i * angleStep;
                 double x_current = Math.cos(angle);
                 double y_current = Math.sin(angle);

                gl.glVertex3d(x + radius * x_current, y + radius * y_current, z_bottom);
             }
        }
        gl.glEnd();
        
        // Back of the circle
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        {
            gl.glNormal3d(0, 0, -1);
            gl.glVertex3d(x, y, z_top);
            double angleStep = 2*Math.PI / slices;
            for (int i = slices; i >= 0; i--)
            {
                double angle = 2*Math.PI - i * angleStep;
                double x_current = Math.cos(angle);
                double y_current = Math.sin(angle);
                
                gl.glVertex3d(x + radius * x_current, y + radius * y_current, z_top);
            }
        }
        gl.glEnd();
        
        // Side of the cylinder
        // Load the texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
        gl.glBegin(GL2.GL_QUAD_STRIP);
        {
            double angle_step = 2 * Math.PI / slices;
            for (int i = 0; i <= slices; i++) 
            {
                double angle = i * angle_step;
                double x_current = Math.cos(angle);
                double y_current = Math.sin(angle);
                double tex_coord = 2.0 / slices * i;
                
                gl.glNormal3d(x_current, y_current, 0);
                gl.glTexCoord2d(tex_coord, 1);
                gl.glVertex3d(x + radius * x_current, y + radius * y_current, z_top);
                gl.glTexCoord2d(tex_coord, 0);
                gl.glVertex3d(x + radius * x_current, y + radius * y_current, z_bottom);
            }
        }
        gl.glEnd();
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }
    
    public void init(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        texture = new Texture(gl, textureFileName, textureExt, true);
    }

    /**
     * Set the terrain which this tree is located on 
     * @param terrain which the tree is locate on 
     */
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
    
    /**
     * @return The terrain which this tree is located on
     */
    public Terrain getTerrain() 
    {
        return this.terrain;
    }
}
