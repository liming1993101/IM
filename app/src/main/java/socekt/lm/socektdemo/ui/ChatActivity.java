package socekt.lm.socektdemo.ui;

/**
 * Created by Administrator on 2016/6/21.
 */

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import socekt.lm.socektdemo.R;
import socekt.lm.socektdemo.adapter.ChatMsgAdapter;
import com.way.chat.common.bean.TextMessage;
import socekt.lm.socektdemo.clients.Client;
import socekt.lm.socektdemo.clients.ClientOutputThread;
import socekt.lm.socektdemo.entity.ChatMsgEntity;
import socekt.lm.socektdemo.entity.RecentChatEntity;
import socekt.lm.socektdemo.utils.Constants;
import socekt.lm.socektdemo.utils.MessageDB;
import socekt.lm.socektdemo.utils.MyApplication;
import socekt.lm.socektdemo.utils.MyDate;
import socekt.lm.socektdemo.utils.RecentContactsDB;
import socekt.lm.socektdemo.utils.SharePreferenceUtil;
import socekt.lm.socektdemo.utils.UserDB;


public class ChatActivity extends MyActivity implements View.OnClickListener{

    private EditText mSendEt;
    private TextView backBt;
    private TextView chatName;
    private Button sendBt;
    private ListView messageList;
    private MessageDB messageDB;
    private MyApplication myApplication;
    private User user;
    private UserDB userDB;
    private ImageView mSendPhoto;
    private ImageView mSendCamera;
    private Uri photoUri;
    private SharePreferenceUtil util;
    private List<ChatMsgEntity>chatMsgList=new ArrayList<ChatMsgEntity>();
    private ChatMsgAdapter adapter;
    private String picPath;
    /** 使用照相机拍照获取图片 */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /** 使用相册中的图片 */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    /** 获取到的图片路径 */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myApplication= (MyApplication) getApplicationContext();
        messageDB=new MessageDB(this);
        userDB=new UserDB(this);
        user= (User) getIntent().getSerializableExtra("user");
        util=new SharePreferenceUtil(this, Constants.SAVE_USER);
        initView();
        initData();

    }

    private void initView() {
        mSendPhoto= (ImageView) findViewById(R.id.send_photo);
        mSendCamera= (ImageView) findViewById(R.id.send_camera);
        mSendEt= (EditText) findViewById(R.id.chat_editmessage);
        backBt= (TextView) findViewById(R.id.chat_back);
        chatName= (TextView) findViewById(R.id.chat_name);
        sendBt= (Button) findViewById(R.id.chat_send);
        messageList= (ListView) findViewById(R.id.chat_listview);
        sendBt.setOnClickListener(this);
        mSendPhoto.setOnClickListener(this);
        mSendCamera.setOnClickListener(this);

    }

    public void initData() {
        chatName.setText(util.getName());
      List<ChatMsgEntity>chat=messageDB.getMsg(user.getId());
        if (chat.size() > 0) {
            for (ChatMsgEntity entity : chat) {
                if (entity.getName().equals("")) {
                    entity.setName(user.getName());
                }
                if (entity.getImg() < 0) {
                    entity.setImg(user.getImg());
                }
                chatMsgList.add(entity);
            }
            Collections.reverse(chatMsgList);
        }

        adapter=new ChatMsgAdapter(this,chatMsgList);
        messageList.setAdapter(adapter);
        messageList.setSelection(chatMsgList.size()-1);
    }
    @Override
    public void getMessage(TranObject msg) {

        switch (msg.getType())
        {
            case MESSAGE:
                TextMessage tm= (TextMessage) msg.getObject();
                String message=tm.getMessage();
                ChatMsgEntity entity=new ChatMsgEntity(user.getName(),
                        MyDate.getDateEN(), message, user.getImg(), true);
                if (msg.getFromUser()==user.getId()||msg.getFromUser()==0)
                {
                    messageDB.savaMsg(user.getId(),entity);
                    chatMsgList.add(entity);
                    adapter.notifyDataSetChanged();
                    messageList.setSelection(chatMsgList.size()-1);
                    MediaPlayer.create(this, R.raw.msg).start();
                }
                else
                {
                    messageDB.savaMsg(msg.getFromUser(),entity);
                    MediaPlayer.create(this, R.raw.msg).start();
                }
                TextMessage tMessage= (TextMessage) msg.getObject();
                String message1=tMessage.getMessage();
                ChatMsgEntity entity3=new ChatMsgEntity("", MyDate.getDateEN()
                        ,message,-1,true);//收到的消息
                messageDB.savaMsg(msg.getFromUser(),entity3);
                User user=userDB.selectInfo(msg.getFromUser());
                RecentChatEntity entity1=new RecentChatEntity(msg.getFromUser()
                        ,user.getImg(),0,user.getName(),MyDate.getDate(),message1);
                MyApplication.getmRecentAdapter().remove(entity1);// 先移除该对象，目的是添加到首部
                MyApplication.getmRecentList().addFirst(entity1);// 再添加到首部
                MyApplication.getmRecentAdapter().notifyDataSetChanged();
                RecentContactsDB recentContactsDB=new RecentContactsDB(ChatActivity.this);
                recentContactsDB.savaRctContacts(entity1);
                recentContactsDB.colse();
                break;
            case LOGIN:
                User loginUser= (User) msg.getObject();
                Toast.makeText(ChatActivity.this,"你的好友"+loginUser.getId()+"上线了",Toast.LENGTH_SHORT).show();
                break;
            case LOGOUT:
                User login0utUser= (User) msg.getObject();
                Toast.makeText(ChatActivity.this,"你的好友"+login0utUser.getId()+"下线了",Toast.LENGTH_SHORT).show();
                break;
                default:
                    break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.chat_send:
                sendMessage(1);
                break;
            case R.id.send_photo:
                pickPhoto();
                break;
            case R.id.send_camera:
                takePhoto();
                break;

        }
    }

    /*
    * 向服务器发送消息
     */
