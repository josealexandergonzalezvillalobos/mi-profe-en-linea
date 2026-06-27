package com.dsm.miprofeenlinea.data.repository

import com.dsm.miprofeenlinea.services.TutorChatbot

class TutorRepository(
    private val chatbot: TutorChatbot
) {

    suspend fun askTutor(
        question: String
    ): String {

        return chatbot.ask(
            question
        )
    }
}