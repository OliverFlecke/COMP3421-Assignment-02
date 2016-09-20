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
	    
	    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        
        // enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);

        
        gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    
//	    setCamera(gl, new GLU(), 15);
//	    gl.glFrustum(-1, 11, -1, 11, 1, 5);
//	    gl.glRotated(30, 0, 1, 0);
//	    gl.glOrtho(0, this.terrain.size().getWidth(), 0, this.terrain.size().getHeight(), 1, 10);
	    gl.glOrtho(0, 12, 0, 12, 0, -2);
//	    GLU glu = new GLU();
//	    glu.gluLookAt(0, 0, 5, 5, 5, 0, 0, 1, 1);
	    gl.glRotated(90, 1, 0, 0);
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    gl.glTranslated(dx, dy, dz);

        this.terrain.display(gl);
	}

	private void setCamera(GL2 gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	int dx = 0;
	int dy = 0;
	int dz = 0;
    @Override
    public void keyPressed(KeyEvent e) {
         switch (e.getKeyCode())
         {
             case KeyEvent.VK_RIGHT:
                 dx += 1;
                 break;
             case KeyEvent.VK_LEFT:
                 dx -= 1;
                 break;
             case KeyEvent.VK_UP:
                 dy += 1;
                 break;
             case KeyEvent.VK_DOWN:
                 dy -= 1;
                 break;
             case KeyEvent.VK_0:
                 dz += 1;
                 break;
             case KeyEvent.VK_9:
                 dz -= 1;
                 break;
             default:
                 break;
         }
         System.out.println("dx: " + dx + " \tdy: " + dy + " \tdz: " + dz);
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
