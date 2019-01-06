package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Conversation extends Base {
    @Json(name = "_id") public int mId;
    @Json(name = "lastMessage") public Message mLastMessage;
    @Json(name = "users") public List<User> mUser;
    @Json(name = "lastDate") public Date mLastDate;
}
