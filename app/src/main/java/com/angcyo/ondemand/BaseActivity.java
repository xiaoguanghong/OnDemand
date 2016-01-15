package com.angcyo.ondemand;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.angcyo.ondemand.event.EventNoNet;
import com.angcyo.ondemand.util.PopupTipWindow;
import com.angcyo.ondemand.view.ProgressFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public abstract class BaseActivity extends AppCompatActivity {

    public static Handler handler;
    private ProgressFragment progressFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        initBefore();
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        initAfter();
        initEvent();
    }

    //初始化
    private void init() {
        handler = new StaticHandler(this);
    }


    /**
     * Init before _.
     */
    protected void initBefore() {

    }

    protected void initEvent() {

    }

    /**
     * Init view.
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * Init after.
     */
    protected abstract void initAfter();

    protected void launchActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    public void showDialogTip(String tip) {
        progressFragment = ProgressFragment.newInstance(tip);
        progressFragment.show(getSupportFragmentManager(), "dialog_tip");
    }

    public void hideDialogTip() {
        if (progressFragment != null) {
            progressFragment.dismiss();
            progressFragment = null;
        }
    }

    @TargetApi(19)
    protected void initWindow(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//导航栏
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setTintResource(color);//设置状态栏颜色
            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setNavigationBarTintResource(R.color.dark_green);//设置导航栏颜色
//            tintManager.setNavigationBarTintEnabled(false);
        }
    }

    @TargetApi(19)
    protected void setStateBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(color);//设置状态栏颜色
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    protected void handMessage(Message msg, int what, Object obj) {

    }

    public void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public void sendMessage(int what, Object obj) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    public void sendRunnable(Runnable runnable) {
        handler.post(runnable);
    }

    public void sendDelayRunnable(Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }


    public void removeCallbacks(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

    protected OdApplication getApp() {
        return ((OdApplication) getApplication());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void noNet(EventNoNet event) {
        hideDialogTip();
        PopupTipWindow.showTip(this, "请检查网络连接");
    }

    static class StaticHandler extends Handler {
        BaseActivity context;

        public StaticHandler(BaseActivity context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (context != null && msg != null) {
                context.handMessage(msg, msg.what, msg.obj);
            }
        }
    }
}
