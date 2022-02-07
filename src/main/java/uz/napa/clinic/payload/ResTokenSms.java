package uz.napa.clinic.payload;

public class ResTokenSms {
    private String message;

    private String data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResTokenSms{" +
                "message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
