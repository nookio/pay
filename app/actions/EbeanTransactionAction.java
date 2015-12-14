package actions;

import com.avaje.ebean.Ebean;
import helper.ResponseHelper;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by nookio on 15/6/9.
 */
public class EbeanTransactionAction extends Action.Simple{
    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        try {
            Ebean.beginTransaction();
            F.Promise<Result> result = delegate.call(context);
            Ebean.commitTransaction();
            return result;
        } catch (RuntimeException e) {
            Ebean.rollbackTransaction();
            return F.Promise.pure(ResponseHelper.badAjax("ERROR", e.getMessage()));
            //return  F.Promise.pure(ResponseHelper.wrapAjax(e.getMessage()));
        } catch (Throwable e){
            Ebean.rollbackTransaction();
            return F.Promise.pure(ResponseHelper.badAjax("ERROR", e.getMessage()));
        } finally {
            Ebean.endTransaction();
        }
    }
}
