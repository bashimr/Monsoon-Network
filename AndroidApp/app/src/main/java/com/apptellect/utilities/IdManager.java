package com.apptellect.utilities;

import java.util.UUID;

public class IdManager
{
    public static String getNewID()
    {
        return UUID.randomUUID().toString();
    }
}
