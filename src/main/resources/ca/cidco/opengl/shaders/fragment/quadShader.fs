#version 330 core

out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D texture1;

void main()
{
    FragColor = texture(texture1, TexCoords);
	float average = 0.2126 * FragColor.x + 0.7152 * FragColor.y + 0.0722 * FragColor.z;
	FragColor = vec4(vec3(average), 1.0);
}
