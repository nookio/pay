import org.junit.Test;
import play.Configuration;

/**
 * Created by nookio on 15/12/11.
 */
public class TestConfig {

    @Test
    public void  testConfig(){
        Configuration configuration = Configuration.root();
        System.out.println(configuration.asMap().toString());
    }
}
