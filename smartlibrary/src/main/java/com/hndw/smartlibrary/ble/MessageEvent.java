package com.hndw.smartlibrary.ble;

public class MessageEvent {
    private String messageKey;
    private Object object;

    public MessageEvent(String key, Object value) {
        this.messageKey = key;
        object = value;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object getObject() {
        return object;
    }
}
