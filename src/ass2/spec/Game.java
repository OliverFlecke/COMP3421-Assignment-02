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
    private float[] light_position = new float[3];
    private int light_slices = 250;
    private int light_step = 0;
    private float light_radius = 10f;
    
    private Terrain terrain;
    private Avatar avatar;
    
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
        this.setSize(1400, 1200);
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
    
    /**
     * Needs better name and explenation for what it actually calculates
     * @param angle which the rotation should consider
     * @return 
     */
    private double rotation_ration(double angle)
    {
        double output = (90.0 - angle % 90.0) / 90.0;
        if ((angle >= 90 && angle < 180) || (angle >= 270 && angle < 360))
        {
            return (1 - output);
        }
        else 
        {
            return -output;
        }
    }
    
	@Override
	public void display(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
	    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

	    // Disabled by default
	    // To turn on culling:
	    gl.glEnable(GL2.GL_CULL_FACE);
	    gl.glCullFace(GL2.GL_BACK);
	    
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    GLU glu = new GLU();
	    glu.gluPerspective(60, 1, 1, 100);
	    glu.gluLookAt(avatar.getPosition()[0] + avatar.getLook()[0], 
	            avatar.getPosition()[1] + avatar.getLook()[1], 
	            avatar.getPosition()[2] + avatar.getLook()[2], 
	            avatar.getPosition()[0], avatar.getPosition()[1], avatar.getPosition()[2], 
	            0, 1, 0);
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    
	    // Rotate the world around the player
	    gl.glTranslated(avatar.getPosition()[0] + avatar.getLook()[0], 
	            avatar.getPosition()[1] + avatar.getLook()[1], 
	            avatar.getPosition()[2] + avatar.getLook()[2]);
	    
            // Rotate around the z axis
            gl.glRotated(avatar.getRotation()[0], 0, 0, 1);
            // Rotate up and down - The x and y axis need to be calculated
            double ratio = rotation_ration(avatar.getRotation()[0]);
            double angle = avatar.getRotation()[1];
            if (avatar.getRotation()[0] > 90 && avatar.getRotation()[0] <= 270)
            {
                angle = -angle;
            }
            if (ratio > 0)
            {
                gl.glRotated(angle, ratio, 1.0 - ratio, 0);
            }
            else 
            {
                gl.glRotated(-angle, ratio, 1.0 - Math.abs(ratio), 0);
            }
        gl.glTranslated(-(avatar.getPosition()[0] + avatar.getLook()[0]), 
                -(avatar.getPosition()[1] + avatar.getLook()[1]), 
                -(avatar.getPosition()[2] + avatar.getLook()[2]));
        
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

        setUpLighting(gl);
        this.terrain.display(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void init(GLAutoDrawable drawable)
    {
        avatar = new Avatar();
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
    
    /**
     * Setup the light properly.
     * This also calculates the moving/dynamic lightning if it is enabled
     * @param gl
     */
    public void setUpLighting(GL2 gl) 
    {
        // Light property vectors.
        float lightAmb[] = { 0.5f, 0.5f, 0.5f, 1.0f };
        float lightDifAndSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float globAmb[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        
        if (dynamic_lightning)
        {
            double light_angle = light_step * (2 * Math.PI / light_slices);
            light_position[0] = (float) (light_radius * Math.cos(light_angle));
            light_position[1] = (float) (light_radius * Math.sin(light_angle));
            light_position[2] = 5;
            light_step++;
        }
        else 
        {
            light_position = terrain.getSunlight();
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
    
    @Override
    public void keyPressed(KeyEvent e) 
    {
        switch (e.getKeyCode())
        {
            // Reset variables
            case KeyEvent.VK_R:
                avatar.reset();
                break;
            // Switch dynamic lightning on/off
            case KeyEvent.VK_L:
                dynamic_lightning = !dynamic_lightning;
                break;
            case KeyEvent.VK_SHIFT:
                avatar.startSprinting();
                break;
            
            // Move the camera around with the arrow keys
            case KeyEvent.VK_RIGHT:
                avatar.moveRight();
                break;
            case KeyEvent.VK_LEFT:
                avatar.moveLeft();
                break;
            case KeyEvent.VK_UP:
                avatar.moveForward();
                break;
            case KeyEvent.VK_DOWN:
                avatar.moveBackward();
                break;

            // Rotate the world
            case KeyEvent.VK_E:
                avatar.lookRight();
                break;
            case KeyEvent.VK_A:
                avatar.lookLeft();
                break;
            case KeyEvent.VK_COMMA:
                avatar.lookUp();
                break;
            case KeyEvent.VK_O:
                avatar.lookDown();
                break;
                
            default:
                break;
        }
//        System.out.println("dx: " + camera_look[0] + " \tdy: " + camera_look[1] + " \tdz: " + camera_look[2]);
    }
    
    @Override
    public void keyReleased(KeyEvent e) 
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_SHIFT:
                avatar.stopSprinting();
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
