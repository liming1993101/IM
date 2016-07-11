package socekt.lm.socektdemo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import socekt.lm.socektdemo.entity.RecentChatEntity;

/**
 * Created by Administrator on 2016/6/23.
 */
public class RecentContactsDB {
    private SQLiteDatabase db;
    private String _id;
    public RecentContactsDB(Context context)
    {
        db = context.openOrCreateDatabase(Constants.DBNAME,
                Context.MODE_PRIVATE, null);
        _id=new SharePreferenceUtil(context,Constants.SAVE_USER).getId();
    }
    public void savaRctContacts(RecentChatEntity entity)
    {

        db.execSQL("CREATE table IF NOT EXISTS _chat"
                +_id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id TEXT, img TEXT,count TEXT,name TEXT,time TEXT,msg TEXT)");

        if (querryRctContacts(entity.getId()))
        {
            deleteRctContacts(entity.getId());
        }
        db.execSQL(
                "insert into _chat" + _id
                        +" (id,img,count,name,time,msg) values(?,?,?,?,?,?)",
                new Object[] { entity.getId(), entity.getImg(),
                        entity.getCount(), entity.getName(), entity.getTime(),entity.getMsg() });
    }
    public LinkedList<RecentChatEntity> getRctContacts()
    {
        LinkedList<RecentChatEntity>list=new LinkedList<RecentChatEntity>();
        db.execSQL("CREATE table IF NOT EXISTS _chat"
                +_id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,id TEXT, img TEXT,count TEXT,name TEXT,time TEXT,msg TEXT)");

        Cursor c = db.rawQuery("select * from _chat"+_id, null);
        while (c.moveToNext())
        {
            int id=c.getInt(c.getColumnIndex("id"));
            String name = c.getString(c.getColumnIndex("name"));
            int img = c.getInt(c.getColumnIndex("img"));
            String time = c.getString(c.getColumnIndex("time"));
            int count = c.getInt(c.getColumnIndex("count"));
            String msg = c.getString(c.getColumnIndex("msg"));
            RecentChatEntity recentChatEntity=new RecentChatEntity(id,img,count,name,time,msg);
            list.add(recentChatEntity);
        }
        c.close();
        Collections.reverse(list);
        return list;
    }
    public boolean querryRctContacts(int id)
    {
        Cursor c = db.rawQuery("select * from _chat"+_id+" where id="+id, null);

        if (c.moveToNext())
        {
            c.close();
            return true;
        }
        else
        {
            c.close();
            return false;
        }

    }

    public void deleteRctContacts(int id)
    {
        db.execSQL("DELETE FROM _chat"+_id+" WHERE id="+id);
    }
    public void colse()
    {
        db.close();
    }
}


