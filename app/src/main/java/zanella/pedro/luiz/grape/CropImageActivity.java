package zanella.pedro.luiz.grape;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

public class CropImageActivity extends AppCompatActivity {
    public static final String MODEL_MESSAGE = "zanella.pedro.luiz.grape.MODEL_MESSAGE";
    public static final String IMAGE_URL_MESSAGE = "zanella.pedro.luiz.grape.IMAGE_URL_MESSAGE";

    private Model model;
    private ImageView imageViewCrop;
    private Button buttonProcessImage;
    private Paint paint;
    private Canvas canvas;
    private Bitmap imageCropBitmap, copyImageCropBitmap;
    private float onTouchStartX, onTouchStartY;
    private Uri imageCropUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        Intent intent = getIntent();
        String modelMessageJson = intent.getStringExtra(this.MODEL_MESSAGE);
        String imageUrlMessage = getIntent().getStringExtra(this.IMAGE_URL_MESSAGE);

        model = modelMessageJson != null ? new Model(modelMessageJson) : new Model();
        imageCropUrl = Uri.parse(imageUrlMessage);

        try {
            copyImageCropBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageCropUrl).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageViewCrop = (ImageView) findViewById(R.id.imageViewCrop);
        imageViewCrop.setOnTouchListener(this::onTouchImage);
        imageViewCrop.setImageBitmap(imageCropBitmap);

        buttonProcessImage = (Button) findViewById(R.id.buttonProcessImage);
        buttonProcessImage.setOnClickListener(this::onClickProcess);
        enableButtonProcess();

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(5);

        resetImage();
    }

    public boolean onTouchImage(View view, MotionEvent event) {
        if (imageCropBitmap != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchStartX = event.getX();
                    onTouchStartY = event.getY();
                    resetImage();
                    break;
                case MotionEvent.ACTION_MOVE:
                    resetImage();
                    drawRect((ImageView) view, imageCropBitmap, onTouchStartX, onTouchStartY, event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    drawRect((ImageView) view, imageCropBitmap, onTouchStartX, onTouchStartY, event.getX(), event.getY());
                    break;
            }
            enableButtonProcess();
        }
        return true;
    }

    private void drawRect(ImageView imageView, Bitmap bitmap, float x0, float y0, float x1, float y1){
        if (x1 < 0 || y1 < 0 || x1 > imageView.getWidth() || y1 > imageView.getHeight()){
            //outside ImageView
            return;
        } else {

            float ratioWidth = (float) bitmap.getWidth() / (float) imageView.getWidth();
            float ratioHeight = (float) bitmap.getHeight() / (float) imageView.getHeight();

            int startX = (int) (x0 * ratioWidth);
            int startY = (int) (y0 * ratioHeight);
            int stopX  = (int) (x1 * ratioWidth);
            int stopY  = (int) (y1 * ratioHeight);

            canvas.drawRect(startX, startY, stopX, stopY, paint);
            imageView.invalidate();

            model.setCropA(new int[]{startX, startY});
            model.setCropB(new int[]{stopX, stopY});
        }
    }

    private void onClickProcess(View view) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.MODEL_MESSAGE, model.toJson());
        intent.putExtra(ResultActivity.IMAGE_URL_MESSAGE, imageCropUrl.toString());
        startActivity(intent);
    }

    private void resetImage() {
        imageCropBitmap = copyImageCropBitmap.copy(Bitmap.Config.ARGB_8888, true);
        imageViewCrop.setImageBitmap(imageCropBitmap);
        canvas = new Canvas(imageCropBitmap);
    }

    private void enableButtonProcess() {
        buttonProcessImage.setEnabled(model.getCropA() != null
                && model.getCropB() != null
                && !Arrays.equals(model.getCropA(), model.getCropB()));
    }
}