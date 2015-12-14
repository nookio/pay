package config;

import play.Configuration;

import java.io.File;

/**
 * Created by nookio on 15/8/4.
 */
public class UnionpayConfig {

    private static Configuration configuration =  Configuration.root();
    //请求地址
    public static String APP_TRANS_URL = configuration.getString("union.cert.app.trans.url");
    //项目根目录
    public static String ROOT_PATH = new File("").getAbsolutePath();
    //证书地址
    public static String CERT_PATH = ROOT_PATH + configuration.getString("union.cert.path");
    //证书密码
    public static String PASSWORD = configuration.getString("union.cert.password");
    //验证目录
    public static String VALIDATE_CERT_DIR = ROOT_PATH + configuration.getString("union.cert.validate.cert.dir");
    //商户id
    public static String CUSTOM_ID = "";
    //证书类型
    public static String KEY_TYPE = "";

    public static String UNIONPAY_APP_NOTIFY_URL = "";
}
