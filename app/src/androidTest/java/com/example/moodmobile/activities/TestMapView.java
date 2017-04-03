package com.example.moodmobile.activities;
import android.test.ActivityInstrumentationTestCase2;
import com.example.moodmobile.Osm_mapView;
import com.robotium.solo.Solo;

public class TestMapView extends ActivityInstrumentationTestCase2 <Osm_mapView> {

    private Solo solo;

    public TestMapView() {
        super(com.example.moodmobile.Osm_mapView.class);
    }

    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testButton(){
        solo.clickOnButton("MyMood");
        solo.clickOnButton("Following");
        solo.clickOnButton("People Nearby");
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}



