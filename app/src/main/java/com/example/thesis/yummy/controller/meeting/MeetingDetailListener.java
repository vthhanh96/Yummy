package com.example.thesis.yummy.controller.meeting;

import com.example.thesis.yummy.restful.model.Comment;

public interface MeetingDetailListener {
    void onCreateComment(String content);
    void onUpdateComment(String content, Comment comment);
    void onDeleteComment(Comment comment);
}
