package zanella.pedro.luiz.grape;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Arrays;

public class ConfigureScaleActivity extends AppCompatActivity {
    public static final String MODEL_MESSAGE = "zanella.pedro.luiz.grape.MODEL_MESSAGE";
    public static final String IMAGE_URL_MESSAGE = "zanella.pedro.luiz.grape.IMAGE_URL_MESSAGE";

    private Model model;
    private ImageView imageViewScale;
    private Button buttonConfirmScale;
    private EditText editTextScale;
    private Paint paint;
    private Canvas canvas;
    private Bitmap imageScaleBitmap, copyImageScaleBitmap;
    private float onTouchStartX, onTouchStartY;
    private Uri imageScaleUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_scale);

        Intent intent = getIntent();
        String modelMessageJson = intent.getStringExtra(this.MODEL_MESSAGE);
        String imageUrlMessage = getIntent().getStringExtra(this.IMAGE_URL_MESSAGE);

        model = modelMessageJson != null ? new Model(modelMessageJson) : new Model();
        imageScaleUrl = Uri.parse(imageUrlMessage);

        try {
            copyImageScaleBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageScaleUrl).copy(Bitmap.Config.ARGB_8888, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageViewScale = (ImageView) findViewById(R.id.imageViewCrop);
        imageViewScale.setOnTouchListener(this::onTouchImage);
        imageViewScale.setImageBitmap(imageScaleBitmap);

        editTextScale = (EditText) findViewById(R.id.editTextScale);
        editTextScale.addTextChangedListener(scaleWatcher);

        buttonConfirmScale = (Button) findViewById(R.id.buttonProcessImage);
        buttonConfirmScale.setOnClickListener(this::onClickConfirm);
        enableButtonConfirm();

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(5);


        Log.i("LUIZ", "START Width -> " + imageViewScale.getWidth());
        Log.i("LUIZ", "START Height -> " + imageViewScale.getHeight());

        resetImage();
    }

    private TextWatcher scaleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                model.setScaleDistance(Float.parseFloat(editTextScale.getText().toString()));
            } catch (NumberFormatException e) {
                model.setScaleDistance(0);
            }
            enableButtonConfirm();
        }
    };

    public boolean onTouchImage(View view, MotionEvent event) {
        if (imageScaleBitmap != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchStartX = event.getX();
                    onTouchStartY = event.getY();
                    resetImage();
                    break;
                case MotionEvent.ACTION_MOVE:
                    resetImage();
                    drawLine((ImageView) view, imageScaleBitmap, onTouchStartX, onTouchStartY, event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    drawLine((ImageView) view, imageScaleBitmap, onTouchStartX, onTouchStartY, event.getX(), event.getY());
                    break;
            }
            enableButtonConfirm();
        }
        return true;
    }

    private void drawLine(ImageView imageView, Bitmap bitmap, float x0, float y0, float x1, float y1){
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

            canvas.drawLine(startX, startY, stopX, stopY, paint);
            imageView.invalidate();

            model.setScaleA(new int[]{startX, startY});
            model.setScaleB(new int[]{stopX, stopY});
        }
    }

    public void onClickConfirm(View view) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.putExtra(CropImageActivity.MODEL_MESSAGE, model.toJson());
        intent.putExtra((CropImageActivity.IMAGE_URL_MESSAGE), imageScaleUrl.toString());
        startActivity(intent);
    }

    private void enableButtonConfirm() {
        buttonConfirmScale.setEnabled((model.getScaleDistance() != 0)
                && model.getScaleA() != null
                && model.getScaleB() != null
                && !Arrays.equals(model.getScaleA(), model.getScaleB()));
    }

    private void resetImage() {
        imageScaleBitmap = copyImageScaleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        imageViewScale.setImageBitmap(imageScaleBitmap);
        canvas = new Canvas(imageScaleBitmap);

        Log.i("LUIZ", "imageScaleBitmap.getWidth() -> " + imageScaleBitmap.getWidth());
        Log.i("LUIZ", "imageScaleBitmap.getHeight() -> " + imageScaleBitmap.getHeight());
        Log.i("LUIZ", "imageViewScale.getWidth() -> " + imageViewScale.getWidth());
        Log.i("LUIZ", "imageViewScale.getHeight() -> " + imageViewScale.getHeight());


        float ratioWidth = (float) imageScaleBitmap.getWidth() / (float) imageViewScale.getWidth();
        float ratioHeight = (float) imageScaleBitmap.getHeight() / (float) imageViewScale.getHeight();

        Log.i("LUIZ", "ratioWidth -> " + ratioWidth);
        Log.i("LUIZ", "ratioHeight -> " + ratioHeight);
    }
}