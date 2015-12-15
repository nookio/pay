package config;

/**
 * Created by nookio on 15/12/11.
 */
public abstract class BaseConfig {

    public static boolean HOST_INITED = false;

    public static String HOST = "";

    public static void setHost(String host){
        HOST = host;
        HOST_INITED = true;
    }

}
