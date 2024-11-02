package com.example.data.dto

import com.example.models.Item
import kotlinx.serialization.Serializable

@Serializable
data class ItemDTO(
    val id: Int,
    val listId: Int,
    val name: String?
) {
    fun toItem(): Item? {
        // Convert to `Item`, filtering out any with blank or null names
        return name?.takeIf { it.isNotBlank() }?.let { nonBlankName ->
            Item(id = id, listId = listId, name = nonBlankName)
        }
    }
}

