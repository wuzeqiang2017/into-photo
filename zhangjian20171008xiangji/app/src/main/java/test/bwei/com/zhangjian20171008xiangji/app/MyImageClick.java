package test.bwei.com.zhangjian20171008xiangji.app;

import android.app.Application;

import com.bwei.imageloaderlibrary.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author ${张健}
 * @date 2017/9/25/20:53
 */

public class MyImageClick extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration configuration = ImageLoaderUtils.getConfiguration(this);
        ImageLoader.getInstance().init(configuration);
    }


}
