package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class Comment extends Base implements IMessage{
    @Json(name = "_id") public Integer mId;
    @Json(name = "creator") public User mCreator;
    @Json(name = "content") public String mContent;
    @Json(name = "created_date") public Date mCreatedDate;
    @Json(name = "modify_date") public Date mModifiedDate;
    @Json(name = "parentID") public Integer mParentID;

    public Comment() {
    }

    public Comment(Integer id) {
        mId = id;
    }

    public Comment(Integer id, User creator, String content, Date createdDate) {
        mId = id;
        mCreator = creator;
        mContent = content;
        mCreatedDate = createdDate;
    }

    @Override
    public String getId() {
        return String.valueOf(mId);
    }

    @Override
    public String getText() {
        return mContent;
    }

    @Override
    public IUser getUser() {
        return mCreator;
    }

    @Override
    public Date getCreatedAt() {
        return mCreatedDate;
    }
}
