package com.way.chat.common.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/28.
 */
public class FindContacts implements Serializable {
    private static final long serialVersionUID = 1L;
    private int myID;

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getToID() {
        return toID;
    }

    public void setToID(int toID) {
        this.toID = toID;
    }

    private int toID;
    private boolean state;

}
