package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nookio on 15/5/13.
 */
public class DateUtil {

    public static Date addDate(Date date, Integer field,Integer addNum){
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, addNum);
        Long time =calendar.getTimeInMillis();
        return  new Date(time);
    }

    public static Date formate(String before) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(before);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换失败");
        }
        System.out.println(date);
        return date;
    }

    public static Date timeMillToDate(String before) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(before);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换失败");
        }
        System.out.println(date);
        return date;
    }

    public static String getNow() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String result = simpleDateFormat.format(new Date());
        return result;
    }

    public static Date getToday(Date before) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = null;//simpleDateFormat.format(before);
        Date date = null;
        try {
            dateStr = simpleDateFormat.format(before);
            date = simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("日期转换失败");
        }
        //System.out.println(date);
        return date;
    }
}
