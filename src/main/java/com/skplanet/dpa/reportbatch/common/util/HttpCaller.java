package com.skplanet.dpa.reportbatch.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.protocol.ClientProtocolException;
import org.apache.hc.client5.http.sync.methods.HttpGet;
import org.apache.hc.client5.http.sync.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.ResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by 1003724 on 2017-07-25.
 */
public class HttpCaller {

    public HttpCaller(){}

    public String getHttpResponse(HttpUriRequestBase httpbase) throws IOException, URISyntaxException {
        String responseBody = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();

        final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(ClassicHttpResponse classicHttpResponse) throws HttpException, IOException {
                final int status = classicHttpResponse.getCode();
                if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                    final HttpEntity entity = classicHttpResponse.getEntity();
                    try {
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } catch (ParseException ex) {
                        throw new ClientProtocolException(ex);
                    }
                } else {
                    throw new ClientProtocolException(status + "");
                }
            }
        };

        responseBody = httpclient.execute(httpbase, responseHandler);
        return responseBody;
    }

    public static void main(String[] args) {
        HttpCaller call = new HttpCaller();
        String url = "http://iapis.11st.co.kr:8765/intelligence/v1/sellers/{seller_mem_no}/products/{prd_no}/estimateddeliverydate?orderDateTime={ord_dt}&memberNo={buyr_mem_no}";
        url = url.replace("{seller_mem_no}", "39911445").replace("{prd_no}", "1244963050").replace("{buyr_mem_no}", "39941349");
        HttpGet httpGet = new HttpGet(url);
        try {
            String result = call.getHttpResponse(httpGet);
            JsonObject object = new JsonParser().parse(result).getAsJsonObject();
            object.keySet().forEach((v) -> {
                System.out.println(v + " : " + object.get(v).getAsString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
