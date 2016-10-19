#version 130

out vec4 color;

in vec3 frag_pos;  
in vec3 normal;  

uniform float ambient_intensity;
uniform float specular_intensity; 
uniform vec3 specular_factor;
 
uniform vec3 light_pos; 
uniform vec3 view_pos;
uniform vec3 light_color;
uniform vec3 object_color;
void main()
{
    /* Calculate the ambient compoonent. */
    float ambient_intensity = ambient_intensity;
    vec3 ambient = (ambient_intensity * light_color);
        
    /* Calculate the diffuse component. */
    vec3 norm = normalize(normal);
    vec3 light_direction = normalize(light_pos - frag_pos);
    float diff = max(dot(norm, light_direction), 0.0);
    vec3 diffuse = (diff * light_color);
    
    /* Calculate the specular component. */
    vec3 view_direction = normalize(view_pos - frag_pos);
    float spec = pow((dot(norm, view_direction)), specular_factor);
   
    float spec = 1;
    vec3 specular = (specular_intensity * spec * light_color);  
        
    vec3 result = (ambient + specular + diffuse) * object_color;
    color = vec4(result, 1.0f);
} 

