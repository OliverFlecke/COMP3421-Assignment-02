#version 130

in vec3 N;
in vec3 v;
in vec3 frag_col;

void main(void) 
{
	vec3 lightDir = normalize(gl_LightSource[0].position.xyz - v);
    vec3 dirToView = normalize(-v);
    vec3 normal = normalize(N);

    // Ambient light calculations 
    vec4 globalAmbient = gl_LightModel.ambient * 
        gl_FrontMaterial.ambient; 
    vec4 ambient = gl_LightSource[0].ambient * 
        gl_FrontMaterial.ambient;

    float NdotL = max(dot(normal, lightDir), 0.0);
    vec4 diffuse = NdotL * 
        gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;

    vec4 specular = vec4(frag_col, 1.0);
    vec3 H = normalize(dirToView + lightDir); 

    // compute specular term if NdotL is larger than  zero 
    if (NdotL > 0.0) 
    { 
        float NdotHV = max(dot(normal, H),0.0); 
        specular = gl_FrontMaterial.specular * 
            gl_LightSource[0].specular * pow(NdotHV, gl_FrontMaterial.shininess); 
    }

    gl_FragColor = globalAmbient + ambient + diffuse + specular;
}


