package com.raptorsrepublic.myrrapp.rrapp1;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Zarar_Siddiqi on 6/5/2014.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Create global configuration and initialize ImageLoader with this configuration
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .diskCache(new UnlimitedDiscCache(cacheDir))
                .memoryCacheSize(2 * 1024 * 1024)

        .build();
        ImageLoader.getInstance().init(config);
    }
}
