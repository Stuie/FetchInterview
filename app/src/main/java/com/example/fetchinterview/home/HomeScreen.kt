package com.example.fetchinterview.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.testTag
import com.example.models.Item

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel<DefaultHomeViewModel>(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is UiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
            }
        }
        is UiState.Loaded -> {
            DisplayContent(
                items = (uiState as UiState.Loaded).items,
                modifier = modifier
            )
        }
        is UiState.NoNetworkWithData -> {
            Column(modifier = modifier.fillMaxSize()) {
                // Banner notification for offline mode
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.error)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "No network connection. Displaying offline data.",
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                // Reuse the content display
                DisplayContent(
                    items = (uiState as UiState.NoNetworkWithData).items,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        is UiState.NoNetworkAndNoData -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "No network connection and no data available.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is UiState.Error -> {
            val message = (uiState as UiState.Error).message
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayContent(items: List<Item>, modifier: Modifier = Modifier) {
    val groupedItems = items.groupBy { it.listId }
    val expandedState = remember { mutableStateMapOf<Int, Boolean>() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        groupedItems.forEach { (listId, itemsInList) ->
            val isExpanded = expandedState[listId] ?: true

            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            expandedState[listId] = !isExpanded
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "List $listId",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            if (isExpanded) {
                items(itemsInList) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 16.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                    ) {
                        Text(text = item.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
