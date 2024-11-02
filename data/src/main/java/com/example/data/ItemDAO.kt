package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.dto.ItemRoomDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemRoomDTO>)

    @Query("SELECT * FROM items WHERE name IS NOT NULL AND name != '' ORDER BY list_id, name")
    fun getItemsGroupedAndSorted(): Flow<List<ItemRoomDTO>>
}
