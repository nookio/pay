package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.client.AsyncHttpClientConfig;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import play.api.libs.ws.WSClientConfig;
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder;
import play.api.libs.ws.ning.NingWSClientConfig;
import play.api.libs.ws.ning.NingWSClientConfigFactory;
import play.api.libs.ws.ssl.SSLConfigFactory;
import play.libs.F;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.libs.ws.ning.NingWSClient;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by nookio on 15/8/1.
 */
public class HttpUtil{

    private static String ENCODING = "UTF-8";

    private static long HTTP_TIME_OUT = 10000l;

    private static Integer CONNECTION_TIME_OUT = 10000;

    private static Integer READ_TIME_OUT = 10000;

    @Inject
    WSClient sad;

    private static WSClient ws;

    static {
        // Set up the client config (you can also use a parser here):
        scala.Option<String> noneString = scala.None$.empty();
        WSClientConfig wsClientConfig = new WSClientConfig(
                Duration.apply(120, TimeUnit.SECONDS), // connectionTimeout
                Duration.apply(120, TimeUnit.SECONDS), // idleTimeout
                Duration.apply(120, TimeUnit.SECONDS), // requestTimeout
                true, // followRedirects
                false, // useProxyProperties
                noneString, // userAgent
                true, // compressionEnabled / enforced
                SSLConfigFactory.defaultConfig());

        NingWSClientConfig clientConfig = NingWSClientConfigFactory.forClientConfig(wsClientConfig);

        // Build a secure config out of the client config:
        NingAsyncHttpClientConfigBuilder secureBuilder = new NingAsyncHttpClientConfigBuilder(clientConfig);
        AsyncHttpClientConfig secureDefaults = secureBuilder.build();

        // You can directly use the builder for specific options once you have secure TLS defaults...
        AsyncHttpClientConfig customConfig = new AsyncHttpClientConfig.Builder(secureDefaults)
                .setCompressionEnforced(true)
                .build();
        ws = new NingWSClient(customConfig);
    }

    /**
     * 发送String型的xml格式字符串
     * @param url
     * @param xmlData
     * @return
     */
    public static Document doPostAsXml(String url, String xmlData){
         return ws.url(url).post(xmlData).map(new F.Function<WSResponse, Document>() {
             @Override
             public Document apply(WSResponse wsResponse) throws Throwable {
                 return wsResponse.asXml();
             }
         }).get(HTTP_TIME_OUT);
    }


    /**
     * 发送String型的json格式字符串
     * @param url
     * @param jsonData
     * @return string型的json字符串
     */
    public static String doPostAsJson(String url, String jsonData){
        return ws.url(url).post(jsonData).map(new F.Function<WSResponse, JsonNode>() {
            @Override
            public JsonNode apply(WSResponse wsResponse) throws Throwable {
                return wsResponse.asJson();
            }
        }).get(HTTP_TIME_OUT).toString();
    }

    /**
     * 进行get访问
     * @param url
     * @return
     */
    public static String doGet(String url){
        return ws.url(url).get().map(new F.Function<WSResponse, String>() {
            @Override
            public String apply(WSResponse wsResponse) throws Throwable {
                return wsResponse.getBody();
            }
        }).get(HTTP_TIME_OUT);
    }

    public static String doPost(String url, String data){
        return ws.url(url).post(data).map(new F.Function<WSResponse, String>() {
            @Override
            public String apply(WSResponse wsResponse) throws Throwable {
                return wsResponse.getBody();
            }
        }).get(HTTP_TIME_OUT);
    }

    public static String doPostOnSSL(String url, String data, Map<String, String> headers){
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = createConnection(requestUrl, headers);
            if(null == connection) {
                throw new Exception("创建联接失败");
            } else {
                String result = getRequest(connection, data, ENCODING);
                return  result;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getRequest(HttpURLConnection connection, String data,String encoding) {
        PrintStream out = null;
        InputStream in = null;
        StringBuilder sb = new StringBuilder(1024);
        String result = "";
        try{
            connection.connect();
            out = new PrintStream(connection.getOutputStream(), false, encoding);
            out.print(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            out.close();
        }

        try {
            if(200 == connection.getResponseCode()) {
                in = connection.getInputStream();
                sb.append(IOUtils.toString(in, encoding));
            } else {
                in = connection.getErrorStream();
                sb.append(IOUtils.toString(in, encoding));
            }
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(null != connection) {
                connection.disconnect();
            }
        }

        return result;
    }

    private static HttpURLConnection createConnection(URL url, Map<String, String> header) throws ProtocolException {
        HttpURLConnection httpURLConnection = null;

        try {
            httpURLConnection = (HttpURLConnection)url.openConnection();
        } catch (IOException var4) {
            var4.printStackTrace();
            return null;
        }

        httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT);
        httpURLConnection.setReadTimeout(READ_TIME_OUT);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        Iterator<String> headerIterator = header.keySet().iterator();
        while (headerIterator.hasNext()){
            String key = headerIterator.next();
            httpURLConnection.setRequestProperty(key, header.get(key));
        }
        httpURLConnection.setRequestMethod("POST");
        if("https".equalsIgnoreCase(url.getProtocol())) {
            HttpsURLConnection husn = (HttpsURLConnection)httpURLConnection;
            husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
            husn.setHostnameVerifier(new BaseHttpSSLSocketFactory.TrustAnyHostnameVerifier());
            return husn;
        } else {
            return httpURLConnection;
        }
    }

    public static class BaseHttpSSLSocketFactory extends SSLSocketFactory {
        public BaseHttpSSLSocketFactory() {
        }

        private SSLContext getSSLContext() {
            return this.createEasySSLContext();
        }

        public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
            return this.getSSLContext().getSocketFactory().createSocket(arg0, arg1, arg2, arg3);
        }

        public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException, UnknownHostException {
            return this.getSSLContext().getSocketFactory().createSocket(arg0, arg1, arg2, arg3);
        }

        public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
            return this.getSSLContext().getSocketFactory().createSocket(arg0, arg1);
        }

        public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
            return this.getSSLContext().getSocketFactory().createSocket(arg0, arg1);
        }

        public String[] getSupportedCipherSuites() {
            return null;
        }

        public String[] getDefaultCipherSuites() {
            return null;
        }

        public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
            return this.getSSLContext().getSocketFactory().createSocket(arg0, arg1, arg2, arg3);
        }

        private SSLContext createEasySSLContext() {
            try {
                SSLContext e = SSLContext.getInstance("SSL");
                e.init((KeyManager[]) null, new TrustManager[]{BaseHttpSSLSocketFactory.MyX509TrustManager.manger}, (SecureRandom) null);
                return e;
            } catch (Exception var2) {
                var2.printStackTrace();
                return null;
            }
        }

        public static class TrustAnyHostnameVerifier implements HostnameVerifier {
            public TrustAnyHostnameVerifier() {
            }

            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }

        public static class MyX509TrustManager implements X509TrustManager {
            static BaseHttpSSLSocketFactory.MyX509TrustManager manger = new BaseHttpSSLSocketFactory.MyX509TrustManager();

            public MyX509TrustManager() {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }
    }
}
