package com.dsm.miprofeenlinea.services

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

class TutorChatbot {

    private val generativeModel =
        Firebase.ai(
            backend = GenerativeBackend.googleAI()
        ).generativeModel(
            modelName = "gemini-2.5-flash"
        )

    suspend fun ask(question: String): String {

        val prompt = """
        Eres EduTutor.

        Explica conceptos educativos.

        Usa ejemplos simples.

        Si el alumno necesita atención personalizada agrega:

        [CONTACTAR_PROFESOR]

        Pregunta:
        $question
        """.trimIndent()

        val response =
            generativeModel.generateContent(
                prompt
            )

        return response.text ?: ""
    }
}