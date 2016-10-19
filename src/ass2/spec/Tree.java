package ass2.spec;

import java.util.Random;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * Representing a tree. Can draw tree with texture at a given
 * position
 *
 * @author malcolmr
 */
public class Tree extends TerrainElement
{
    private static Random random = new Random();
    
    private String textureFileNameTree = "src/textures/tree/tree.jpg";
    private String textureExtTree = "jpg";
    private String textureFileNameLeaf = "src/textures/leaves/Copa.jpg";
    private String textureExtLeaf = "jpg";
    private Texture treeTexture;
    private Texture leafTexture;
    
    private double[] position;
    private double radius;
    private double sphereRadius;
    
    private int slices = 50;
    
    public Tree(double x, double y, double z) {
        this.position = new double[3];
        this.position[0] = x;
        this.position[1] = z;
        this.position[2] = 1 + random.nextDouble() * 2;
        this.radius = 0.1 + random.nextDouble() * 0.15;
        this.sphereRadius = 0.5 + random.nextDouble() * 0.5;
    }
    
    /**
     * @return The position of the tree
     */
    public double[] getPosition() {
        return position;
    }
    
    /**
     * Function to display the tree
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        
        double x = this.getPosition()[0];
        double y = this.getPosition()[1];
        double z = this.getPosition()[2];
        double z_bottom = this.getTerrain().getAltitude(x, y);
        double z_top = z_bottom + z;

        // Draw a line to see were the center of the tree should be
//        gl.glBegin(GL2.GL_LINES);
//        {
//            gl.glColor3f(1, 1, 1);
//            gl.glVertex3d(x, y, z_bottom);
//            gl.glVertex3d(x, y, z_top);
//        }
//        gl.glEnd();
        
        // Push a matrix so you don't have to calculate the position
        gl.glPushMatrix();
        gl.glTranslated(x, y, 0);
        
        float emmL[] = {0.0f, 0.0f, 0.0f, 1.0f};
        float matAmbAndDifL[] = {0.25f, 0.5f, 0.25f, 0.5f};
        float matSpecL[] = { 0.2f, 0.5f, 0.2f, 1.0f };
        float matShineL[] = { 50.0f };

        // Material properties of sphere.
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifL,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecL,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineL,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmL,0);
        
        
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
        // Side of the cylinder
        // Load the texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D, treeTexture.getTextureId());
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
                gl.glVertex3d(radius * x_current, radius * y_current, z_top);
                gl.glTexCoord2d(tex_coord, 0);
                gl.glVertex3d(radius * x_current, radius * y_current, this.getTerrain().getAltitude(x + radius * x_current, y + radius * y_current));
            }
        }
        gl.glEnd();
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        
        gl.glPushMatrix();
            gl.glTranslated(0, 0, z_top + 0.9 * sphereRadius);
            
            gl.glEnable(GL2.GL_TEXTURE_GEN_S); //enable texture coordinate generation
            gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, leafTexture.getTextureId());
            Game.glut.glutSolidSphere(sphereRadius, 40, 40);
            gl.glDisable(GL2.GL_TEXTURE_GEN_S); //enable texture coordinate generation
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);
        gl.glPopMatrix();
        
        gl.glPopMatrix();
    }
    
    public void init(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        treeTexture = new Texture(gl, textureFileNameTree, textureExtTree, true);
        leafTexture = new Texture(gl, textureFileNameLeaf, textureExtLeaf, true);
    }
}
