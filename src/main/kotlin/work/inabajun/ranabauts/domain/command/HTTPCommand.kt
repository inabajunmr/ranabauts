package work.inabajun.ranabauts.domain.command

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URL
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.LoggerFactory

/**
 * Command expresses HTTP request
 */
class HTTPCommand(uri: URL, response: Response, commands: List<Command>) : Command(response, commands, CommandType.HTTP) {

    companion object {
        private val JSON_MEDIA_TYPE: MediaType = MediaType.get("application/json; charset=utf-8")
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * This command send request to the uri
     */
    val uri: URL = uri

    override fun execute() {
        val client = OkHttpClient()
        val body = RequestBody.create(JSON_MEDIA_TYPE, serializeJson())
        val request = Request.Builder().url(uri).post(body).build()
        val response = client.newCall(request).execute()
        logger.info("Call:$uri. Status:${response.code()}. Body:${response.body()?.string()}")
    }

    private fun serializeJson(): String {
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(this)
    }
}
