package alex.carcar.triangle;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    public static int POSITION_ATTRIBUTE = 0;

    private int _program = -1;
    private float _translate = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8,8,8,8,0,0);
        glSurfaceView.setRenderer(this);
        setContentView(glSurfaceView);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        String vertexShaderSource = "" +
                "attribute vec2 position;\n" +
                "uniform float translate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   gl_Position = vec4(position.x + translate, position.y, 0.0, 1.0);\n" +
                "}\n" +
                "\n";

        String fragmentShaderSource = "" +
                "\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);\n" +
                "}\n" +
                "\n";

        // Write and compile a vertex shader object
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader,vertexShaderSource);
        GLES20.glCompileShader(vertexShader);
        Log.i("Vertex Shader", "Compile Log: " + GLES20.glGetShaderInfoLog(vertexShader));

        // Write and compile a fragment shader object
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader,fragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);
        Log.i("Fragment Shader", "Compile Log: " + GLES20.glGetShaderInfoLog(fragmentShader));

        // Link the shader into a program object
        _program = GLES20.glCreateProgram();
        GLES20.glAttachShader(_program, vertexShader);
        GLES20.glAttachShader(_program, fragmentShader);
        GLES20.glBindAttribLocation(_program, POSITION_ATTRIBUTE, "position");
        GLES20.glLinkProgram(_program);
        Log.i("Link", "Compile Log: " + GLES20.glGetShaderInfoLog(_program));

        GLES20.glUseProgram(_program);
        GLES20.glClearColor(0.8f, 0.2f, 0.2f, 1.0f);
        GLES20.glEnableVertexAttribArray(POSITION_ATTRIBUTE);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 unused) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Give OpenGL a triangle to render
        float[] geometry = new float[] {
                -1.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, -1.0f
        };

        //GLES20.glVertexAttrib2fv(POSITION_ATTRIBUTE, geometry, 0);
        ByteBuffer geometryByteBuffer = ByteBuffer.allocateDirect(geometry.length*4);
        geometryByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer geometryBuffer = geometryByteBuffer.asFloatBuffer();
        geometryBuffer.put(geometry);
        geometryBuffer.rewind();
        GLES20.glVertexAttribPointer(POSITION_ATTRIBUTE, 2, GLES20.GL_FLOAT, false, 4*2, geometryBuffer);

        _translate += 0.01f;
        if (_translate > 2.0f) {
            _translate = -2.0f;
        }

        GLES20.glUniform1f(GLES20.glGetUniformLocation(_program, "translate"), _translate);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, geometry.length / 2);
    }
}
