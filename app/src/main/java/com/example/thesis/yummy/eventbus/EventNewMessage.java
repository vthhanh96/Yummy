package com.example.thesis.yummy.eventbus;

import com.example.thesis.yummy.restful.model.Message;

public class EventNewMessage {
    public Message mMessage;

    public EventNewMessage(Message message) {
        mMessage = message;
    }
}
