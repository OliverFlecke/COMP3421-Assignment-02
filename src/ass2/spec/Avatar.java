package ass2.spec;

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
    
    /**
     * Create an avatar 
     */
    public Avatar()
    {
        reset();
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
    
    /**
     * Move the avatar to the right
     */
    public void moveRight()
    {
        position[0] += movementSpeed * Math.cos(rotation[0] / 180 * Math.PI);
        position[1] += movementSpeed * (-Math.sin(rotation[0] / 180 * Math.PI));
    }
    
    /**
     * Move the avatar to the left
     */
    public void moveLeft()
    {
        position[0] -= movementSpeed * Math.cos(rotation[0] / 180 * Math.PI);
        position[1] -= movementSpeed * (-Math.sin(rotation[0] / 180 * Math.PI));
    }
    
    /**
     * Move the avatar forward
     */
    public void moveForward()
    {
        position[1] += movementSpeed * Math.cos(rotation[0] / 180 * Math.PI);
        position[0] += movementSpeed * Math.sin(rotation[0] / 180 * Math.PI);
    }
    
    /**
     * Move the avatar backwards
     */
    public void moveBackward()
    {
        position[1] -= movementSpeed * Math.cos(rotation[0] / 180 * Math.PI);
        position[0] -= movementSpeed * Math.sin(rotation[0] / 180 * Math.PI);
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
     * Reset the avatar to the original position, and looking in the original direction
     */
    public void reset()
    {
        position = new double[] { 0, 5, 1 };
        rotation = new double[] { 0, 0 };
        look = new double[] { 0, -10, 1 };
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
}
