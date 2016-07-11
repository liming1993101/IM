package socekt.lm.socektdemo.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;

import socekt.lm.socektdemo.R;
import com.way.chat.common.bean.TextMessage;
import socekt.lm.socektdemo.clients.Client;
import socekt.lm.socektdemo.clients.ClientInputThread;
import socekt.lm.socektdemo.clients.ClientOutputThread;
import socekt.lm.socektdemo.clients.MessageListener;
import socekt.lm.socektdemo.entity.ChatMsgEntity;
import socekt.lm.socektdemo.entity.RecentChatEntity;
import socekt.lm.socektdemo.ui.HomeActivity;
import socekt.lm.socektdemo.utils.Constants;
import socekt.lm.socektdemo.utils.MessageDB;
import socekt.lm.socektdemo.utils.MyApplication;
import socekt.lm.socektdemo.utils.MyDate;
import socekt.lm.socektdemo.utils.RecentContactsDB;
import socekt.lm.socektdemo.utils.SharePreferenceUtil;
import socekt.lm.socektdemo.utils.UserDB;

import static socekt.lm.socektdemo.R.*;

/**
 * Created by Administrator on 2016/6/16.
 */
public class GetMsgService extends Service {
    private static final int MSG=0x001;
    private MyApplication application;
    private Client client;
    private NotificationManager mNotificationManager;
    private  boolean isStart=false;
    private  Notification mNotification;
    private Context context=this;
    private SharePreferenceUtil util;
    private MessageDB messageDB;
    private UserDB userDB;

    private BroadcastReceiver backKeyReceiveer=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           setMsgNotification();
        }
    };

    private void setMsgNotification() {

        int icon= mipmap.info_news;
        CharSequence tickerText="";
        long when = System.currentTimeMillis();
        mNotification=new Notification(icon,tickerText,when);
        RemoteViews contentView=new RemoteViews(context.getPackageName(),
                layout.notify_view);

        contentView.setTextViewText(id.notify_name,util.getName());
        contentView.setTextViewText(id.notify_msg, "手机QQ正在后台运行");
        contentView.setTextViewText(id.notify_time, MyDate.getDate());
        mNotification.contentView=contentView;
        Intent intent=new Intent(this, HomeActivity.class);
        PendingIntent contentIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.contentIntent=contentIntent;
        mNotificationManager.notify(Constants.NOTIFY_ID,mNotification);

    }

    private Handler handler=new Handler()
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG:
                    int newMsgNum=application.getNewMsgNum();
                    newMsgNum++;
                    application.setNewMsgNum(newMsgNum);
                    TranObject<TextMessage> textObjiect= (TranObject<TextMessage>) msg
                            .getData().getSerializable("msg");
                    if (textObjiect!=null)
                    {
                        int form =textObjiect.getFromUser();
                        String content=textObjiect.getObject().getMessage();
                        ChatMsgEntity entity=new ChatMsgEntity("",MyDate.getDateEN(),content,-1,true);
                        messageDB.savaMsg(form,entity);
                        int icon= mipmap.info_news;
                        CharSequence tickerText = form + ":" + content;

                        Intent intent =new Intent(context,HomeActivity.class);
                        PendingIntent contentIntent=PendingIntent.getActivity(context,0,intent,0);
                        mNotification=new Notification.Builder(context)
                                .setContentTitle(form+"")
                                .setContentIntent(contentIntent)
                                .setWhen(System.currentTimeMillis())
                                .setAutoCancel(true)//设置可以清除
                                .setContentText("来自"+newMsgNum + "条新消息")
                                .setTicker(""+tickerText)
                                .setSmallIcon(R.mipmap.ic_nav_2_normal)
                                .setLargeIcon(BitmapFactory.decodeResource( context.getResources(), mipmap.app_icon))
                                .build();

//                                setLatestEventInfo(mContext, util.getName()
//                                        + " (" + newMsgNum + "条新消息)", content,
//                                contentIntent);
                        userDB=new UserDB(GetMsgService.this);
                        ChatMsgEntity entity5=new ChatMsgEntity("", MyDate.getDateEN()
                                ,content,-1,true);//收到的消息
                        messageDB.savaMsg(form,entity5);
                        User user=userDB.selectInfo(form);
                        RecentChatEntity entity1=new RecentChatEntity(form
                                ,user.getImg(),newMsgNum,user.getName(),MyDate.getDate(),content);
                        MyApplication.getmRecentAdapter().remove(entity1);// 先移除该对象，目的是添加到首部
                        MyApplication.getmRecentList().addFirst(entity1);// 再添加到首部
                        MyApplication.getmRecentAdapter().notifyDataSetChanged();
                        RecentContactsDB recentContactsDB=new RecentContactsDB(GetMsgService.this);
                        recentContactsDB.savaRctContacts(entity1);
                        recentContactsDB.colse();

                    }
                    mNotificationManager.notify(Constants.NOTIFY_ID, mNotification);
                    break;
            }
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        messageDB=new MessageDB(this);
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constants.BACKKEY_ACTION);
        registerReceiver(backKeyReceiveer,filter);
        mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        application= (MyApplication) this.getApplicationContext();
        client=application.getClient();
        application.setmNotificationManager(mNotificationManager);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        util=new SharePreferenceUtil(getApplicationContext(),Constants.SAVE_USER);
        isStart=application.isClientStart();
        if (isStart)
        {
            ClientInputThread in =client.getClientInputThread();
            in.setMessageListener(new MessageListener() {
                @Override
                public void Message(TranObject msg) {
                    if (util.getIsStart())
                    {
                        if (msg.getType()== TranObjectType.MESSAGE)
                        {
                            Message message = handler.obtainMessage();
                            message.what = MSG;
                            message.getData().putSerializable("msg", msg);
                            handler.sendMessage(message);
                        }
                    }
                    else
                    {
                        Intent broadCast=new Intent();
                        broadCast.setAction(Constants.ACTION);
                        broadCast.putExtra(Constants.MSGKEY,msg);
                        sendBroadcast(broadCast);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageDB!=null)
        {
            messageDB.close();
        }
        unregisterReceiver(backKeyReceiveer);
        mNotificationManager.cancel(Constants.NOTIFY_ID);
        if (isStart)
        {
            ClientOutputThread out =client.getClientOutputThread();
            TranObject<User> o=new TranObject<>(TranObjectType.LOGOUT);
            User u=new User();
            u.setId(Integer.parseInt(util.getId()));
            o.setObject(u);
            out.setMsg(o);
            out.setStart(false);
            client.getClientInputThread().setStart(false);

        }
    }
}
