package com.skplanet.dpa.reportbatch.common.util;

import com.os.operando.meteoroid.Meteoroid;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.protocol.ClientProtocolException;
import org.apache.hc.client5.http.sync.methods.HttpPost;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.ResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.File;
import java.io.IOException;

/**
 * Created by 1003724 on 2017-07-10.
 */
@Slf4j
public class SlackTalker {
    public static Response uploadFile(String api_token, File uploadFile, String public_channel, String title, String comment){
        Response response = null;
        try {
            response = new Meteoroid.Builder()
                    .token(api_token)
                    .uploadFile(uploadFile)
                    .channels(public_channel)
                    .title(title)
                    .initialComment(comment)
                    .build()
                    .post();
            log.info(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
        return response;
    }

    public static void talk(String endpoint, String msg){
        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            final HttpPost httppost = new HttpPost(endpoint);

            StringEntity params = new StringEntity("{\"text\":\"" + msg + "\"}", ContentType.APPLICATION_JSON);

            httppost.addHeader("'Content-type", "application/json;charset-UTF-8");
            httppost.setEntity(params);

            final ResponseHandler<String> responseHandler = new ResponseHandler<String>(){
                @Override
                public String handleResponse(ClassicHttpResponse classicHttpResponse) throws HttpException, IOException {
                    final int status = classicHttpResponse.getCode();
                    if(status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION){
                        final HttpEntity entity = classicHttpResponse.getEntity();
                        try {
                            return entity != null ? EntityUtils.toString(entity) : null;
                        }catch (ParseException ex){
                            throw new ClientProtocolException(ex);
                        }
                    }else{
                        throw new ClientProtocolException(status + "");
                    }
                }
            };

            final String responseBody = httpclient.execute(httppost, responseHandler);
            log.info(responseBody);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}