package socekt.lm.socektdemo.ui.fragment;


import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;

import socekt.lm.socektdemo.R;
import socekt.lm.socektdemo.adapter.MeassageAdapter;
import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.tran.bean.TranObjectType;

import socekt.lm.socektdemo.clients.Client;
import socekt.lm.socektdemo.clients.ClientOutputThread;
import socekt.lm.socektdemo.entity.ChatMsgEntity;
import socekt.lm.socektdemo.entity.RecentChatEntity;
import socekt.lm.socektdemo.ui.LoginActivity;
import socekt.lm.socektdemo.utils.Constants;
import socekt.lm.socektdemo.utils.Encode;
import socekt.lm.socektdemo.utils.MessageDB;
import socekt.lm.socektdemo.utils.MyApplication;
import socekt.lm.socektdemo.utils.MyDate;
import socekt.lm.socektdemo.utils.RecentContactsDB;
import socekt.lm.socektdemo.utils.SharePreferenceUtil;
import socekt.lm.socektdemo.utils.UserDB;

/**
 * Created by Administrator on 2016/4/27.
 */
public class MessageFragment extends BaseFragment {
    private ListView mRecentListView;
    private MyApplication myApplication;
    private MessageDB messageDB;
    private UserDB userDB;
    private int toast=0;
    private SharePreferenceUtil util;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.message_fragment,null);
        util=new SharePreferenceUtil(getActivity(),Constants.SAVE_USER);
        initView(view);
        mRecentListView.setAdapter(myApplication.getmRecentAdapter());
        messageDB=new MessageDB(getActivity());
        userDB=new UserDB(getActivity());
        notMessage();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myApplication = (MyApplication) getActivity().getApplicationContext();


    }

    private void initView(View view) {
        mRecentListView= (ListView) view.findViewById(R.id.message_rv);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        adapter=new MeassageAdapter(getActivity());
////设置布局管理器
//        mRecyclerView.setLayoutManager(layoutManager);
////设置为垂直布局，这也是默认的
//        layoutManager.setOrientation(OrientationHelper.VERTICAL);
////设置Adapter
////        mRecyclerView.setAdapter( recycleAdapter);
////        //设置分隔线
////        mRecyclerView.addItemDecoration( new DividerGridItemDecoration(this ));
////设置增加或删除条目的动画
//        mRecyclerView.setItemAnimator( new DefaultItemAnimator());


    }

    private void notMessage() {


        if (myApplication.isClientStart())
        {
            Client client=myApplication.getClient();
            ClientOutputThread out=client.getClientOutputThread();
            TranObject<User> o = new TranObject<User>(TranObjectType.NOTMESSAGE);
            User u = new User();
            u.setId(Integer.parseInt(util.getId()));
            o.setObject(u);
            out.setMsg(o);
        }
        else
        {
            Toast.makeText(getActivity(),"服务器抽风了！",Toast.LENGTH_SHORT);
        }
    }
    @Override
    public void getMessage(TranObject msg) {


    }
}
