package com.arulvakku.ui.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.arulvakku.ui.model.PushNotification;
import com.arulvakku.ui.model.PushNotificationDao;

@Database(entities = {PushNotification.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PushNotificationDao pushNotificationDao();
}
