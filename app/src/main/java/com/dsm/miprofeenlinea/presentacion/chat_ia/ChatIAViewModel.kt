package com.dsm.miprofeenlinea.presentacion.chat_ia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsm.miprofeenlinea.data.repository.TutorRepository
import com.dsm.miprofeenlinea.model.ChatMessage
import com.dsm.miprofeenlinea.services.TutorChatbot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatIAViewModel : ViewModel() {

    private val repository =
        TutorRepository(
            TutorChatbot()
        )

    private val _uiState =
        MutableStateFlow(
            ChatIAUiState(
                messages = listOf(
                    ChatMessage(
                        text = "Hola 👋 Soy tu Tutor IA.",
                        isUser = false
                    )
                )
            )
        )

    val uiState: StateFlow<ChatIAUiState>
            = _uiState

    fun onMessageChange(
        value: String
    ) {

        _uiState.value =
            _uiState.value.copy(
                currentMessage = value
            )
    }

    fun sendMessage() {

        val question =
            _uiState.value.currentMessage

        if(question.isBlank()) return

        _uiState.value =
            _uiState.value.copy(
                messages =
                    _uiState.value.messages +
                            ChatMessage(
                                question,
                                true
                            ),
                currentMessage = "",
                isTyping = true
            )

        viewModelScope.launch {

            try {

                val response =
                    repository.askTutor(
                        question
                    )

                var answer =
                    response

                var showButton =
                    _uiState.value.showProfessorButton

                if(
                    answer.contains(
                        "[CONTACTAR_PROFESOR]"
                    )
                ) {

                    showButton = true

                    answer =
                        answer.replace(
                            "[CONTACTAR_PROFESOR]",
                            ""
                        )
                }

                _uiState.value =
                    _uiState.value.copy(
                        messages =
                            _uiState.value.messages +
                                    ChatMessage(
                                        answer,
                                        false
                                    ),
                        isTyping = false,
                        showProfessorButton =
                            showButton
                    )

            } catch(e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        messages =
                            _uiState.value.messages +
                                    ChatMessage(
                                        "Error: ${e.message}",
                                        false
                                    ),
                        isTyping = false
                    )
            }
        }
    }
}