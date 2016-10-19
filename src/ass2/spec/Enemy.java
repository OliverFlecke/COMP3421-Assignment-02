package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * Represent an enemy in the game world
 * @author Oliver Fleckenstein
 *
 */
public class Enemy extends TerrainElement {

    private double[] position;
    
    public Enemy(double x, double y)
    {
        this.position = new double[3];
        this.position[0] = x;
        this.position[1] = y;
    }
    
    public void init(GLAutoDrawable drawable) 
    {
        
    }
    
    public void display(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        
        
    }
}
