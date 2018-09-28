package com.apptellect.utilities;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karthi on 9/21/2018.
 */

public class LocationParser {
    public static List<String> ParseLocation(List<Location> locations)
    {
        List<String> values = new ArrayList<>();

        for (Location current :
                locations)
        {
            String val = current.getLatitude()+ ":" + current.getLongitude();
            values.add(val);
        }
        return values;
    }

    public static List<Location> ParseString(List<String> values)
    {
        List<Location> locations = new ArrayList<>();
        for (String val :
                values)
        {
                String[] vals = val.split(":");
        }
        return null;
    }
}
