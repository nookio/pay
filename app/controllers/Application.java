package controllers;

import config.BaseConfig;
import models.User;
import play.*;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.*;

import services.UserService;
import utils.StringUtil;
import views.html.*;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    private static Logger.ALogger logger = play.Logger.of(Application.class);

    private static Configuration configuration =  Configuration.root();

    protected void setHost() {
        if ( !BaseConfig.HOST_INITED && StringUtil.isBlank(HOST) ){
            BaseConfig.HOST = configuration.getString("service.host");
        }
    }

    protected static boolean validateForm(Form form){
        Map errors = form.errors();
        Iterator it = errors.values().iterator();
        if(it.hasNext()){
            List<ValidationError> error = (List<ValidationError>) it.next();
            throw new RuntimeException(error.get(0).message());
        }else return true;
    }

    protected static void checkToken(Integer loginUserId, String loginToken){
        if (null == loginUserId || null == loginToken){
            logger.error("有用户企图支付" + new Date().toString());
            throw new RuntimeException("数据有误");
        }
        User user = UserService.getUserById(loginUserId);
        if (null == user){
            throw new RuntimeException("用户不存在");
        }

        if (StringUtil.isBlank(user.token)){
            throw new RuntimeException("还未登陆");
        }

        if (!loginToken.equals(user.token)){
            throw new RuntimeException("会话过期。");
        }

        if (user.payed.equals(1)){
            throw new RuntimeException("您已经支付成功请不要重复支付。");
        }
        logger.info(new Date().toString() + "     " +loginUserId + "的信息验证通过" + loginToken);
    }

}
