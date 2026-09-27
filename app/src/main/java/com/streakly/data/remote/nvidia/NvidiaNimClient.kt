package com.streakly.data.remote.nvidia

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NvidiaNimClient {

    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: NvidiaChatRequest
    ): NvidiaChatResponse

    companion object {
        const val DEFAULT_BASE_URL = "https://integrate.api.nvidia.com/"
        const val DEFAULT_MODEL = "meta/llama-3.1-70b-instruct"
    }
}
