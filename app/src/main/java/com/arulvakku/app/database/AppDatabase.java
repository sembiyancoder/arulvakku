package com.arulvakku.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.arulvakku.app.model.PushNotification;
import com.arulvakku.app.model.PushNotificationDao;

@Database(entities = {PushNotification.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PushNotificationDao pushNotificationDao();
}
