package socekt.lm.socektdemo.ui;

import android.animation.Animator;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.way.chat.common.bean.FindContacts;
import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;


import java.util.ArrayList;
import java.util.List;

import socekt.lm.socektdemo.R;
import socekt.lm.socektdemo.adapter.DemoViewPagerAdapter;
import socekt.lm.socektdemo.entity.ChatMsgEntity;
import socekt.lm.socektdemo.entity.RecentChatEntity;
import socekt.lm.socektdemo.service.GetMsgService;
import socekt.lm.socektdemo.ui.fragment.ContactFragment;
import socekt.lm.socektdemo.ui.fragment.MessageFragment;
import socekt.lm.socektdemo.ui.fragment.PersonalFragment;
import socekt.lm.socektdemo.utils.Constants;
import socekt.lm.socektdemo.utils.MessageDB;
import socekt.lm.socektdemo.utils.MyApplication;
import socekt.lm.socektdemo.utils.MyDate;
import socekt.lm.socektdemo.utils.NetUtil;
import socekt.lm.socektdemo.utils.RecentContactsDB;
import socekt.lm.socektdemo.utils.SharePreferenceUtil;
import socekt.lm.socektdemo.utils.UserDB;


public class HomeActivity extends MyActivity
{

    private AHBottomNavigation mNavigation;
    private AHBottomNavigationViewPager mViewPager;
    private Fragment currentFragment;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private List<Fragment> fragemnts=new ArrayList<Fragment>();
    private DemoViewPagerAdapter adapter;
    private int toast=0;
    private UserDB userDB;
    private MessageDB messageDB;
    private MyApplication myApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        myApplication= (MyApplication) getApplicationContext();
        toast = myApplication.getRecentNum();// 从新获取一下全局变量
        NotificationManager manager = myApplication.getmNotificationManager();
        if (manager != null) {
            manager.cancel(Constants.NOTIFY_ID);
//            myApplication.setNewMsgNum(0);// 把消息数目置0
//            myApplication.getmRecentAdapter().notifyDataSetChanged();
        }
        if (new NetUtil().isNetworkAvailable(HomeActivity.this)) {
            Intent service = new Intent(this, GetMsgService.class);
            startService(service);
        } else {
            Toast.makeText(this,"没有可用的网络",Toast.LENGTH_LONG);
        }
        messageDB=new MessageDB(this);
        userDB=new UserDB(this);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {

        mNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        mViewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("消息", R.mipmap.info_news, R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("联系人", R.mipmap.man_1, R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("设置", R.mipmap.setup_ac_icon2, R.color.white);
        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        fragemnts.add(new MessageFragment());
        fragemnts.add(new ContactFragment());
        fragemnts.add(new PersonalFragment());
        mNavigation.addItems(bottomNavigationItems);
        mNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        mNavigation.setInactiveColor(Color.parseColor("#747474"));
        mViewPager.setOffscreenPageLimit(4);
        adapter = new DemoViewPagerAdapter(getSupportFragmentManager(), fragemnts);
        mViewPager.setAdapter(adapter);
        currentFragment = adapter.getCurrentFragment();
        mNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {

                currentFragment = adapter.getCurrentFragment();

                mViewPager.setCurrentItem(position, false);


            }
        });
        RecentContactsDB recentContactsDB=new RecentContactsDB(this);
        myApplication.setmRecentList(recentContactsDB.getRctContacts());
        myApplication.getmRecentAdapter().notifyDataSetChanged();
        recentContactsDB.colse();
    }
    @Override
    public void getMessage(TranObject msg) {

        switch (msg.getType())
        {
            case MESSAGE:
                toast++;
                myApplication.setNewMsgNum(toast);
                TextMessage tMessage= (TextMessage) msg.getObject();
                String message=tMessage.getMessage();
                ChatMsgEntity entity=new ChatMsgEntity("", MyDate.getDateEN()
                        ,message,-1,true);//收到的消息
                messageDB.savaMsg(msg.getFromUser(),entity);
                MediaPlayer.create(HomeActivity.this,R.raw.msg).start();
                User user=userDB.selectInfo(msg.getFromUser());
                RecentChatEntity entity1=new RecentChatEntity(msg.getFromUser()
                        ,user.getImg(),toast,user.getName(),MyDate.getDate(),message);
                MyApplication.getmRecentAdapter().remove(entity1);// 先移除该对象，目的是添加到首部
                MyApplication.getmRecentList().addFirst(entity1);// 再添加到首部
                MyApplication.getmRecentAdapter().notifyDataSetChanged();
                RecentContactsDB recentContactsDB=new RecentContactsDB(HomeActivity.this);
                recentContactsDB.savaRctContacts(entity1);
                recentContactsDB.colse();
                break;
            case FIND:
                FindContacts object= (FindContacts) msg.getObject();
                if (object.isState())
                {
                    Toast.makeText(HomeActivity.this,"添加好友成功！",Toast.LENGTH_SHORT);
                }
                else {
                    Toast.makeText(HomeActivity.this,"出现不可预知的错误！",Toast.LENGTH_SHORT);
                }

                break;




        }
    }
}
