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
public class Game extends JFrame implements GLEventListener, KeyListener 
{
    // Variables for lightning
    private boolean dynamic_lightning = true;
    private float[] light_position = { 0, 0, 5, 1 };
    private int light_slices = 250;
    private int light_step = 0;
    private float light_radius = 10f;
    
    private Terrain terrain;
    
    // Angle to rotate the world
    private double[] rotate_angle;;
    
    // Camera variables
    private double[] camera_position;
    private double[] camera_look;
    
    /**
     * Create a game with a terrain
     * @param terrain
     */
    public Game(Terrain terrain) {
        super("Assignment 2");
        this.terrain = terrain;
    }
    
    /** 
     * Run the game.
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
	    glu.gluLookAt(camera_position[0] + camera_look[0], 
	            camera_position[1] + camera_look[1], 
	            camera_position[2] + camera_look[2], 
	            camera_position[0], camera_position[1], camera_position[2], 
	            0, 1, 0);
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    gl.glTranslated(camera_position[0] + camera_look[0], 
	            camera_position[1] + camera_look[1], 
	            camera_position[2] + camera_look[2]);
	    gl.glRotated(rotate_angle[0], 0, 0, 1);
	    gl.glRotated(-rotate_angle[1], 
	            Math.cos(rotate_angle[0] / 180 * Math.PI), 
	            Math.sin(rotate_angle[0] / 180 * Math.PI), 0);	    
	    gl.glTranslated(-(camera_position[0] + camera_look[0]), 
	            -(camera_position[1] + camera_look[1]), 
	            -(camera_position[2] + camera_look[2]));
        
	    // Center the world to 0,0
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
	public void dispose(GLAutoDrawable drawable) {}

	@Override
	public void init(GLAutoDrawable drawable)
	{
	    resetCamera();
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
        
        if (dynamic_lightning)
        {
            double light_angle = light_step * (2 * Math.PI / light_slices);
            
            light_position[0] = (float) ((this.terrain.size().getWidth() / 2) 
                    + light_radius * Math.cos(light_angle));
            light_position[1] = (float) ((this.terrain.size().getHeight() / 2) 
                    + light_radius * Math.sin(light_angle));
            light_step++;
        }
        
        gl.glEnable(GL2.GL_LIGHT0);
        // Set light properties.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmb,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position,0);
        
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE); // Enable two-sided lighting.
    }

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

	double step_size = 0.1;
	
    public void moveRight()
    {
        camera_position[0] += step_size * Math.cos(rotate_angle[0] / 180 * Math.PI);
        camera_position[1] += step_size * (-Math.sin(rotate_angle[0] / 180 * Math.PI));
        System.out.println("Move right");
    }
    
    public void moveLeft()
    {
        camera_position[0] -= step_size * Math.cos(rotate_angle[0] / 180 * Math.PI);
        camera_position[1] -= step_size * (-Math.sin(rotate_angle[0] / 180 * Math.PI));
        System.out.println("Move left");
    }
    
    public void moveForward()
    {
        camera_position[1] += step_size * Math.cos(rotate_angle[0] / 180 * Math.PI);
        camera_position[0] += step_size * Math.sin(rotate_angle[0] / 180 * Math.PI);
        System.out.println("Move up");
    }
    
    public void moveBackward()
    {
        
        camera_position[1] -= step_size * Math.cos(rotate_angle[0] / 180 * Math.PI);
        camera_position[0] -= step_size * Math.sin(rotate_angle[0] / 180 * Math.PI);
        System.out.println("Move down");
    }
    
    /**
     * Reset the camera to the original position
     */
    public void resetCamera()
    {
        camera_position = new double[3];
        camera_look = new double[] { 0, -10, 5 };
        rotate_angle = new double[3];
    }
    
    double angle_step = 1;
    @Override
    public void keyPressed(KeyEvent e) 
    {
        switch (e.getKeyCode())
        {
            // Reset variables
            case KeyEvent.VK_R:
                resetCamera();
                break;
            
            // Move the camera around with the arrow keys
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;
            case KeyEvent.VK_UP:
                moveForward();
                break;
            case KeyEvent.VK_DOWN:
                moveBackward();
                break;

            // Rotate the world
            case KeyEvent.VK_E:
                rotate_angle[0] += angle_step;
                System.out.println(rotate_angle[0]);
                break;
            case KeyEvent.VK_A:
                rotate_angle[0] -= angle_step;
                break;
            case KeyEvent.VK_COMMA:
                rotate_angle[1] += angle_step;
                break;
            case KeyEvent.VK_O:
                rotate_angle[1] -= angle_step;
                break;
                
                // Switch dynamic lightning on/off
            case KeyEvent.VK_L:
                dynamic_lightning = !dynamic_lightning;
                break;
        
//            // Use <AOE to move the point which the camera is looking at
//            case KeyEvent.VK_A:
//                camera_look[0] -= 1;
//                break;
//            case KeyEvent.VK_E:
//                camera_look[0] += 1;
//                break;
//            case KeyEvent.VK_COMMA:
//                camera_look[1] += 1;
//                break;
//            case KeyEvent.VK_O:
//                camera_look[1] -= 1;
//                break;
//            case KeyEvent.VK_0:
//                camera_look[2] += 1;
//                break;
//            case KeyEvent.VK_9:
//                camera_look[2] -= 1;
//                break;
             
            default:
                break;
        }
//        System.out.println("dx: " + camera_look[0] + " \tdy: " + camera_look[1] + " \tdz: " + camera_look[2]);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
