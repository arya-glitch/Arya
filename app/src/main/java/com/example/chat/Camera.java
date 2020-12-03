package com.example.chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Camera extends AppCompatActivity {

    TextureView textureView;
    String userid;
    private int REQUEST_CODE_PERMISSION=100;
    private String[] REQUIRED_PERMISSION=new String[]{"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
    private ImageView capture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera);

        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        userid=bundle.getString("userid");
        textureView=findViewById(R.id.textureView);
        capture=findViewById(R.id.capture);



        if(allPermissionGranted()){

            startCamera();

        }else {

            ActivityCompat.requestPermissions(Camera.this,REQUIRED_PERMISSION,REQUEST_CODE_PERMISSION);

        }



    }

    private boolean allPermissionGranted() {

        for(String permission:REQUIRED_PERMISSION){
            if(ContextCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void startCamera() {


        CameraX.unbindAll();

        Rational aspectRatio = new Rational(textureView.getWidth(),textureView.getHeight());
        Size screen= new Size(textureView.getWidth(),textureView.getHeight());

        PreviewConfig previewConfig=new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen).build();

        Preview preview=new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {

                ViewGroup parent= (ViewGroup)textureView.getParent();
                parent.removeView(textureView);
                parent.addView(textureView);

                textureView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });

        ImageCaptureConfig imageCaptureConfig=new ImageCaptureConfig.Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imageCapture=new ImageCapture(imageCaptureConfig);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //File file=new File("sdcard/Chat/Captured"+System.currentTimeMillis());

                 File filepath= Environment.getExternalStorageDirectory();
                 File dir=new File(filepath.getAbsolutePath()+"/Chat/"+File.separator+"Media");
                if(!dir.exists()){
                    dir.mkdir();
                }
                File file1=new File(dir,"Captured");
                if(!file1.exists()){
                    file1.mkdir();
                }
                 File file=new File(file1,System.currentTimeMillis()+".jpg");

                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        Intent intent = new Intent(getApplicationContext(), Send_image.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("imageuri", Uri.fromFile(file).toString());
                        bundle.putString("userid", userid);
                        bundle.putString("filename",file.toString());
                        bundle.putString("videouri","default");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {

                        Toast.makeText(Camera.this, "Failed to capture image ", Toast.LENGTH_SHORT).show();

                        if(cause!=null){
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });


        CameraX.bindToLifecycle(this,preview,imageCapture);

    }

    private void updateTransform() {

        Matrix mx=new Matrix();

        float w=textureView.getMeasuredWidth();
        float h=textureView.getMeasuredHeight();

        float cX=w/2f;
        float cY=h/2f;

        int rotaionDgr;
        int rotaion =(int)textureView.getRotation();

        switch (rotaion){
            case Surface.ROTATION_0:
                     rotaionDgr=0;
                     break;
            case Surface.ROTATION_90:
                    rotaionDgr=90;
                    break;
            case Surface.ROTATION_180:
                rotaionDgr=180;
                break;
            case Surface.ROTATION_270:
                rotaionDgr=270;
                break;
            default:
                return;


        }

        mx.postRotate((float)rotaionDgr, cX,cY);
        textureView.setTransform(mx);

    }
}
