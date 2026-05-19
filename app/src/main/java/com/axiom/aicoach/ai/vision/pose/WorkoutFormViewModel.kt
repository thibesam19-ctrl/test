package com.axiom.aicoach.ai.vision.pose

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class WorkoutFormUiState(
    val exerciseType: ExerciseType = ExerciseType.SQUAT,
    val repCount: Int = 0,
    val formScore: Int = 100,
    val feedback: List<FormFeedback> = emptyList(),
    val isAnalyzing: Boolean = false,
    val hasCameraPermission: Boolean = false,
)

@HiltViewModel
class WorkoutFormViewModel @Inject constructor(
    private val poseEngine: MLKitPoseAnalysisEngine,
) : ViewModel() {

    val repCount  = poseEngine.repCount
    val formScore = poseEngine.formScore
    val feedback  = poseEngine.feedback

    private val _uiState = MutableStateFlow(WorkoutFormUiState())
    val uiState: StateFlow<WorkoutFormUiState> = _uiState.asStateFlow()

    fun selectExercise(type: ExerciseType) {
        poseEngine.setExerciseType(type)
        _uiState.update { it.copy(exerciseType = type) }
    }

    fun onPermissionGranted() {
        _uiState.update { it.copy(hasCameraPermission = true, isAnalyzing = true) }
    }

    fun startAnalysis() {
        _uiState.update { it.copy(isAnalyzing = true) }
    }

    fun processFrame(imageProxy: ImageProxy) {
        poseEngine.processFrame(imageProxy).launchIn(viewModelScope)
    }

    fun stopAnalysis() {
        poseEngine.reset()
        _uiState.update { it.copy(isAnalyzing = false, repCount = 0) }
    }

    override fun onCleared() {
        super.onCleared()
        poseEngine.reset()
    }
}
