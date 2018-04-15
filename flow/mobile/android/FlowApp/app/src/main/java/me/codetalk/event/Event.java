package me.codetalk.event;

/**
 * Created by guobxu on 2017/12/27.
 */

public final class Event {

    private String name;
    private Integer errKey;
    private String errMsg;

    private Object extra1;
    private Object extra2;
    private Object extra3;
    private Object extra4;
    private Object extra5;

    public static Builder builder() {
        return new Builder();
    }

    public String name() {
        return name;
    }

    public Integer errkey() {
        return errKey;
    }

    public String errmsg() {
        return errMsg;
    }

    public Object extra1() {
        return extra1;
    }

    public Object extra2() {
        return extra2;
    }

    public Object extra3() {
        return extra3;
    }

    public Object extra4() {
        return extra4;
    }

    public Object extra5() {
        return extra5;
    }

    public boolean success() {
        return errKey == null && errMsg == null;
    }

    public static Event name(String eventName) {
        return Event.builder().name(eventName).build();
    }

    public static class Builder {

        private Event e = new Event();

        public Builder name(String name) {
            e.name = name;

            return this;
        }

        public Builder errkey(int errKey) {
            e.errKey = errKey;

            return this;
        }

        public Builder errmsg(String errMsg) {
            e.errMsg = errMsg;

            return this;
        }

        public Builder extra1(Object obj) {
            e.extra1 = obj;

            return this;
        }

        public Builder extra2(Object obj) {
            e.extra2 = obj;

            return this;
        }

        public Builder extra3(Object obj) {
            e.extra3 = obj;

            return this;
        }

        public Builder extra4(Object obj) {
            e.extra4 = obj;

            return this;
        }

        public Builder extra5(Object obj) {
            e.extra5 = obj;

            return this;
        }

        public Event build() {
            return e;
        }

    }

}
