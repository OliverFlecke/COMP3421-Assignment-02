package ass2.spec;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * Represent an enemy in the game world
 * @author Oliver Fleckenstein
 *
 */
public class Enemy extends TerrainElement 
{
    private float positions[] =  {0,1,-1, 
            -1,-1,-1,
            1,-1,-1, 
            0, 2,-4,
            -2,-2,-4, 
            2,-2,-4};

    //There should be a matching entry in this array for each entry in
    //the positions array
    private float colors[] =     {1,0,0, 
            0,1,0,
            1,1,1,
            0,0,0,
            0,0,1, 
            1,1,0}; 

    //Best to use smallest data type possible for indexes 
    //We could even use byte here...
    private short indexes[] = {0,1,5,3,4,2};

    //These are not vertex buffer objects, they are just java containers
    private FloatBuffer  posData= Buffers.newDirectFloatBuffer(positions);
    private FloatBuffer colorData = Buffers.newDirectFloatBuffer(colors);
    private ShortBuffer indexData = Buffers.newDirectShortBuffer(indexes);

    //We will be using 2 vertex buffer objects
    private int bufferIds[] = new int[2];
    
    
     private static final String VERTEX_SHADER = "src/ass2/spec/shaders/AttributeVertex.glsl";
     private static final String FRAGMENT_SHADER = "src/ass2/spec/shaders/AttributeFragment.glsl";
     
     private int shaderprogram;
    
    public Enemy(double x, double y, Terrain terrain)
    {
        this.setTerrain(terrain);
        this.position[0] = x;
        this.position[1] = y;
        this.position[2] = this.getTerrain().getAltitude(x, y);
    }
    
    public void init(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        
       //Generate 2 VBO buffer and get their IDs
        gl.glGenBuffers(2,bufferIds,0);
       
        //This buffer is now the current array buffer
        //array buffers hold vertex attribute data
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
     
        //This is just setting aside enough empty space
        //for all our data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,    //Type of buffer  
                   positions.length * Float.BYTES +  colors.length* Float.BYTES, //size needed
                   null,    //We are not actually loading data here yet
                   GL2.GL_STATIC_DRAW); //We expect once we load this data we will not modify it

        //Actually load the positions data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, //From byte offset 0
                positions.length*Float.BYTES,posData);

        //Actually load the color data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
                positions.length*Float.BYTES,  //Load after the position data
                colors.length*Float.BYTES,colorData);
        
        //Now for the element array
        //Element arrays hold indexes to an array buffer
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);

        //We can load it all at once this time since there are not
        //two separate parts like there was with color and position.
        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER,      
               indexes.length *Short.BYTES,
               indexData, GL2.GL_STATIC_DRAW);
                
        try {
            shaderprogram = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void display(GLAutoDrawable drawable) 
    {
        GL2 gl = drawable.getGL().getGL2();
        gl.glPushMatrix();
        gl.glTranslated(this.getPosition()[0], this.getPosition()[1], this.getPosition()[2]);
        //Use the shader
        gl.glUseProgram(shaderprogram);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
           
        int vertexColLoc = gl.glGetAttribLocation(shaderprogram,"vertexCol");
        int vertexPosLoc = gl.glGetAttribLocation(shaderprogram,"vertexPos");
               
        // Specify locations for the co-ordinates and color arrays.
        gl.glEnableVertexAttribArray(vertexPosLoc);
        gl.glEnableVertexAttribArray(vertexColLoc);
        gl.glVertexAttribPointer(vertexPosLoc,3, GL.GL_FLOAT, false,0, 0); //last num is the offset
        gl.glVertexAttribPointer(vertexColLoc,3, GL.GL_FLOAT, false,0, positions.length*Float.BYTES);
       
        
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
     
    
        gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_SHORT,0);    
        
        
        gl.glUseProgram(0);
           
        //Un-bind the buffer. 
        //This is not needed in this simple example but good practice
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,0);
        
        gl.glPopMatrix();
    }
}
