package com.dsm.miprofeenlinea.presentacion.chat_ia

import com.dsm.miprofeenlinea.model.ChatMessage

data class ChatIAUiState(

    val messages: List<ChatMessage> = listOf(),

    val currentMessage: String = "",

    val isTyping: Boolean = false,

    val showProfessorButton: Boolean = false
)