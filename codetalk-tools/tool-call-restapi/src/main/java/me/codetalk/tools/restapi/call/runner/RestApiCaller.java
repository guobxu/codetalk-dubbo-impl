package me.codetalk.tools.restapi.call.runner;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class RestApiCaller implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiCaller.class);

    @Autowired
    private OkHttpClient httpClient;

    private static final String URL_HOST = "http://dev.home.phiwifi.com";

    // url 和 请求体映射关系
    private static List<PostApiRequest> POST_API_LIST = null;
    static {
        POST_API_LIST = Arrays.asList(new PostApiRequest[] {
                new PostApiRequest("/sharedwifi/v1/router/ping", "{ \"config_version\":\"sw0.1\", \"model\":\"K2\", \"rom_version\":\"22.5.10.552\", \"router_ip\":\"192.168.66.112\", \"router_mac\":\"D8:C8:E9:8C:BF:58\", \"router_uptime\":\"7469.20\", \"schedule_close\":0 }"),
                new PostApiRequest("/sharedwifi/v1/h5web/queryorder", "{ \"device_mac\":\"D8:C8:E9:BB:53:78\", \"order_id\":\"SWP00000000000000000000000257813\", \"router_mac\":\"D8:C8:E9:BB:53:78\" }"),
                new PostApiRequest("/sharedwifi/v1/app/get_sharedwifi_income", "{ \"router_mac\":\"D8:C8:E9:BB:53:78\", \"token\":\"\" }"),
                new PostApiRequest("/sharedwifi/v1/h5web/queryorder", "{ \"device_mac\": \"10:2A:B3:D5:4C:70\", \"router_mac\":\"D8:C8:E9:BA:C0:C4\", \"order_id\":\"SWT00000000000000000000000002072\" }"),
                new PostApiRequest("/sharedwifi/v1/h5web/unifiedorder", "{ \"device_mac\":\"4C:32:75:95:86:77\", \"finger_print\":\"8089e3d48fe8bab69bc398063dccea8d\", \"online_time\":\"2\", \"online_time_unit\":\"0.5\", \"online_time_unit_price\":\"0.1\", \"pay_type\":1, \"router_ip\":\"192.168.2.1\", \"router_mac\":\"D8:C8:E9:BA:C0:C4\", \"router_port\":\"2060\", \"share_para_type\":1, \"sign\":\"1236123#$#%1\", \"ssid\":\"@PHICOMM共享wifiC4\" }"),
                new PostApiRequest("/ssp/withdraw/downloadBill", "{ \"bill_date\":\"20171022\", \"bill_type\":\"ALL\" }")
        });
    }

    @Override
    public void run(String... strings) throws Exception {

        while(true) {
            POST_API_LIST.forEach(r -> {
                try {
                    postRestApi(r.relPath, r.json);

                    Thread.sleep(200);
                } catch(Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            });
        }

    }

    private void postRestApi(String relPath, String json) throws IOException {
        LOGGER.info(String.format("Call api [%s] using data [%s]", relPath, json));

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String url = URL_HOST + relPath;
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = httpClient.newCall(request);
        Response response = call.execute();

        LOGGER.info(response.body().string());
    }

}

class PostApiRequest {

    PostApiRequest(String relPath, String json) {
        this.relPath = relPath;
        this.json = json;
    }

    String relPath;
    String json;

}
