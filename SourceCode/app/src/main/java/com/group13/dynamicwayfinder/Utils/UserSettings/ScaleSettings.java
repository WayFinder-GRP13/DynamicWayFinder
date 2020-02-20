package com.group13.dynamicwayfinder.Utils.UserSettings;

public class ScaleSettings {
    private int environmentalScale;
    private int costScale;
    private int speedScale;

    public ScaleSettings(int environmentalScale, int costScale, int speedScale) {
        this.environmentalScale = environmentalScale;
        this.costScale = costScale;
        this.speedScale = speedScale;
    }

    public int getEnvironmentalScale() {
        return environmentalScale;
    }

    public void setEnvironmentalScale(int environmentalScale) {
        this.environmentalScale = environmentalScale;
    }

    public int getCostScale() {
        return costScale;
    }

    public void setCostScale(int costScale) {
        this.costScale = costScale;
    }

    public int getSpeedScale() {
        return speedScale;
    }

    public void setSpeedScale(int speedScale) {
        this.speedScale = speedScale;
    }
}
