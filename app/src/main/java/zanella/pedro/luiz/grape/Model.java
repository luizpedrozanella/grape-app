package zanella.pedro.luiz.grape;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Model {

    @Expose (serialize = false, deserialize = false)
    private Bitmap startImage;

    @Expose (serialize = false, deserialize = false)
    private Bitmap resultImage;

    @Expose (serialize = false, deserialize = false)
    private Bitmap croppedImage;

    @Expose
    @SerializedName("img_b64")
    private String startImageB64;

    @Expose
    @SerializedName("img_result_b64")
    private String resultImageB64;

    @Expose
    @SerializedName("img_cropped_b64")
    private String croppedImageB64;

    @Expose
    @SerializedName("scale_distance")
    private float scaleDistance;

    @Expose
    @SerializedName("scale_a")
    private int[] scaleA;

    @Expose
    @SerializedName("scale_b")
    private int[] scaleB;

    @Expose
    @SerializedName("crop_a")
    private int[] cropA;

    @Expose
    @SerializedName("crop_b")
    private int[] cropB;

    @Expose
    @SerializedName("affected_area")
    private double affectedArea;

    public Model() {

    }

    public Model(String json) {
        this.fromJson(json);
    }

    public Bitmap getStartImage() {
        return startImage;
    }

    public void setStartImage(Bitmap startImage) {
        this.startImage = startImage;
    }

    public Bitmap getCopyStartImage(){
        return startImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    public Bitmap getResultImage() {
        return resultImage;
    }

    public void setResultImage(Bitmap resultImage) {
        this.resultImage = resultImage;
    }

    public Bitmap getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(Bitmap croppedImage) {
        this.croppedImage = croppedImage;
    }

    public String getStartImageB64() {
        return startImageB64;
    }

    public void setStartImageB64(String startImageB64) {
        this.startImageB64 = startImageB64;
    }

    public String getResultImageB64() {
        return resultImageB64;
    }

    public void setResultImageB64(String resultImageB64) {
        this.resultImageB64 = resultImageB64;
    }

    public String getCroppedImageB64() {
        return croppedImageB64;
    }

    public void setCroppedImageB64(String croppedImageB64) {
        this.croppedImageB64 = croppedImageB64;
    }

    public float getScaleDistance() {
        return scaleDistance;
    }

    public void setScaleDistance(float scaleDistance) {
        this.scaleDistance = scaleDistance;
    }

    public int[] getScaleA() {
        return scaleA;
    }

    public void setScaleA(int[] scaleA) {
        this.scaleA = scaleA;
    }

    public int[] getScaleB() {
        return scaleB;
    }

    public void setScaleB(int[] scaleB) {
        this.scaleB = scaleB;
    }

    public int[] getCropA() {
        return cropA;
    }

    public void setCropA(int[] cropA) {
        this.cropA = cropA;
    }

    public int[] getCropB() {
        return cropB;
    }

    public void setCropB(int[] cropB) {
        this.cropB = cropB;
    }

    public double getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(double affectedArea) {
        this.affectedArea = affectedArea;
    }

    @Override
    public String toString() {
        return "Model{" +
                "scaleDistance=" + scaleDistance +
                ", scaleA=" + Arrays.toString(scaleA) +
                ", scaleB=" + Arrays.toString(scaleB) +
                ", cropA=" + Arrays.toString(cropA) +
                ", cropB=" + Arrays.toString(cropB) +
                ", affectedArea=" + affectedArea +
                '}';
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        if (startImage != null) {
            startImageB64 = Utility.BitmapToBase64(startImage);
        }

        if (resultImage != null) {
            resultImageB64 = Utility.BitmapToBase64(resultImage);
        }

        if (croppedImage != null) {
            croppedImageB64 = Utility.BitmapToBase64(croppedImage);
        }

        return gson.toJson(this);
    }

    public void fromJson(String json) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        Model modelTemp = gson.fromJson(json, Model.class);

        this.startImageB64 = modelTemp.startImageB64;
        this.resultImageB64 = modelTemp.resultImageB64;
        this.croppedImageB64 = modelTemp.croppedImageB64;
        this.scaleDistance = modelTemp.scaleDistance;
        this.scaleA = modelTemp.scaleA;
        this.scaleB = modelTemp.scaleB;
        this.cropA = modelTemp.cropA;
        this.cropB = modelTemp.cropB;
        this.affectedArea = modelTemp.affectedArea;

        if (startImageB64 != null) {
           startImage = Utility.Base64ToBitmap(startImageB64);
        }

        if (resultImageB64 != null) {
           resultImage = Utility.Base64ToBitmap(resultImageB64);
        }

        if (croppedImageB64 != null) {
            croppedImage = Utility.Base64ToBitmap(croppedImageB64);
        }
    }
}
