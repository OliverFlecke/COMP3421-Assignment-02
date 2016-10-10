package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;



/**
 * COMMENT: Comment Game 
 *
 * @author Oliver Fleckenstein
 */
public class Game extends JFrame implements GLEventListener, KeyListener {

    boolean dynamicLightning = true;
    boolean direction = true;
    float[] lightPos = { 0, 0, 5, 1 };
    int light_slices = 250;
    int light_step = 0;
    float light_radius = 10f;
    
    private Terrain terrain;

    public Game(Terrain terrain) {
        super("Assignment 2");
        this.terrain = terrain;
    }
    
    /** 
     * Run the game.
     *
     */
    public void run() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        
        // Create panel to draw on 
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);

        // Add an animator to call 'display' at 60fps        
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel);
        animator.start();
        
        this.getContentPane().add(panel);
        this.setSize(1800, 1600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }

    private double rotate_angle = 0;
    
	@Override
	public void display(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
	    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

	    // Disabled by default
	    // To turn on culling:
	    gl.glEnable(GL2.GL_CULL_FACE);
	    gl.glCullFace(GL2.GL_BACK);
	    
        setUpLighting(gl);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    GLU glu = new GLU();
	    glu.gluPerspective(60, 1, 1, 100);
	    glu.gluLookAt(cx, -10 + cy, 5 + cz, x, y, 0 + z, 0, 1, 0);
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    gl.glRotated(rotate_angle, 0, 0, 1);
        gl.glTranslated(-this.terrain.size().getWidth() / 2, 
                -this.terrain.size().getHeight() / 2, 0);
	    
        // Draw axis'
        gl.glBegin(GL2.GL_LINES);
        {
            gl.glColor3d(1, 0, 0);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(1, 0, 0);
            
            gl.glColor3d(0, 1, 0);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(0, 1, 0);
            
            gl.glColor3d(0, 0, 1);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(0, 0, 1);
        }
        gl.glEnd();

        this.terrain.display(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
	    gl.glClearColor(0.2f, 0.2f, 1f, 1f);
	    // Makes sure that the objects are drawn in the right order
        gl.glEnable(GL2.GL_DEPTH_TEST);
        // enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);
	    
	    gl.glEnable(GL2.GL_TEXTURE_2D);
	    this.terrain.init(drawable);
	}
	
    public void setUpLighting(GL2 gl) 
    {
        // Light property vectors.
//        float lightPos[] = { -3f, -5f, 5f, 1f };
        float lightAmb[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float lightDifAndSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float globAmb[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        
        if (dynamicLightning)
        {
            double light_angle = light_step * (2 * Math.PI / light_slices);
            
            lightPos[0] = (float) ((this.terrain.size().getWidth() / 2) 
                    + light_radius * Math.cos(light_angle));
            lightPos[1] = (float) ((this.terrain.size().getHeight() / 2) 
                    + light_radius * Math.sin(light_angle));
            light_step++;
        }
        
        gl.glEnable(GL2.GL_LIGHT0);
        // Set light properties.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmb,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos,0);
        
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE); // Enable two-sided lighting.
    }

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	int x = 0, y = 0, z = 0;
	int cx = 0;
	int cy = 0;
	int cz = 0;
    @Override
    public void keyPressed(KeyEvent e) {
         switch (e.getKeyCode())
         {
             // Reset variables
             case KeyEvent.VK_R:
                 cx = cy = cz = 0;
                 x = y = z = 0;
                 break;
             case KeyEvent.VK_RIGHT:
                 cx += 1;
                 x += 1;
                 break;
             case KeyEvent.VK_LEFT:
                 cx -= 1;
                 x -= 1;
                 break;
             case KeyEvent.VK_UP:
                 cy += 1;
                 y += 1;
                 break;
             case KeyEvent.VK_DOWN:
                 cy -= 1;
                 y -= 1;
                 break;

             // Arrow keys to move camera around
             case KeyEvent.VK_A:
                 cx -= 1;
                 break;
             case KeyEvent.VK_E:
                 cx += 1;
                 break;
             case KeyEvent.VK_COMMA:
                 cy += 1;
                 break;
             case KeyEvent.VK_O:
                 cy -= 1;
                 break;
             case KeyEvent.VK_0:
                 cz += 1;
//                 z += 1;
                 break;
             case KeyEvent.VK_9:
                 cz -= 1;
//                 z -= 1;
                 break;
                 
             case KeyEvent.VK_1:
                 rotate_angle += 10;
                 break;
             case KeyEvent.VK_2:
                 rotate_angle -= 10;
                 break;
                 
             case KeyEvent.VK_L:
                 dynamicLightning = !dynamicLightning;
                 break;
             default:
                 break;
         }
         System.out.println("dx: " + cx + " \tdy: " + cy + " \tdz: " + cz);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}
