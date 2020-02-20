package com.group13.dynamicwayfinder.Utils.UserSettings;

public abstract class TransportSettings {
    private boolean enabled;
    private int bugValue;


    protected TransportSettings(boolean enabled, int bugValue) {
        this.enabled = enabled;
        this.bugValue = bugValue;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBugValue() {
        return bugValue;
    }

    public void setBugValue(int bugValue) {
        this.bugValue = bugValue;
    }
}
