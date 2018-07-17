#version 330 core
struct Material{
	sampler2D diffuse;
	sampler2D specular;
	float shininess;
};

struct Light{
	vec3 position;
	vec3 direction;
	float cutOff;

	vec3 ambient;
	vec3 diffuse;
	vec3 specular;

	vec3 attenuation;
};

out vec4 FragColor;

in vec3 Normal;
in vec3 FragPos;
in vec2 TexCoords;

uniform vec3 viewPos;

uniform Material material;
uniform Light light;

void main()
{	
	vec3 lightDir = normalize(light.position - FragPos);
	float theta = dot(lightDir, normalize(-light.direction));
	
	if(theta > light.cutOff)
	{

		float distance = length(light.position - FragPos);
		float attenuation = 1.0/(light.attenuation.x + light.attenuation.y * distance + light.attenuation.z * (distance * distance));

		vec3 ambient = light.ambient * vec3(texture(material.diffuse, TexCoords));

		vec3 norm = normalize(Normal);
		float diff = max(dot(norm, lightDir), 0.0);
		vec3 diffuse = diff * light.diffuse * vec3(texture(material.diffuse, TexCoords));	
	
		vec3 viewDir = normalize(viewPos - FragPos);
		vec3 reflectDir = reflect(-lightDir, norm);
		float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
		vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoords));
	
		diffuse *= attenuation;
		specular *= attenuation;

		FragColor = vec4((ambient + diffuse + specular), 1.0);
	}
	else
	{
		FragColor = vec4(light.ambient * vec3(texture(material.diffuse, TexCoords)), 1.0);
	}
} 
