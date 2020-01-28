package com.group13.dynamicwayfinder.Activities.Map;

import android.widget.SeekBar;

import com.group13.dynamicwayfinder.R;

public class helperMap {
    private MapActivity mapActivity;
    private SeekBar seekBarSpeed;
    public helperMap(MapActivity mapActivity){
        this.mapActivity=mapActivity;

    }

    public void testMethod(){
        mapActivity.findViewById(R.id.simpleSeekBarSpeed);
    }
}
