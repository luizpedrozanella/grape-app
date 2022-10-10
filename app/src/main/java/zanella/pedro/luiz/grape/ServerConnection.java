package zanella.pedro.luiz.grape;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection {

    public static final String BASE_URL = "https://appgrape.herokuapp.com/";

    public static ServerResponse Home() {
        try {
            URL url = new URL(BASE_URL) ;

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                return new ServerResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage(), Utility.InputStreamToString(urlConnection.getInputStream()));
            } else {
                return new ServerResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
            }

        }catch (Exception e) {
            e.printStackTrace();
            return new ServerResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        }
    }

    public static ServerResponse ProcessImage(Model model){
        try {
            URL url = new URL(BASE_URL + "process-image") ;

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setInstanceFollowRedirects(false);

            String body = model.toJson();

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            writer.write(body);
            writer.close();

            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String jsonReturn = Utility.InputStreamToString(urlConnection.getInputStream());
                model.fromJson(jsonReturn);

                return new ServerResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage(), model);
            } else {
                return new ServerResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
            }

        }catch (Exception e) {
            e.printStackTrace();
            return new ServerResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        }
    }
}
