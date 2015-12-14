package actions;

import actions.annoations.PayTransactional;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by nookio on 15/12/11.
 */
public class PayTransaction extends Action<PayTransactional>{


    @Override
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        return null;
    }
}
