package test.bwei.com.zhangjian20171008xiangji;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author ${张健}
 * @date 2017/9/28/22:09
 */

public class MyUtil {

    private static SharedPreferences sharedPreferences=null;

    public static SharedPreferences getSharedPreferencesInstance(Context context){

        if (sharedPreferences==null){

            sharedPreferences = context.getSharedPreferences("config", Context.MODE_APPEND);
        }

        return sharedPreferences;
    }
}
