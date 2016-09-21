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
        this.setSize(800, 600);
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

	@Override
	public void display(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
	    
	    // Makes sure that the objects are drawn in the right order
	    gl.glEnable(GL2.GL_DEPTH_TEST);
	    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

	    // Disabled by default
	    // To turn on culling:
	    gl.glEnable(GL2.GL_CULL_FACE);
	    gl.glCullFace(GL2.GL_BACK);
	    
        
        // enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);

        float[] pos = { 0, 0, 10, 1 };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
        
//        float[] pos1 = { 0, 0, 10, 1 };
//        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, pos1, 0);
        
        float[] amb = {0.1f, 0.2f, 0.3f, 1.0f};
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, amb, 0);
        gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
        
//      float[] a = new float[4];
//      a[0] = a[1] = a[2] = 0.2f;
//      a[3] = 1.0f;
//      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, a, 0);
//
//      float[] d = new float[4];
//      d[0] = d[1] = d[2] = 0.5f;
//      d[3] = 1.0f;
//      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, d, 0);
//
//      float[] s = new float[4];
//      s[0] = s[1] = s[2] = 0.2f;
//      s[3] = 1.0f;
//	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, s, 0);
	    
	    GLU glu = new GLU();
	    glu.gluPerspective(60, 1, 1, 100);
	    glu.gluLookAt(5 + cx, cy, 5 + cz, 5 + x, 5 + y, 0 + z, 0, 1, 0);
//	    gl.glRotated(90, 1, 0, 0);
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();

        this.terrain.display(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
	    gl.glEnable(GL2.GL_TEXTURE_2D);
	    this.terrain.init(drawable);
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
             case KeyEvent.VK_E:
                 cx += 1;
                 x += 1;
                 break;
             case KeyEvent.VK_A:
                 cx -= 1;
                 x -= 1;
                 break;
             case KeyEvent.VK_COMMA:
                 cy += 1;
                 y += 1;
                 break;
             case KeyEvent.VK_O:
                 cy -= 1;
                 y -= 1;
                 break;

             // Arrow keys to move camera around
             case KeyEvent.VK_LEFT:
                 cx -= 1;
                 break;
             case KeyEvent.VK_RIGHT:
                 cx += 1;
                 break;
             case KeyEvent.VK_UP:
                 cy += 1;
                 break;
             case KeyEvent.VK_DOWN:
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
