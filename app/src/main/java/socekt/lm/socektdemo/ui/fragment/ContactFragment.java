package socekt.lm.socektdemo.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.way.chat.common.bean.FindContacts;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import socekt.lm.socektdemo.R;
import socekt.lm.socektdemo.adapter.ContactAdapter;
import socekt.lm.socektdemo.clients.Client;
import socekt.lm.socektdemo.clients.ClientOutputThread;
import socekt.lm.socektdemo.utils.Constants;
import socekt.lm.socektdemo.utils.Encode;
import socekt.lm.socektdemo.utils.GroupFriend;
import socekt.lm.socektdemo.utils.MessageDB;
import socekt.lm.socektdemo.utils.MyApplication;
import socekt.lm.socektdemo.utils.SharePreferenceUtil;
import socekt.lm.socektdemo.utils.UserDB;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ContactFragment extends Fragment {
    private ExpandableListView mExpandableListView;
    private List<GroupFriend> group;
    private TranObject msg;
    private  UserDB userDB;
    private EditText qqEt;
    private ImageView findBt;
    private MessageDB messageDB;
    private String groupName[]={"我的好友","我的家人","我的同事"};
    private SharePreferenceUtil util;
    private List<User>listUser;
    private ContactAdapter adapter;
    private MyApplication myApplication;
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.contact_fragment,null);
        myApplication = (MyApplication) getActivity().getApplicationContext();
        util=new SharePreferenceUtil(getActivity(),Constants.SAVE_USER);
        initView(view);
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        findBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myApplication.isClientStart())
                {
                    Client client=myApplication.getClient();
                    ClientOutputThread out=client.getClientOutputThread();
                    TranObject<FindContacts> o = new TranObject<FindContacts>(TranObjectType.FIND);
                    FindContacts u = new FindContacts();
                    u.setMyID(Integer.parseInt(util.getId()));
                    u.setToID(Integer.parseInt(qqEt.getText().toString()));
                    o.setObject(u);
                    out.setMsg(o);
                    qqEt.setText("");
                }
                else
                {
                    Toast.makeText(getActivity(),"服务器抽风了！",Toast.LENGTH_SHORT);
                }
            }
        });
    }


    private void initView(View view) {
       mExpandableListView= (ExpandableListView) view.findViewById(R.id.contact_lv);
        qqEt= (EditText) view.findViewById(R.id.contact_et);
        findBt= (ImageView) view.findViewById(R.id.find_contacts);

    }
    private void initData() {
        userDB = new UserDB(getActivity());// 本地用户数据库
        messageDB = new MessageDB(getActivity());// 本地消息数据库
        util = new SharePreferenceUtil(getActivity(), Constants.SAVE_USER);
        msg= (TranObject) getActivity().getIntent().getSerializableExtra(Constants.MSGKEY);
        if (msg!=null)
        {
            listUser= (List<User>) msg.getObject();
            userDB.updateUser(listUser);
        }
        else
        {
            listUser= userDB.getUser();
        }
       group=new ArrayList<GroupFriend>();
        for (int i=0;i<groupName.length;i++)
        {
            List<User>list=new ArrayList<User>();
            GroupFriend groupFriend=new GroupFriend(groupName[i],list);
            for(User user:listUser)
            {
                if (user.getGroup()==i)
                {
                   list.add(user);
                }

            }
            group.add(groupFriend);

        }
        adapter=new ContactAdapter(getActivity(), group);
        mExpandableListView.setAdapter(adapter);
    }

}
