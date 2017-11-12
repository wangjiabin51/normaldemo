package com.youmi.android.addemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.youmi.android.nm.bn.BannerManager;
import net.youmi.android.nm.bn.BannerViewListener;
import net.youmi.android.nm.cm.ErrorCode;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.vdo.VideoAdManager;
import net.youmi.android.nm.vdo.VideoAdRequestListener;

/**
 * 主窗口
 *
 * @author Alian Lee
 * @since 2016-11-25
 */
public class MainActivity extends BaseActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 设置应用版本信息
		setupAppVersionInfo();
		// 初始化视图
		initView();
		// 预加载数据
		preloadData();
		//设置广告条
		setupBannerAd();
	}
	
	/**
	 * 设置应用版本信息
	 */
	private void setupAppVersionInfo() {
		TextView textVersionInfo = (TextView) findViewById(R.id.tv_main_version_info);
		if (textVersionInfo != null) {
			textVersionInfo.append(getAppVersionName());
		}
	}
	
	/**
	 * 初始化视图
	 */
	private void initView() {
		findViewById(R.id.btn_main_show_spot_ad).setOnClickListener(this);
		findViewById(R.id.btn_main_show_slideable_spot_ad).setOnClickListener(this);
		findViewById(R.id.btn_main_show_native_spot_ad).setOnClickListener(this);
		findViewById(R.id.btn_main_show_video_ad).setOnClickListener(this);
		findViewById(R.id.btn_main_show_native_video_ad).setOnClickListener(this);
	}
	
	/**
	 * 预加载数据
	 */
	private void preloadData() {
		// 设置服务器回调 userId，一定要在请求广告之前设置，否则无效
		VideoAdManager.getInstance(mContext).setUserId("");
		// 请求视频广告
		// 注意：不必每次展示视频广告前都请求，只需在应用启动时请求一次
		VideoAdManager.getInstance(mContext)
		                       .requestVideoAd(mContext, new VideoAdRequestListener() {
			
			                       @Override
			                       public void onRequestSuccess() {
				                       logInfo("请求视频广告成功");
			                       }
			
			                       @Override
			                       public void onRequestFailed(int errorCode) {
				                       logError("请求视频广告失败，errorCode: %s", errorCode);
				                       switch (errorCode) {
				                       case ErrorCode.NON_NETWORK:
					                       showShortToast("网络异常");
					                       break;
				                       case ErrorCode.NON_AD:
					                       showShortToast("暂无视频广告");
					                       break;
				                       default:
					                       showShortToast("请稍后再试");
					                       break;
				                       }
			                       }
		                       });
	}
	
	/**
	 * 设置广告条广告
	 */
	private void setupBannerAd() {
		//		/**
		//		 * 普通布局
		//		 */
		//		// 获取广告条
		//		View bannerView = BannerManager.getInstance(mContext)
		//		                                        .getBannerView(mContext, new BannerViewListener() {
		//			                                        @Override
		//			                                        public void onRequestSuccess() {
		//				                                        logInfo("请求广告条成功");
		//			                                        }
		//
		//			                                        @Override
		//			                                        public void onSwitchBanner() {
		//				                                        logDebug("广告条切换");
		//			                                        }
		//
		//			                                        @Override
		//			                                        public void onRequestFailed() {
		//				                                        logError("请求广告条失败");
		//			                                        }
		//		                                        });
		//		// 实例化广告条容器
		//		LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.ll_main_banner);
		//		// 添加广告条到容器中
		//		bannerLayout.addView(bannerView);
		
		/**
		 * 悬浮布局
		 */
		// 实例化LayoutParams
		FrameLayout.LayoutParams layoutParams =
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置广告条的悬浮位置，这里示例为右下角
		layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		// 获取广告条
		final View bannerView = BannerManager.getInstance(mContext)
		                                              .getBannerView(mContext, new BannerViewListener() {
			
			                                              @Override
			                                              public void onRequestSuccess() {
				                                              logInfo("请求广告条成功");
				
			                                              }
			
			                                              @Override
			                                              public void onSwitchBanner() {
				                                              logDebug("广告条切换");
			                                              }
			
			                                              @Override
			                                              public void onRequestFailed() {
				                                              logError("请求广告条失败");
			                                              }
		                                              });
		// 添加广告条到窗口中
		((Activity) mContext).addContentView(bannerView, layoutParams);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 展示广告条窗口的 onDestroy() 回调方法中调用
		BannerManager.getInstance(mContext).onDestroy();
		
		// 退出应用时调用，用于释放资源
		// 如果无法保证应用主界面的 onDestroy() 方法被执行到，请移动以下接口到应用的退出逻辑里面调用
		
		// 插屏广告（包括普通插屏广告、轮播插屏广告、原生插屏广告）
		SpotManager.getInstance(mContext).onAppExit();
		// 视频广告（包括普通视频广告、原生视频广告）
		VideoAdManager.getInstance(mContext).onAppExit();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 插屏广告
		case R.id.btn_main_show_spot_ad:
			startActivity(new Intent(mContext, SpotAdActivity.class));
			break;
		// 轮播插屏广告
		case R.id.btn_main_show_slideable_spot_ad:
			startActivity(new Intent(mContext, SlideableSpotAdActivity.class));
			break;
		// 原生插屏广告
		case R.id.btn_main_show_native_spot_ad:
			startActivity(new Intent(mContext, NativeSpotAdActivity.class));
			break;
		// 视屏广告
		case R.id.btn_main_show_video_ad:
			startActivity(new Intent(mContext, VideoAdActivity.class));
			break;
		// 原生视频广告
		case R.id.btn_main_show_native_video_ad:
			startActivity(new Intent(mContext, NativeVideoAdActivity.class));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取应用版本号
	 *
	 * @return 应用当前的版本号
	 */
	private String getAppVersionName() {
		try {
			PackageManager packageManager = getPackageManager();
			return packageManager.getPackageInfo(getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
	}
}