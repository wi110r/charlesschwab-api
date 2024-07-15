package com.github.wi110r.com.github.wi110r.charlesschwab_api.tools

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.TimeUnit
import com.github.wi110r.com.github.wi110r.charlesschwab_api.tools.Log as log


internal object NetworkClient {

    private var client: OkHttpClient? = null
    private var maxRetries = 60     // Sleeps each try
    private var retrySleepMs = 1_000L

    /**
     * Returns the Singleton Instance of OkHttpclient
     */
    fun getClient(): OkHttpClient {
        if (client == null) {
            client = buildClient()
        }

        return client!!
    }


    /**
     * Used to initially build the OkHttpClient. Only called once during program life.
     */
    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(5_000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(true)      // VERY IMPORTANT FOR ANDROID
            .followSslRedirects(true)       // ALSO MAYBE
            .build()
    }


    // TODO Delete me -- decided not to use it (causes problems)
    private fun initRetryInterceptor(): Interceptor {

        return object : Interceptor {

            override fun intercept(chain: Interceptor.Chain): Response {
                var retryCount = 0
                val req = chain.request()
                var response: Response? = null

                while (retryCount <= maxRetries){

                    try {

                        // Make request
                        response = chain.proceed(req)

                        // Return on Success
                        if (response.isSuccessful) {
                            return response
                        }

                        // Close, increase count, Log, and sleep on Fail
                        else {
                            response.close()

                            retryCount ++

                            log.w("charlesschwab-api.NetworkClient",
                                "-- RETRY INTERCEPTOR TRIGGERED -- Response Code: ${response.code} " +
                                        "Msg: ${response.message} " +
                                        "Retry Count: $retryCount | Max Retries: $maxRetries")

                            Thread.sleep(retrySleepMs)
                        }

                        // Close, increase count, Log, and sleep on Fail
                    } catch (e: Exception) {

                        // Response will Never be null at this point
                        response?.close()

                        retryCount++

                        log.w("charlesschwab-api.NetworkClient","Retry Interceptor Failed with" +
                                " Exception: ${e.message} \n" +
                                "${e.stackTrace}")

                        Thread.sleep(retrySleepMs)

                    }
                }

                // Return failed response after max retries reached
                return createMaxRetriesReachedResponse(req)
            }
        }
    }


    private fun createMaxRetriesReachedResponse(request: Request): Response {

        return Response.Builder()
            .request(request)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(500) // Generic error code
            .message(
                "charlesschwab-api.NetworkClient RetryInterceptor Failed. " +
                        "Max retries of ${maxRetries} reached. Unknown error."
            )
            .body("Request failed after $maxRetries attempts".toResponseBody(null))
            .build()
    }


}

