package com.example.ui.screens.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.VideoProject
import com.example.data.repository.VideoProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ArchiveUiState(
    val projects: List<VideoProject> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedStatus: String? = null,
    val feedbackMessage: String? = null
)

class ArchiveViewModel(
    private val projectRepository: VideoProjectRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _selectedStatus = MutableStateFlow<String?>(null)
    private val _feedbackMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ArchiveUiState> = combine(
        projectRepository.allProjects,
        _searchQuery,
        _selectedCategory,
        _selectedStatus,
        _feedbackMessage
    ) { all, query, cat, status, msg ->
        var list = all
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.tags.contains(query, ignoreCase = true)
            }
        }
        if (cat != null) {
            list = list.filter { it.category == cat }
        }
        if (status != null) {
            list = list.filter { it.status == status }
        }
        ArchiveUiState(
            projects = list,
            searchQuery = query,
            selectedCategory = cat,
            selectedStatus = status,
            feedbackMessage = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ArchiveUiState()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(cat: String?) {
        _selectedCategory.value = cat
    }

    fun selectStatus(status: String?) {
        _selectedStatus.value = status
    }

    fun duplicateProject(id: Long) {
        viewModelScope.launch {
            val copyId = projectRepository.duplicateProject(id)
            if (copyId != null) {
                _feedbackMessage.value = "Copia creata con successo nell'archivio!"
            }
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            projectRepository.deleteProject(id)
            _feedbackMessage.value = "Elemento eliminato definitivamente."
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }
}
