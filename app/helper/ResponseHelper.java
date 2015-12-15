package helper;

import com.alibaba.fastjson.serializer.PropertyFilter;
import pays.CreatePay;
import pays.NotifyPay;
import play.mvc.Result;
import utils.FastJson;

import java.util.HashMap;
import java.util.Map;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

//import data.DataProvider;
//import data.FieldsFilter;

/**
 * Created by yuzhen on 15/4/9.
 */
public class ResponseHelper {

    public static Result wrapAjax(String jsonStr) {
        return ok(jsonStr).as("application/json; charset=utf-8");
    }

    public static Result badAjax(String error, String str){
        Map<String,String> map = new HashMap<>();
        map.put(error,str);
        return badRequest(FastJson.toJsonString(map)).as("application/json; charset=utf-8");
    }


    public static Result wrapAjax(Object data) {
        return wrapAjax(FastJson.toJsonString(data));
    }

    public static Result wrapAjax(Object data, PropertyFilter filter) {
        return wrapAjax(FastJson.toJsonString(data, filter));
    }

    public static Result wrapPay(CreatePay createPay){
        return wrapAjax(createPay.getPrePayInfo());
    }

    public static Result wrapPayNotify(NotifyPay notifyPay){
        return wrapAjax(notifyPay.getSuccessResult());
    }

//    public static <T extends Model> Result wrapPage(DataProvider<T> provider) {
//        response().setHeader(GlobalConfig.HEADER_PAGINATION_TOTAL_COUNT, provider.getTotalCount() + "");
//        response().setHeader(GlobalConfig.HEADER_PAGINATION_PAGE_COUNT, provider.getPageCount() + "");
//        response().setHeader(GlobalConfig.HEADER_PAGINATION_CURRENT_PAGE, provider.getPagination().getPage() + "");
//        response().setHeader(GlobalConfig.HEADER_PAGINATION_PER_PAGE, provider.getPagination().getPageSize() + "");
//        return ok(FieldsFilter.wrap(provider.getData())).as("application/json; charset=utf-8");
//    }

}
