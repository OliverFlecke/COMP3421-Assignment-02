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

    private String textureFileName1 = "src/textures/tree.jpg";
    private String textureExt1 = "jpg";
    
    private Texture texture;
    
    private double[] myPos;
    private double radius = 0.25;
    private Terrain terrain;
    
    private int slices = 10;
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
//   {
//    public void display(GL2 gl) 
//    {
//        texture = new Texture(gl, textureFileName1, textureExt1, true);
//        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
//        gl.glLoadIdentity();
//
////        GLU glu = new GLU();
////       
////        glu.gluLookAt(0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
////       
////        gl.glTranslated(0, 0, -3);
//        // Commands to turn the cylinder.
////        gl.glRotated(Zangle, 0.0, 0.0, 1.0);
////        gl.glRotated(Yangle, 0.0, 1.0, 0.0);
////        gl.glRotated(Xangle, 1.0, 0.0, 0.0);
//        gl.glTranslated(0, 0, 3);
//        
//        double angleIncrement = (Math.PI * 2.0) / slices;
//        double zFront = -1;
//        double zBack = -3;
//        
//       //Draw the top of the cylinder with the canTop.bmp texture
//        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
//        gl.glBegin(GL2.GL_POLYGON);
//        {
//            for(int i = 0; i < slices; i++)
//            {
//                double angle0 = i*angleIncrement;
//            
//                gl.glNormal3d(0.0, 0.0, 1);
//                gl.glTexCoord2d(0.5+0.5*Math.cos(angle0),0.5+0.5*Math.sin(angle0));
//                gl.glVertex3d(Math.cos(angle0), Math.sin(angle0),zFront);
//            }
//        }
//        gl.glEnd();
//      
//        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
//        gl.glBegin(GL2.GL_QUAD_STRIP);{      
//            for(int i=0; i<= slices; i++){
//                double angle0 = i*angleIncrement;
//                double angle1 = (i+1)*angleIncrement;
//                double xPos0 = Math.cos(angle0);
//                double yPos0 = Math.sin(angle0);
//                double sCoord = 2.0/slices * i; //Or * 2 to repeat label
//                
//                gl.glNormal3d(xPos0, yPos0, 0);
//                gl.glTexCoord2d(sCoord,1);
//                gl.glVertex3d(xPos0,yPos0,zFront);
//                gl.glTexCoord2d(sCoord,0);
//                gl.glVertex3d(xPos0,yPos0,zBack);
//            }
//        }
//        gl.glEnd();
//        
//        //Draw the bottom of the cylinder also with the canTop.bmp texture :)
//        //just for demonstration.
//        gl.glBegin(GL2.GL_POLYGON);
//        {
//            for(int i = 0; i < slices; i++)
//            {
//                double angle0 = -i*angleIncrement;
//                
//                gl.glNormal3d(0.0, 0.0, -1);
//             
//                gl.glTexCoord2d(0.5+0.5*Math.cos(angle0),0.5+0.5*Math.sin(angle0));
//                gl.glVertex3d(Math.cos(angle0), Math.sin(angle0),zBack);
//            }
//        }
//        gl.glEnd();
//    }
//         texture = new Texture(gl, textureFileName1, textureExt1, true);
//        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
//        gl.glLoadIdentity();
//
////        GLU glu = new GLU();
////       
////        glu.gluLookAt(0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
////       
////        gl.glTranslated(0, 0, -3);
//        // Commands to turn the cylinder.
////        gl.glRotated(Zangle, 0.0, 0.0, 1.0);
////        gl.glRotated(Yangle, 0.0, 1.0, 0.0);
////        gl.glRotated(Xangle, 1.0, 0.0, 0.0);
//        gl.glTranslated(0, 0, 3);
//        
//        double angleIncrement = (Math.PI * 2.0) / slices;
//        double zFront = -1;
//        double zBack = -3;
//        
//       //Draw the top of the cylinder with the canTop.bmp texture
//        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
//        gl.glBegin(GL2.GL_POLYGON);
//        {
//            for(int i = 0; i < slices; i++)
//            {
//                double angle0 = i*angleIncrement;
//            
//                gl.glNormal3d(0.0, 0.0, 1);
//                gl.glTexCoord2d(0.5+0.5*Math.cos(angle0),0.5+0.5*Math.sin(angle0));
//                gl.glVertex3d(Math.cos(angle0), Math.sin(angle0),zFront);
//            }
//        }
//        gl.glEnd();
//      
//        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
//        gl.glBegin(GL2.GL_QUAD_STRIP);{      
//            for(int i=0; i<= slices; i++){
//                double angle0 = i*angleIncrement;
//                double angle1 = (i+1)*angleIncrement;
//                double xPos0 = Math.cos(angle0);
//                double yPos0 = Math.sin(angle0);
//                double sCoord = 2.0/slices * i; //Or * 2 to repeat label
//                
//                gl.glNormal3d(xPos0, yPos0, 0);
//                gl.glTexCoord2d(sCoord,1);
//                gl.glVertex3d(xPos0,yPos0,zFront);
//                gl.glTexCoord2d(sCoord,0);
//                gl.glVertex3d(xPos0,yPos0,zBack);
//            }
//        }
//        gl.glEnd();
//        
//        //Draw the bottom of the cylinder also with the canTop.bmp texture :)
//        //just for demonstration.
//        gl.glBegin(GL2.GL_POLYGON);
//        {
//            for(int i = 0; i < slices; i++)
//            {
//                double angle0 = -i*angleIncrement;
//                
//                gl.glNormal3d(0.0, 0.0, -1);
//             
//                gl.glTexCoord2d(0.5+0.5*Math.cos(angle0),0.5+0.5*Math.sin(angle0));
//                gl.glVertex3d(Math.cos(angle0), Math.sin(angle0),zBack);
//            }
//        }
//        gl.glEnd();
//    }
// 
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
        
        double z_bottom = 1 + this.getTerrain().getAltitude(x, y);
        double z_top = 1 + this.getTerrain().getAltitude(x, y) + z;
        
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
        
        // Sides of the cylinder
        gl.glBegin(GL2.GL_QUAD_STRIP);
        {
            double angleStep = 2*Math.PI / slices;
            for (int i = 0; i <= slices; i++)
            {
                double current_angle = i * angleStep;
                double next_angle = ((i+1) % slices) * angleStep;
                
                //Calculate vertices for the quad
                double x_current = Math.cos(current_angle);
                double y_current = Math.sin(current_angle);
                double x_next = Math.cos(next_angle);
                double y_next = Math.sin(next_angle);

//                System.out.println("x0: " + x0 + " y0: " + y0);
                gl.glNormal3d(x_current, y_current, 0);
                x_current = x_current * radius + x;
                y_current = y_current * radius + y;
                gl.glVertex3d(x_current, y_current, z_top);  
                gl.glVertex3d(x_current, y_current, z_bottom);
                
                gl.glNormal3d(x_current, y_current, 0);
                x_next = x_next * radius + x;
                y_next = y_next * radius + y;
                gl.glVertex3d(x_next, y_next, z_top);
                gl.glVertex3d(x_next, y_next, z_bottom);
            }
        }
        gl.glEnd();
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }
    
    public void init(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        texture = new Texture(gl, textureFileName1, textureExt1, true);
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
