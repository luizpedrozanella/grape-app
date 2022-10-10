package zanella.pedro.luiz.grape;

import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE_PICK = 100;
    public static final int REQUEST_TAKE_PHOTO = 200;
    public static final int REQUEST_PERMISSION_CAMERA = 300;

    private Model model;
    private ImageView imageViewStart;
    private Button buttonCamera, buttonGallery;
    private Bitmap imageStartBitmap;
    private Uri startImageUrl = null;
    private String currentPhotoPath;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new Model();

        imageViewStart = (ImageView) findViewById(R.id.imageViewStart);
        imageViewStart.setImageResource(R.drawable.ic_image_grey600_36dp);

        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(this::dispatchTakePictureIntent);

        buttonGallery = (Button) findViewById(R.id.buttonGallery);
        buttonGallery.setOnClickListener(this::dispatchPickPhotoIntent);
    }

    private void dispatchTakePictureIntent(View view) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
        } else {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //getFilesDir(); //getExternalFilesDir(Environment.DIRECTORY_PICTURES); //getCacheDir(); //
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchPickPhotoIntent(View view) {
        Intent intentPickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intentPickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults != null
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED //CAMERA
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) { //WRITE_EXTERNAL_STORAGE
                dispatchTakePictureIntent(buttonCamera);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode){
                case REQUEST_IMAGE_PICK:
                    startImageUrl = data.getData();
                    break;

                case REQUEST_TAKE_PHOTO:
                    startImageUrl = Uri.fromFile(photoFile);
                    break;
            }

            try {
                imageStartBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), startImageUrl).copy(Bitmap.Config.ARGB_8888, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, ConfigureScaleActivity.class);
            intent.putExtra(ConfigureScaleActivity.MODEL_MESSAGE, model.toJson());
            intent.putExtra(ConfigureScaleActivity.IMAGE_URL_MESSAGE, startImageUrl.toString());
            startActivity(intent);
        }
    }
}