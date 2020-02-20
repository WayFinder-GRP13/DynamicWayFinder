package com.group13.dynamicwayfinder.Utils.UserSettings;

public class UserSettings {
    private int id;
    private BusSettings busSettings;
    private RailSettings railSettings;
    private WalkSettings walkSettings;
    private CarSettings carSettings;
    private CycleSettings cycleSettings;
    private ScaleSettings scaleSettings;

    public UserSettings(int id, BusSettings busSettings, RailSettings railSettings, CarSettings carSettings,WalkSettings walkSettings,CycleSettings cycleSettings,ScaleSettings scaleSettings) {
        this.id = id;
        this.busSettings = busSettings;
        this.railSettings = railSettings;
        this.carSettings = carSettings;
        this.walkSettings = walkSettings;
        this.cycleSettings = cycleSettings;
        this.scaleSettings=scaleSettings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BusSettings getBusSettings() {
        return busSettings;
    }

    public void setBusSettings(BusSettings busSettings) {
        this.busSettings = busSettings;
    }

    public RailSettings getRailSettings() {
        return railSettings;
    }

    public void setRailSettings(RailSettings railSettings) {
        this.railSettings = railSettings;
    }

    public WalkSettings getWalkSettings() {
        return walkSettings;
    }

    public void setWalkSettings(WalkSettings walkSettings) {
        this.walkSettings = walkSettings;
    }

    public CarSettings getCarSettings() {
        return carSettings;
    }

    public void setCarSettings(CarSettings carSettings) {
        this.carSettings = carSettings;
    }

    public CycleSettings getCycleSettings() {
        return cycleSettings;
    }

    public void setCycleSettings(CycleSettings cycleSettings) {
        this.cycleSettings = cycleSettings;
    }

    public ScaleSettings getScaleSettings() {
        return scaleSettings;
    }

    public void setScaleSettings(ScaleSettings scaleSettings) {
        this.scaleSettings = scaleSettings;
    }
}