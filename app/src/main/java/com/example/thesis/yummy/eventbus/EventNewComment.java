package com.example.thesis.yummy.eventbus;

import com.example.thesis.yummy.restful.model.Comment;

public class EventNewComment {
    public Comment mComment;
    public String mDataNotification;

    public EventNewComment(Comment comment, String dataNotification) {
        mComment = comment;
        mDataNotification = dataNotification;
    }
}
