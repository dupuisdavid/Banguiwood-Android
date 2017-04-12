package com.afrikawood.banguiwood;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class AfrikawoodApplication extends MultiDexApplication {

	public AfrikawoodApplication() {}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		setupUniversalImageLoader();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
		MultiDex.install(this);
	}

	public void setupUniversalImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		ImageLoader.getInstance().init(config);
	}

}
