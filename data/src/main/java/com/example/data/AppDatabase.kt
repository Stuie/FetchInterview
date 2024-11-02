package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.dto.ItemRoomDTO

@Database(entities = [ItemRoomDTO::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