private void  sendMessage(int type) {

        String sendContent=mSendEt.getText().toString();
        ChatMsgEntity entity=new ChatMsgEntity();
        entity.setDate(MyDate.getDateEN());
        entity.setName(util.getName());
        entity.setImg(util.getImg());
        entity.setMsgType(false);
    if (type==1)
    {
        entity.setMessage(sendContent);
    }
    else
    {
        entity.setMessage("图片");
    }
        messageDB.savaMsg(user.getId(),entity);
        chatMsgList.add(entity);
        adapter.notifyDataSetChanged();
        mSendEt.setText("");
        messageList.setSelection(chatMsgList.size()-1);
        MyApplication application= (MyApplication) this.getApplicationContext();
        Client client=application.getClient();
        ClientOutputThread out =client.getClientOutputThread();
        if (out!=null)
        {
            TranObject<TextMessage>o;
            TextMessage message=new TextMessage();

            if (type==1)
            {
                o=new TranObject<TextMessage>(TranObjectType.MESSAGE);
                message.setMessage(sendContent);
            }
            else
            {
                o=new TranObject<TextMessage>(TranObjectType.IMG);
                File file=new File(picPath);
                message.setImg(file);
            }
            o.setObject(message);
            o.setFromUser(Integer.parseInt(util.getId()));
            o.setToUser(user.getId());
            out.setMsg(o);
        }
        RecentChatEntity entity1=new RecentChatEntity(user.getId()
        ,user.getImg(), 0, user.getName(), MyDate.getDate(),
               sendContent);
        application.getmRecentList().remove(entity1);
        application.getmRecentList().addFirst(entity1);
        application.getmRecentAdapter().notifyDataSetChanged();


    }



    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }
/*
  * 重相册中选取照片
 */
    private void pickPhoto() {
        Intent intent = new Intent();
        // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCELED){
            return;
        }

        // 可以使用同一个方法，这里分开写为了防止以后扩展不同的需求
        switch (requestCode) {
            case SELECT_PIC_BY_PICK_PHOTO:// 如果是直接从相册获取
                doPhoto(requestCode, data);
                break;
            case SELECT_PIC_BY_TACK_PHOTO:// 如果是调用相机拍照时
                doPhoto(requestCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void doPhoto(int requestCode, Intent data) {

        // 从相册取图片，有些手机有异常情况，请注意
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            if (data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri = data.getData();
            if (photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String[] pojo = { MediaStore.MediaColumns.DATA };
        // The method managedQuery() from the type Activity is deprecated
        //Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
        Cursor cursor = this.getContentResolver().query(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);

            // 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
            if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                cursor.close();
            }
        }

        // 如果图片符合要求将其上传到服务器
        if (picPath != null && (	picPath.endsWith(".png") ||
                picPath.endsWith(".PNG") ||
                picPath.endsWith(".jpg") ||
                picPath.endsWith(".JPG"))) {
            BitmapFactory.Options option = new BitmapFactory.Options();
            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
            option.inSampleSize = 1;
            // 根据图片的SDCard路径读出Bitmap
            Bitmap bm = BitmapFactory.decodeFile(picPath, option);
            // 显示在图片控件上
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
        sendMessage(2);
    }
}
