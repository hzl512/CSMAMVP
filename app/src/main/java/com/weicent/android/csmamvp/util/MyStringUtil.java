package com.weicent.android.csmamvp.util;

import android.content.Context;
import android.text.Html;

import com.ab.util.AbStrUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/12/4.
 */
public class MyStringUtil {

    public static String createJsonString(Object object) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object); // 用Gson方式 把object 保存为 json字符串
        return jsonString;
    }

    public static boolean isNullToToast(Context context, String c, String t) {
        if (AbStrUtil.isEmpty(c))  {
            ToastUtil.showShort(context,t+"不为空");
            return  true;
        }
        return  false;
    }

    //单个jsonString数据上传
    static public String toJsonString(String[] strings1,String[] strings2){
        if (strings1.length == strings2.length&& strings1.length > 0){
            JsonObject jo = new JsonObject();
            for (int i = 0 ;i<strings1.length;i++){
                jo.addProperty(strings1[i], strings2[i]);
            }
            return jo.toString();
        }else {
            return "{}";
        }
    }

    //检查s是否是空字符串，并略缩该值"(" +s+ ")"
    static public String checkSIsEmptyToSlightlyShrink(String s,int len,String left,String right) {
        if (AbStrUtil.isEmpty(s) ) {
            return "";
        }else {
            if (len == 0)
                return left + s + right;
            else
                return left +( s.length() > len ? s.substring(0, len) + "..." : s) +right;
        }
    }

    //检查c或d是否是空字符串，并赋默认值""
    static public String checkCorDIsEmptyToDefault(String c,String d) {
        if (AbStrUtil.isEmpty(c)&&!AbStrUtil.isEmpty(d))
            return d;
        else if (!AbStrUtil.isEmpty(c)&&AbStrUtil.isEmpty(d))
            return c;
        else
            return "";
    }

    //剔除是空的fromHtml
    static public String turnStringfromHtmlExcludeEmpty(String s){
        if (AbStrUtil.isEmpty(s)){
            return "";
        }else {
            return  String.valueOf(Html.fromHtml(s));
        }
    }

    //检查c是否是空字符串，并赋默认值d
    static public String checkCIsEmptyToDefault(String c,String d) {
        if (AbStrUtil.isEmpty(c))
            return d;
        else
            return c;
    }

    //检查c是否是空字符串，并赋默认值d与追加值
    static public String checkCIsEmptyToDefaultAndApend(String c,String d,String a) {
        if (AbStrUtil.isEmpty(c))
            return d;
        else
            return a+c;
    }

    //转变电话格式
    static public String turnPhoneType(String tel){
        if (tel !=null && tel.length() > 0){
            String str = tel.replace(" ","");
            str=str.replace("-","");
            if (str.length() > 11){
                str = str.substring(str.length() - 11,str.length());
            }
//            Log.d("phoneListener",str);
            return str;
        }else {
            return "";
        }
    }

    public static Date turnStringToDate(String s){//字符串转化为时间
        Date date=null;
        try {
            date = (Date) new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(s);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public static Date turnStringToDate1(String s){//字符串转化为时间
        Date date=null;
        try {
            date = (Date) new SimpleDateFormat("yyyy/MM/dd").parse(s);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateTimeString(Date date){//获取时间的字符串年月日
        if (date != null){
            String s=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
            return s;
        }else {
            return "";
        }
    }

    public static String getDateTimeStringYMDHM(Date date){//获取时间的字符串年月日
        if (date != null){
            String s=new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date);
            return s;
        }else {
            return "";
        }
    }

    public static String getDateTimeStringYM(Date date){//获取时间的字符串年月日
        if (date != null){
            String s=new SimpleDateFormat("yyyy'年'MM'月'").format(date);
            return s;
        }else {
            return "";
        }
    }
    public static String getDateTimeStringYMDCN(Date date){//获取时间的字符串年月日
        if (date != null){
            String s=new SimpleDateFormat("yyyy'年'MM'月'dd'日'").format(date);
            return s;
        }else {
            return "";
        }
    }

    public static String getDateTimeStringYMD(Date date){//获取时间的字符串年月日
        if (date != null){
            String s=new SimpleDateFormat("yyyy-MM-dd").format(date);
            return s;
        }else {
            return "";
        }
    }

    public static String getDateTimeStringYMD1(Date date){//获取时间的字符串年月日
        if (date != null){
            String s=new SimpleDateFormat("yyyy/MM/dd").format(date);
            return s;
        }else {
            return "";
        }
    }

    public static String getDateTimeStringYM1(Date date){//获取时间的字符串年月
        if (date != null){
            String s=new SimpleDateFormat("yyyy-MM").format(date);
            return s;
        }else {
            return "";
        }
    }

}
