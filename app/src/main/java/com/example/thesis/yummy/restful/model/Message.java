package com.example.thesis.yummy.restful.model;

import android.support.annotation.Nullable;

import com.example.thesis.yummy.utils.StringUtils;
import com.squareup.moshi.Json;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

public class Message extends Base implements IMessage, MessageContentType.Image{
    @Json(name = "_id") public int mId;
    @Json(name = "message") public String mMessage;
    @Json(name = "to") public User mToUser;
    @Json(name = "from") public User mFromUser;
    @Json(name = "date") public Date mDate;
    @Json(name = "is_seen") public Boolean mIsSeen;

    public Message() {
    }

    public Message(int id, String message, User toUser, User fromUser, Date date) {
        mId = id;
        mMessage = message;
        mToUser = toUser;
        mFromUser = fromUser;
        mDate = date;
    }

    @Override
    public String getId() {
        return String.valueOf(mId);
    }

    @Override
    public String getText() {
        return mMessage;
    }

    @Override
    public IUser getUser() {
        return mFromUser;
    }

    @Override
    public Date getCreatedAt() {
        return mDate;
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return StringUtils.isImage(mMessage) ? mMessage : null;
    }
}
