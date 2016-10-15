package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * An avatar, representing the player in the game. 
 * Is able to move and look around in the game world.
 * 
 * @author Oliver Fleckenstein
 *
 */
public class Avatar 
{
    // Movement constants
    private final double SPRINT_MOVEMENT_SPEED = 0.5;
    private final double BASE_MOVEMENT_SPEED = 0.1;
    
    private final double SPRINT_ROTATION_SPEED = 5;
    private final double BASE_ROTATION_SPEED = 1;
    
    private double[] position;
    private double[] rotation;
    private double[] look;
    
    private double movementSpeed = BASE_MOVEMENT_SPEED;
    private double rotationSpeed = BASE_ROTATION_SPEED;
    
    private ViewMode viewMode = ViewMode.FIRST_PERSON;
    
    private Terrain terrain;
    
    /**
     * Create an avatar 
     */
    public Avatar(Terrain terrain)
    {
        this.terrain = terrain;
        reset();
    }
    
    /**
     * Reset the avatar to the original position, and looking in the original direction
     */
    public void reset()
    {
        rotation = new double[] { 0, 0 };
        position = new double[] { 0, 0, 0 };
        look = new double[] { 0, 5, 0 };
        setZCoordinate();
        movementSpeed = BASE_MOVEMENT_SPEED;
        rotationSpeed = BASE_ROTATION_SPEED;
    }
    
    /**
     * Display the avatar
     * @param drawable
     */
    public void display(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        
        if (getViewMode() != ViewMode.FIRST_PERSON)
        {
            gl.glPushMatrix();
            {
                gl.glColor3d(0.7, 0, 0.7);
                gl.glTranslated(position[0], position[1], position[2]);
                Game.glut.glutSolidCylinder(0.1, 1, 100, 10);
            }
            gl.glPopMatrix();
        }
    }
    
    public void addAngleToZ(double angle) 
    {
        this.rotation[0] += angle;
    }
    
    public void addAngleToXY(double angle)
    {
        this.rotation[1] += angle;
    }
    
    /**
     * Start sprinting - Increases movement and rotation speed
     */
    public void startSprinting()
    {
        movementSpeed = SPRINT_MOVEMENT_SPEED;
        rotationSpeed = SPRINT_ROTATION_SPEED;
    }
    
    /**
     * Stop the avatar from sprinting
     * Resets movement and rotation speed back to the base speed
     */
    public void stopSprinting()
    {
        movementSpeed = BASE_MOVEMENT_SPEED;
        rotationSpeed = BASE_ROTATION_SPEED;
    }
    
    public void setZCoordinate()
    {
        position[2] = this.terrain.getAltitude(position[0], position[1]);
    }
    
    /**
     * Move the avatar to the right
     */
    public void moveRight()
    {
        position[0] += movementSpeed * Math.cos(rotation[0] / 180 * Math.PI);
        position[1] += movementSpeed * (-Math.sin(rotation[0] / 180 * Math.PI));
        setZCoordinate();
    }
    
    /**
     * Move the avatar to the left
     */
    public void moveLeft()
    {
        position[0] -= movementSpeed * Math.cos(rotation[0] / 180 * Math.PI);
        position[1] -= movementSpeed * (-Math.sin(rotation[0] / 180 * Math.PI));
        setZCoordinate();
    }
    
    /**
     * Move the avatar forward
     */
    public void moveForward()
    {
        position[1] += movementSpeed * Math.cos(rotation[0] / 180 * Math.PI);
        position[0] += movementSpeed * Math.sin(rotation[0] / 180 * Math.PI);
        setZCoordinate();
    }
    
    /**
     * Move the avatar backwards
     */
    public void moveBackward()
    {
        position[1] -= movementSpeed * Math.cos(rotation[0] / 180 * Math.PI);
        position[0] -= movementSpeed * Math.sin(rotation[0] / 180 * Math.PI);
        setZCoordinate();
    }
    
    /**
     * Move the focus point of the camera up
     */
    public void lookUp()
    {
        if (rotation[1] > -80)
        {
            rotation[1] -= rotationSpeed;
        }
    }
    
    /**
     * Move the focus point of the camera down
     */
    public void lookDown()
    {
        if (rotation[1] < 80)
        {
            rotation[1] += rotationSpeed;
        }
    }
    
    /**
     * Make the avatar look to the right
     */
    public void lookRight()
    {
        rotation[0] += rotationSpeed;
        if (rotation[0] >= 360)
        {
            rotation[0] = 0;
        }
    }
    
    /**
     * Make the avatar look to the left
     */
    public void lookLeft()
    {
        rotation[0] -= rotationSpeed;
        if (rotation[0] <= 0)
        {
            rotation[0] = 359;
        }
    }
    
    /**
     * @return The position of the avatar
     */
    public double[] getPosition()
    {
        return this.position;
    }
    
    /**
     * @return The point which the avatar is looking at
     */
    public double[] getLook() 
    {
        return this.look;
    }
    
    /**
     * @return The rotation of the avatar
     */
    public double[] getRotation()
    {
        return this.rotation;
    }
    
    /**
     * Switch the view mode between thrid person and first person mode
     */
    public void switchViewMode()
    {
        switch (viewMode)
        {
            case FIRST_PERSON:
                viewMode = ViewMode.THRID_PERSON;
                break;
            case THRID_PERSON:
                viewMode = ViewMode.SUN;
                break;
            case SUN:
                viewMode = ViewMode.FIRST_PERSON;
                break;
            default:
                viewMode = ViewMode.FIRST_PERSON;
                break;
        }
    }
    
    /**
     * @return The current view mode
     */
    public ViewMode getViewMode()
    {
        return this.viewMode;
    }
}
