package com.example.data

import com.example.data.dto.ItemDTO
import com.example.data.dto.ItemRoomDTO
import com.example.models.Item
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

interface ItemRepository {
    suspend fun getStoredItems(): List<Item>
    suspend fun refreshItems(): Result<Unit>
}

class DefaultItemRepository(
    private val itemDao: ItemDao,
    private val client: HttpClient
) : ItemRepository {

    override suspend fun refreshItems(): Result<Unit> {
        return try {
            val itemDTOs: List<ItemDTO> = client
                .get(DATA_URL)
                .body()
            val items = itemDTOs.mapNotNull { it.toItem() }
            itemDao.insertItems(items.map { ItemRoomDTO.fromItem(it) })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStoredItems(): List<Item> {
        return itemDao.getItemsGroupedAndSorted()
            .map { roomItems -> roomItems.map { it.toItem() } }
            .firstOrNull() ?: emptyList()
    }
}

const val DATA_URL = "https://fetch-hiring.s3.amazonaws.com/hiring.json"
