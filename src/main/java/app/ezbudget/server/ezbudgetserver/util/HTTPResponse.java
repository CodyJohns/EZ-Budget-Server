package app.ezbudget.server.ezbudgetserver.util;

public class HTTPResponse<T> {

    private int status;
    private String message;
    private T data;

    public HTTPResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public HTTPResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
