package zanella.pedro.luiz.grape;

import java.net.HttpURLConnection;

public class ServerResponse {
    private boolean sucess;
    private int code;
    private String message;
    private Object object;

    public ServerResponse(boolean sucess, int code, String message, Object object) {
        this.sucess = sucess;
        this.code = code;
        this.message = message;
        this.object = object;
    }

    public ServerResponse(int code, String message, Object object) {
        this.sucess = (code == HttpURLConnection.HTTP_OK);
        this.code = code;
        this.message = message;
        this.object = object;
    }

    public ServerResponse(int code, String message) {
        this.sucess = (code == HttpURLConnection.HTTP_OK);
        this.code = code;
        this.message = message;
        this.object = null;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
