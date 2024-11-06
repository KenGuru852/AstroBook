precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D uTexture;
uniform float uTime;

//void main() {
//    // Создаем эффект волн, изменяя текстурные координаты
//    vec2 waveCoord1 = vTextureCoord + vec2(0.0, 0.02 * sin(vTextureCoord.y * 20.0 + uTime));
//    vec2 waveCoord2 = vTextureCoord + vec2(0.02 * sin(vTextureCoord.x * 15.0 + uTime), 0.0);
//    vec2 waveCoord = mix(waveCoord1, waveCoord2, 0.5);
//    gl_FragColor = texture2D(uTexture, waveCoord);
//}
void main() {
    // Создаем эффект волн, изменяя текстурные координаты
    vec2 waveCoord = vTextureCoord + vec2(0.0, 0.02 * sin(vTextureCoord.y * 20.0 + uTime));
    gl_FragColor = texture2D(uTexture, waveCoord);
}