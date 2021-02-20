package com.arulvakku.ui.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PushNotificationDao {

    @Query("SELECT * FROM PushNotification")
    List<PushNotification> getAll();

    @Insert
    void insert(PushNotification task);

    @Delete
    void delete(PushNotification task);

    @Update
    void update(PushNotification task);

}
