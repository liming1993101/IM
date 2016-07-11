package socekt.lm.socektdemo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.way.chat.common.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16.
 */
public class FriendsDB {
    private  DBHelper dbHelper;
    public void FriendsDB(Context context)
    {
        dbHelper=new DBHelper(context);
    }
    public User selectInfo(int id)
    {
        User user=new User();
        SQLiteDatabase  sdb=dbHelper.getReadableDatabase();
        Cursor cursor=sdb.rawQuery("select * from user where id=?",new String[]{id+""});
        if (cursor.moveToFirst())
        {
            user.setImg(cursor.getInt(cursor.getColumnIndex("img")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
        }
        cursor.close();
        sdb.close();
        return user;

    }
    public void addUser(List<User>list)
    {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        for (User user:list)
        {
            db.execSQL("insert into user(id,name,img,isOnline,_group) values(?,?,?,?,?)"
                    ,new Object[]{user.getId(),user.getName(),user.getImg(),user.getIsOnline(),user.getGroup()});
        }
        db.close();

    }
    public List<User> getUserInfo()
    {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        List<User>list=new ArrayList<User>();
        Cursor c=db.rawQuery("select * from user",null);
        while (c.moveToNext()) {
            User u = new User();
            u.setId(c.getInt(c.getColumnIndex("id")));
            u.setName(c.getString(c.getColumnIndex("name")));
            u.setImg(c.getInt(c.getColumnIndex("img")));
            u.setIsOnline(c.getInt(c.getColumnIndex("isOnline")));
            u.setGroup(c.getInt(c.getColumnIndex("_group")));
            list.add(u);
        }
        c.close();
        db.close();
        return list;
    }
    public  void upUserInfo(List<User>list)
    {

    }
    public  void deleteUserInfo(int id)
    {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from user where id=?",new String[]{id+""});
        db.close();
    }
}
