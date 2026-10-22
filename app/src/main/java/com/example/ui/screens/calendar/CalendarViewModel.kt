package com.example.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ProjectFormat
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.data.repository.VideoProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CalendarUiState(
    val scheduledProjects: List<VideoProject> = emptyList(),
    val allProjects: List<VideoProject> = emptyList(),
    val selectedFormatFilter: String? = null,
    val feedbackMessage: String? = null
)

class CalendarViewModel(
    private val projectRepository: VideoProjectRepository
) : ViewModel() {

    private val _selectedFormatFilter = MutableStateFlow<String?>(null)
    private val _feedbackMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CalendarUiState> = combine(
        projectRepository.allProjects,
        _selectedFormatFilter,
        _feedbackMessage
    ) { projects, formatFilter, msg ->
        val filtered = if (formatFilter != null) {
            projects.filter { it.format == formatFilter }
        } else {
            projects
        }
        val scheduled = filtered
            .filter { it.scheduledDateMillis != null }
            .sortedBy { it.scheduledDateMillis }

        CalendarUiState(
            scheduledProjects = scheduled,
            allProjects = filtered,
            selectedFormatFilter = formatFilter,
            feedbackMessage = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    fun setFormatFilter(format: String?) {
        _selectedFormatFilter.value = format
    }

    fun markAsPublished(project: VideoProject) {
        viewModelScope.launch {
            val updated = project.copy(
                status = ProjectStatus.PUBLISHED.name,
                publishedDateMillis = System.currentTimeMillis()
            )
            projectRepository.saveProject(updated)
            _feedbackMessage.value = "Video contrassegnato come Pubblicato!"
        }
    }

    fun postponeProjectByDays(project: VideoProject, days: Int) {
        viewModelScope.launch {
            val currentScheduled = project.scheduledDateMillis ?: System.currentTimeMillis()
            val newScheduled = currentScheduled + (days.toLong() * 86_400_000L)
            val updated = project.copy(scheduledDateMillis = newScheduled)
            projectRepository.saveProject(updated)
            _feedbackMessage.value = "Data programmata posticipata di $days giorni."
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }
}
