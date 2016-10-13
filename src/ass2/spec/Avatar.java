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
    private double[] position;
    private double[] rotation;
    private double[] look;
    
    private double position_step = 0.1;
    private double angle_step = 1;
    
    public Avatar()
    {
        reset();
    }
    
    /**
     * Move the avatar to the right
     */
    public void moveRight()
    {
        position[0] += position_step * Math.cos(rotation[0] / 180 * Math.PI);
        position[1] += position_step * (-Math.sin(rotation[0] / 180 * Math.PI));
        System.out.println("Move right");
    }
    
    /**
     * Move the avatar to the left
     */
    public void moveLeft()
    {
        position[0] -= position_step * Math.cos(rotation[0] / 180 * Math.PI);
        position[1] -= position_step * (-Math.sin(rotation[0] / 180 * Math.PI));
        System.out.println("Move left");
    }
    
    /**
     * Move the avatar forward
     */
    public void moveForward()
    {
        position[1] += position_step * Math.cos(rotation[0] / 180 * Math.PI);
        position[0] += position_step * Math.sin(rotation[0] / 180 * Math.PI);
        System.out.println("Move up");
    }
    
    /**
     * Move the avatar backwards
     */
    public void moveBackward()
    {
        
        position[1] -= position_step * Math.cos(rotation[0] / 180 * Math.PI);
        position[0] -= position_step * Math.sin(rotation[0] / 180 * Math.PI);
        System.out.println("Move down");
    }
    
    /**
     * Move the focus point of the camera up
     */
    public void lookUp()
    {
        if (rotation[1] > -80)
        {
            rotation[1] -= angle_step;
        }
    }
    
    /**
     * Move the focus point of the camera down
     */
    public void lookDown()
    {
        if (rotation[1] < 80)
        {
            rotation[1] += angle_step;
        }
    }
    
    /**
     * Make the avatar look to the right
     */
    public void lookRight()
    {
        rotation[0] += angle_step;
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
        rotation[0] -= angle_step;
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
        position = new double[] { 0, 5, 0 };
        rotation = new double[] { 0, 0, 0 };
        look = new double[] { 0, -10, 5 };
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
