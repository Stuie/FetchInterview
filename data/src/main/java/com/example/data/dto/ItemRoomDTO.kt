package com.example.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.example.models.Item

@Entity(tableName = "items")
data class ItemRoomDTO(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "list_id") val listId: Int,
    @ColumnInfo(name = "name") val name: String
) {
    fun toItem(): Item {
        return Item(id = id, listId = listId, name = name)
    }

    companion object {
        fun fromItem(item: Item): ItemRoomDTO {
            return ItemRoomDTO(id = item.id, listId = item.listId, name = item.name)
        }
    }
}
