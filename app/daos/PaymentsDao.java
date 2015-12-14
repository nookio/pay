package daos;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import models.Payment;


/**
 * Created by nookio on 15/5/12.
 */
public class PaymentsDao {

    private static Model.Finder<Integer, Payment> find = new Model.Finder<Integer, Payment>(Payment.class);

    public static void save(Payment payment){
        Ebean.save(payment);
    }

    public static void update (Payment payment) {
            Ebean.save(payment);
    }

    public static Payment find(String outTradeNo) {
        return find.byId(Integer.parseInt(outTradeNo));
    }

}
