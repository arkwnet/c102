package jp.arkw.alps.fe;

public class SendPostTaskParams {
    String url;
    String postData;

    SendPostTaskParams(String url, String postData) {
        this.url = url;
        this.postData = postData;
    }
}