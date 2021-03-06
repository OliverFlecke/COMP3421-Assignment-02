package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
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
public class Game extends JFrame implements GLEventListener, KeyListener, MouseMotionListener 
{
    private static final long serialVersionUID = -3503485707097285491L;
    
    private final int RIGHT_KEY = 0,
            LEFT_KEY    = 1,
            UP_KEY      = 2,
            DOWN_KEY    = 3,
            LOOK_UP     = 4,
            LOOK_DOWN   = 5,
            LOOK_LEFT   = 6,
            LOOK_RIGHT  = 7;
    private boolean[] keysBeingPressed = new boolean[8];
    
    boolean fog = false;
    // Mouse variables
    private double mouseRotationFactor = 25;
    private int mouseLastX = 0, mouseLastY = 0;
    private long mouseLastTime = 0;
    private Robot mouseMover; // Class used to move the mouse to the center of the screen
    private GLU glu;
    public static GLUT glut = new GLUT();
    
    private Terrain terrain;
    public static Avatar avatar;
    
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
        try {
            mouseMover = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        panel.addMouseMotionListener(this);
        // Add an animator to call 'display' at 60fps        
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel);
        animator.start();
        
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
        this.getContentPane().setCursor(blankCursor);
//        this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.getContentPane().add(panel);
        this.setSize(1400, 1200);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Load a level file and display it.
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }
    
    /**
     * Calculate the vector (x and y are each other inverse) for rotating up and down,
     * after having rotating the world around the z axis
     * @param angle which the rotation should consider
     * @return Vector value for rotating up and down after rotating around the z axis
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
	public void display(GLAutoDrawable drawable) 
	{
	    GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        
        setupCamera(gl);
        
//        drawCoordinateAxis(gl);
        
        avatar.display(drawable);
        this.terrain.display(drawable);
    }

	/**
	 * Setup the camera for the scene
	 * @param gl
	 */
    protected void setupCamera(GL2 gl) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    
	    glu.gluPerspective(80, 1, 0.1, 40);
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    if (avatar.getViewMode() == ViewMode.FIRST_PERSON)
	    {
	        glu.gluLookAt(avatar.getPosition()[0], avatar.getPosition()[1], avatar.getPosition()[2] + 1,
	                avatar.getPosition()[0] + avatar.getLook()[0],
	                avatar.getPosition()[1] + avatar.getLook()[1],
	                avatar.getPosition()[2] + avatar.getLook()[2],
	                0, 0, 1);
	    }
	    else if (avatar.getViewMode() == ViewMode.THRID_PERSON)
	    {
	        glu.gluLookAt(avatar.getPosition()[0], avatar.getPosition()[1] - 2.5, avatar.getPosition()[2] + 2, 
	                avatar.getPosition()[0] + avatar.getLook()[0], 
	                avatar.getPosition()[1] + avatar.getLook()[1], 
	                avatar.getPosition()[2] + avatar.getLook()[2], 
	                0, 0, 1);
	    }
	    else if (avatar.getViewMode() == ViewMode.SUN)
	    {
	        glu.gluLookAt(terrain.sun.getPosition()[0], terrain.sun.getPosition()[1], terrain.sun.getPosition()[2], 
	                terrain.getSize().getWidth() / 2, terrain.getSize().getHeight() / 2, 0, 
	                0, 0, 1);
	    }
	    
	    // Rotate camera 
	    if (avatar.getViewMode() != ViewMode.SUN)
	    {
	        // Rotate the world around the player
	        gl.glTranslated(avatar.getPosition()[0], avatar.getPosition()[1], avatar.getPosition()[2] + 1);
	        // Rotate around the z axis
	        gl.glRotated(avatar.getRotation()[0], 0, 0, 1);
	        // Rotate up and down - The x and y axis need to be calculated
	        double ratio = rotation_ration(avatar.getRotation()[0]);
	        double angle = avatar.getRotation()[1];
	        if (avatar.getRotation()[0] > 90 && avatar.getRotation()[0] <= 270)
	            angle = -angle;
	        
	        if (ratio > 0) gl.glRotated(angle, ratio, 1.0 - ratio, 0);
	        else gl.glRotated(-angle, ratio, 1.0 - Math.abs(ratio), 0);
	        
	        // Undo translation as: Can't push/pop as this will undo rotation!
	        gl.glTranslated(-avatar.getPosition()[0], -avatar.getPosition()[1], -(avatar.getPosition()[2] + 1));
	    }
    }

    protected void drawCoordinateAxis(GL2 gl) {
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
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void init(GLAutoDrawable drawable)
    {
        avatar = new Avatar(this.terrain);
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0f, 0f, 1f, 1f);

        // Fog
        if (fog)
        {
            gl.glEnable(GL2.GL_FOG);
            gl.glHint(GL2.GL_FOG_HINT, GL2.GL_NICEST);
            float[] fog_color = { 1f, 1f, 1f, 0.5f };
            gl.glFogfv(GL2.GL_FOG_COLOR, fog_color, 1);
            gl.glFogf(GL2.GL_FOG_START, 2f);
            gl.glFogf(GL2.GL_FOG_END, 4f);
            gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
        }
        
        // Makes sure that the objects are drawn in the right order
        gl.glEnable(GL2.GL_DEPTH_TEST);
        // To turn on culling:
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        gl.glEnable( GL2.GL_POLYGON_OFFSET_FILL);
        gl.glEnable(GL2.GL_BLEND); 
        
        gl.glEnable(GL2.GL_TEXTURE_2D);
        glu = new GLU();
        
        this.terrain.init(drawable);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    

    @Override
    public void keyPressed(KeyEvent e) 
    {
        int key = e.getKeyCode();
        
        switch (e.getKeyCode())
        {
            // Reset variables
            case KeyEvent.VK_R:
                avatar.reset();
                break;
            // Switch dynamic lightning on/off
            case KeyEvent.VK_L:
                terrain.sun.switchLightning();
                break;
            case KeyEvent.VK_D:
                terrain.sun.setDayMode();
                break;
            case KeyEvent.VK_N:
                terrain.sun.setNightMode();
                break;
            case KeyEvent.VK_S:
                terrain.sun.switchTimeDynamic();
                break;
            case KeyEvent.VK_C:
                terrain.rain.switchRain();
                break;
            case KeyEvent.VK_F:
                fog = !fog;
                break;
                
            case KeyEvent.VK_SHIFT:
                avatar.startSprinting();
                break;
            case KeyEvent.VK_V:
                avatar.switchViewMode();
                break;
            default:
                break;
        }
        
        // Move the camera around with the arrow keys
        if (key == KeyEvent.VK_RIGHT || keysBeingPressed[RIGHT_KEY])
        {
            keysBeingPressed[RIGHT_KEY] = true;
            avatar.moveRight();
        }
        if (key == KeyEvent.VK_LEFT || keysBeingPressed[LEFT_KEY])
        {
            keysBeingPressed[LEFT_KEY] = true;
            avatar.moveLeft();
        }
        if (key == KeyEvent.VK_UP || keysBeingPressed[UP_KEY])
        {
            keysBeingPressed[UP_KEY] = true;
            avatar.moveForward();
        }
        if (key == KeyEvent.VK_DOWN || keysBeingPressed[DOWN_KEY])
        {
            keysBeingPressed[DOWN_KEY] = true;
            avatar.moveBackward();
        }
        
        // Look around / Rotate the world
        if (key == KeyEvent.VK_COMMA || keysBeingPressed[LOOK_UP])
        {
            keysBeingPressed[LOOK_UP] = true;
            avatar.lookUp();
        }
        if (key == KeyEvent.VK_O || keysBeingPressed[LOOK_DOWN])
        {
            keysBeingPressed[LOOK_DOWN] = true;
            avatar.lookDown();
        }
        if (key == KeyEvent.VK_E || keysBeingPressed[LOOK_RIGHT])
        {
            keysBeingPressed[LOOK_RIGHT] = true;
            avatar.lookRight();
        }
        if (key == KeyEvent.VK_A || keysBeingPressed[LOOK_LEFT])
        {
            keysBeingPressed[LOOK_LEFT] = true;
            avatar.lookLeft();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) 
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_SHIFT:
                avatar.stopSprinting();
                break;
                
            case KeyEvent.VK_RIGHT:
                keysBeingPressed[RIGHT_KEY] = false;
                break;
            case KeyEvent.VK_LEFT:
                keysBeingPressed[LEFT_KEY] = false;
                break;
            case KeyEvent.VK_UP:
                keysBeingPressed[UP_KEY] = false;
                break;
            case KeyEvent.VK_DOWN:
                keysBeingPressed[DOWN_KEY] = false;
                break;
                
            case KeyEvent.VK_COMMA:
                keysBeingPressed[LOOK_UP] = false;
                break;
            case KeyEvent.VK_O:
                keysBeingPressed[LOOK_DOWN] = false;
                break;
            case KeyEvent.VK_E:
                keysBeingPressed[LOOK_RIGHT] = false;
                break;
            case KeyEvent.VK_A:
                keysBeingPressed[LOOK_LEFT] = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    
    private void lookAround(int x, int y)
    {
        int dx = mouseLastX - x;
        int dy = mouseLastY - y;
        avatar.addAngleToXY(dy / mouseRotationFactor);
        avatar.addAngleToZ(dx / mouseRotationFactor);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) 
    {
        int x = e.getX();
        int y = e.getY();
        
        lookAround(x, y);
        
        mouseLastX = x;
        mouseLastY = y;
    }
    
    @Override
    public void mouseMoved(MouseEvent e) 
    {
        long time = System.currentTimeMillis() - mouseLastTime;
        int x = e.getX();
        int y = e.getY();
        
        if (time > 50) // If the mouse hasn't been mode for x ms, center the mouse
        {
            mouseMover.mouseMove(this.getWidth() / 2, this.getHeight() / 2);
            x = this.getWidth() / 2;
            y = this.getHeight() / 2;
        }
        else
        {
            lookAround(x, y);
        }
        
        mouseLastX = x;
        mouseLastY = y;
        mouseLastTime = System.currentTimeMillis();
    }
}
