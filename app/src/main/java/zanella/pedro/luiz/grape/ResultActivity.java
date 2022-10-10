package zanella.pedro.luiz.grape;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ResultActivity extends AppCompatActivity {
    public static final String MODEL_MESSAGE = "zanella.pedro.luiz.grape.MODEL_MESSAGE";
    public static final String IMAGE_URL_MESSAGE = "zanella.pedro.luiz.grape.IMAGE_URL_MESSAGE";

    private Model model;
    private ImageView imageViewResult, imageViewCrop;
    private TextView textViewAffectedArea, textViewTittleResult;
    private TableLayout tableAffectedArea;
    private Button buttonNewAnalysis;
    private Uri startImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String message = intent.getStringExtra(this.MODEL_MESSAGE);
        String imageUrlMessage = getIntent().getStringExtra(this.IMAGE_URL_MESSAGE);

        model = message != null ? new Model(message) : new Model();
        startImageUrl = Uri.parse(imageUrlMessage);

        try {
            Bitmap startImage = MediaStore.Images.Media.getBitmap(getContentResolver(), startImageUrl).copy(Bitmap.Config.ARGB_8888, true);
            model.setStartImage(startImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageViewCrop = (ImageView) findViewById(R.id.imageViewCrop);
        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);

        textViewTittleResult = (TextView) findViewById(R.id.textViewTittleResult);
        textViewTittleResult.setVisibility(View.GONE);
        textViewAffectedArea = (TextView) findViewById(R.id.textViewAffectedArea);

        tableAffectedArea = (TableLayout) findViewById(R.id.tableAffectedArea);
        tableAffectedArea.setVisibility(View.GONE);

        buttonNewAnalysis = (Button) findViewById(R.id.buttonNewnAnalysis);
        buttonNewAnalysis.setVisibility(View.GONE);
        buttonNewAnalysis.setOnClickListener(this::onClickNewAnalysis);

        new ProcessImage().execute();
    }

    private class ProcessImage extends AsyncTask<String, String, ServerResponse> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(
                    ResultActivity.this,
                    "Processando imagem", "Aguarde um momento...");
        }

        @Override
        protected ServerResponse doInBackground(String... strings) {
            return ServerConnection.ProcessImage(model);
        }

        @Override
        protected void onPostExecute(ServerResponse serverResponse) {
            progressDialog.dismiss();
            if (serverResponse.isSucess()){
                model = (Model) serverResponse.getObject();

                imageViewCrop.setImageBitmap(model.getCroppedImage());
                imageViewCrop.setScaleType(ImageView.ScaleType.FIT_CENTER);

                imageViewResult.setImageBitmap(model.getResultImage());
                imageViewResult.setScaleType(ImageView.ScaleType.FIT_CENTER);

                //border
                imageViewCrop.setBackgroundResource(R.color.black);
                imageViewCrop.setPadding(3,3,3,3);

                //border
                imageViewResult.setBackgroundResource(R.color.black);
                imageViewResult.setPadding(3,3,3,3);

                textViewAffectedArea.setText(String.format("%.2f", model.getAffectedArea()) + " cm²");

                textViewTittleResult.setVisibility(View.VISIBLE);
                tableAffectedArea.setVisibility(View.VISIBLE);
                buttonNewAnalysis.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "Processamento finalizado com sucesso!", Toast.LENGTH_SHORT).show();

            } else{
                new AlertDialog.Builder(ResultActivity.this).
                        setTitle("Erro").
                        setMessage(serverResponse.getMessage()).setPositiveButton("OK", null).create().show();
            }
        }
    }

    private void onClickNewAnalysis(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}