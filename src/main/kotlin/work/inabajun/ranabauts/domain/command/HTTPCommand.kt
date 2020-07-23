package work.inabajun.ranabauts.domain.command

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.lang.Exception
import java.net.URL
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.LoggerFactory

/**
 * Command expresses HTTP request
 */
class HTTPCommand(uri: URL, commands: List<Command>) : Command(commands, CommandType.HTTP) {

    companion object {
        private val JSON_MEDIA_TYPE: MediaType = MediaType.get("application/json; charset=utf-8")
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * This command send request to the uri
     */
    val uri: URL = uri

    override fun execute(): HttpResult {

        try { val client = OkHttpClient()
            val requestBody = serializeJson()
            val request = Request.Builder().url(uri).post(RequestBody.create(JSON_MEDIA_TYPE, requestBody)).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string()
            logger.info("Call:$uri. Status:${response.code()}. Request Body:$requestBody Response Body:$responseBody")
            return HttpResult(responseBody)
        } catch (e: Exception) {
            logger.error("Failed to call:$uri. message:${e.message}", e)
            return HttpResult("Failed to call:$uri. message:${e.message}")
        }
    }

    private fun serializeJson(): String {
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(this)
    }

    override fun toString(): String {
        return serializeJson()
    }
}
