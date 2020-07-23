package work.inabajun.ranabauts.domain.command

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URL
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HTTPCommandTest {

    private var webServer: MockWebServer? = null

    @BeforeEach
    fun setup() {
        webServer = setupMockServer()
    }

    @AfterEach
    fun tearDown() {
        webServer?.shutdown()
    }

    @Test
    fun test_NoCommand() {

        // setup
        val rootCommand = HTTPCommand(URL("http://example.com"), emptyList())

        // execute
        rootCommand.executeCommands()

        // verify
        assertThat(webServer?.requestCount).isEqualTo(0)
    }

    @Test
    fun test_SingleCommand() {

        // setup
        val childCommand = HTTPCommand(assembleMockEndpoint(webServer!!), emptyList())
        val rootCommand = HTTPCommand(URL("http://example.com"), listOf(childCommand))

        // execute
        rootCommand.executeCommands()

        // verify
        assertThat(webServer?.requestCount).isEqualTo(1)

        // assertion for HTTP request by command
        val request = webServer?.takeRequest()
        assertThat(request?.body?.readUtf8()).isEqualTo(jacksonObjectMapper().writeValueAsString(childCommand))
        assertThat(request?.method).isEqualTo("POST")
        assertThat(request?.path).isEqualTo("/test")
    }

    @Test
    fun test_MultipleCommand() {

        // setup
        val childCommand1 = HTTPCommand(assembleMockEndpoint(webServer!!), emptyList())
        val childCommand2 = HTTPCommand(assembleMockEndpoint(webServer!!), emptyList())
        val rootCommand = HTTPCommand(URL("http://example.com"), listOf(childCommand1, childCommand2))

        // execute
        rootCommand.executeCommands()

        // verify
        assertThat(webServer?.requestCount).isEqualTo(2)

        // assertion for HTTP request by command
        val request1 = webServer?.takeRequest()
        assertThat(request1?.body?.readUtf8()).isEqualTo(jacksonObjectMapper().writeValueAsString(childCommand1))
        assertThat(request1?.method).isEqualTo("POST")
        assertThat(request1?.path).isEqualTo("/test")

        val request2 = webServer?.takeRequest()
        assertThat(request2?.body?.readUtf8()).isEqualTo(jacksonObjectMapper().writeValueAsString(childCommand2))
        assertThat(request2?.method).isEqualTo("POST")
        assertThat(request2?.path).isEqualTo("/test")
    }

    private fun assembleMockEndpoint(mockServer: MockWebServer): URL {
        return URL("http", mockServer.hostName, mockServer.port, "/test")
    }

    /**
     * Initialize mock server and response mock url
     */
    private fun setupMockServer(): MockWebServer {
        // init web mock
        val webServer = MockWebServer()
        webServer.start()
        webServer.enqueue(MockResponse().apply {
            setResponseCode(200)
            setBody("this is first")
        })
        webServer.enqueue(MockResponse().apply {
            setResponseCode(200)
            setBody("this is second")
        })
        return webServer
    }
}
