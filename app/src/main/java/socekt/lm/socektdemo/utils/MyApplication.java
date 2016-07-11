package socekt.lm.socektdemo.utils;

/**
 * Created by Administrator on 2016/4/26.
 */

import java.util.LinkedList;
import java.util.logging.Logger;


import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import socekt.lm.socektdemo.adapter.RecentChatAdapter;
import socekt.lm.socektdemo.clients.Client;
import socekt.lm.socektdemo.entity.RecentChatEntity;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks{
    private Client client;// 客户端
    private boolean isClientStart;// 客户端连接是否启动
    private NotificationManager mNotificationManager;
    private int newMsgNum = 0;// 后台运行的消息
    private static LinkedList<RecentChatEntity> mRecentList=new LinkedList<RecentChatEntity>();;
    private static RecentChatAdapter mRecentAdapter;
    private int recentNum = 0;
    private int activityCount=0;
    private SharePreferenceUtil util;

    @Override
    public void onCreate() {
        util= new SharePreferenceUtil(this,
                Constants.SAVE_USER);
        registerActivityLifecycleCallbacks(this);
        System.out.println(util.getIp() + " " + util.getPort());
        client = new Client(util.getIp(), util.getPort());// 从配置文件中读ip和地址'
        client.start(this);


//        RecentContactsDB recentContactsDB=new RecentContactsDB(this);
//        mRecentList=recentContactsDB.getRctContacts();
//        recentContactsDB.colse();

        super.onCreate();
    }

    public Client getClient() {
        return client;
    }

    public boolean isClientStart() {
        return isClientStart;
    }

    public void setClientStart(boolean isClientStart) {
        this.isClientStart = isClientStart;
    }

    public NotificationManager getmNotificationManager() {
        return mNotificationManager;
    }

    public void setmNotificationManager(NotificationManager mNotificationManager) {
        this.mNotificationManager = mNotificationManager;
    }

    public int getNewMsgNum() {
        return newMsgNum;
    }

    public void setNewMsgNum(int newMsgNum) {
        this.newMsgNum = newMsgNum;
    }

    public static LinkedList<RecentChatEntity> getmRecentList() {
        return mRecentList;
    }

    public void setmRecentList(LinkedList<RecentChatEntity> mRecentList) {
        this.mRecentList = mRecentList;
        mRecentAdapter = new RecentChatAdapter(getApplicationContext(),
                mRecentList);
    }

    public static RecentChatAdapter getmRecentAdapter() {
        return mRecentAdapter;
    }

    public void setmRecentAdapter(RecentChatAdapter mRecentAdapter) {
        this.mRecentAdapter = mRecentAdapter;
    }

    public int getRecentNum() {
        return recentNum;
    }

    public void setRecentNum(int recentNum) {
        this.recentNum = recentNum;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

        activityCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        activityCount--;
        if (activityCount==0)
        {
            Intent i = new Intent();
            i.setAction(Constants.BACKKEY_ACTION);
            sendBroadcast(i);
            util.setIsStart(true);// 设置后台运行标志，正在运行
        }
        else
        {
            util.setIsStart(false);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
//@Override
//public void onActivityStopped(Activity activity) {
//    activityCount--;
//    Log.e(activity+"", "onActivityStopped"+activityCount);
//}
//
//    @Override
//    public void onActivityStarted(Activity activity) {
//        activityCount++;
//        Log.e(activity+"", "onActivityStarted"+activityCount);
//    }
//
//    @Override
//    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//        Log.e(activity+"", "onActivitySaveInstanceState");
//    }
//
//    @Override
//    public void onActivityResumed(Activity activity) {
//        Log.e(activity+"", "onActivityResumed");
//    }
//
//    @Override
//    public void onActivityPaused(Activity activity) {
//        Log.e(activity+"", "onActivityPaused");
//    }
//
//    @Override
//    public void onActivityDestroyed(Activity activity) {
//        Log.e(activity+"", "onActivityDestroyed");
//    }
//
//    @Override
//    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        Log.e(activity+"", "onActivityCreated");
//    }
}
