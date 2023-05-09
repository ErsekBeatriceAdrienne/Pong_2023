package com.example.pong;

public class Stop_Threads {
    private boolean RUN;

    public Stop_Threads() {
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
