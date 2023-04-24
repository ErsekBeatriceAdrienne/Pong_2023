package com.example.pong;

public class StopThreads {
    private boolean RUN;

    public StopThreads() {
        this.RUN = true;
    }

    public boolean setRUNFalse() {
        this.RUN = false;
        return RUN;
    }

    public boolean setRUNTrue() {
        this.RUN = true;
        return RUN;
    }

    public boolean getRUN() {
        return RUN;
    }
}
