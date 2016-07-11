package socekt.lm.socektdemo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import socekt.lm.socektdemo.R;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.bean.User;
import socekt.lm.socektdemo.clients.Client;
import socekt.lm.socektdemo.clients.ClientOutputThread;
import socekt.lm.socektdemo.utils.Encode;
import socekt.lm.socektdemo.utils.MyApplication;

public class RegitsterActivity extends MyActivity {

    private Button mRegister;
    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;
    private ProgressBar mProgressBar;
    private MyApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitster);
        application = (MyApplication) this.getApplicationContext();
        initView();
        initListener();
    }

    private void initListener() {
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void initView() {
        mRegister= (Button) findViewById(R.id.register_bt);
        mEmail= (EditText) findViewById(R.id.email);
        mName= (EditText) findViewById(R.id.name_1);
        mPassword= (EditText) findViewById(R.id.password);
        mProgressBar= (ProgressBar) findViewById(R.id.register_pager);
    }
    private void login()
    {
        String email=mEmail.getText().toString();
        String name=mName.getText().toString();
        String password=mPassword.getText().toString();
        mProgressBar.setVisibility(View.VISIBLE);
        if (email.equals("") || name.equals("") || password.equals(""))
        {
            Toast.makeText(RegitsterActivity.this,"你填写的某些信息为空",Toast.LENGTH_LONG).show();
        }
        else {

            if (application.isClientStart())
            {
                Client client = application.getClient();
//					Client client = GetMsgService.client;
                ClientOutputThread out = client.getClientOutputThread();
                TranObject<User> o = new TranObject<User>(TranObjectType.REGISTER);
                User u = new User();
                u.setEmail(email);
                u.setName(name);
                u.setPassword(Encode.getEncode("MD5", password));
                o.setObject(u);
                out.setMsg(o);
            }
            else {
                Toast.makeText(RegitsterActivity.this,"服务器抽风了！！！",Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getMessage(TranObject msg) {
        switch (msg.getType()) {
            case REGISTER:
                User u = (User) msg.getObject();
                int id = u.getId();
                if (id > 0) {

                    Toast.makeText(RegitsterActivity.this,"注册成功QQ"+id,Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.GONE);
                    finish();
                } else {
                    Toast.makeText(RegitsterActivity.this,"没有账号返回！",Toast.LENGTH_LONG).show();
                }
                break;

            default:
                mProgressBar.setVisibility(View.GONE);
                break;
        }
    }
}
