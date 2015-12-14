package pays.Strategy;

import java.math.BigDecimal;

/**
 * Created by nookio on 15/12/14.
 */
public class Debug extends Strategy {

    @Override
    public BigDecimal algorithm() {
        return new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }
}
