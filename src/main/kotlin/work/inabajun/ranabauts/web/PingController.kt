package work.inabajun.ranabauts

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Ping API Handler.
     *
     * # Spec
     * This api returns http request body.
     *
     * @param status HTTP status code
     */
    @GetMapping("/ping")
    fun echo(@RequestParam("status") status: Int): ResponseEntity<String> {
        logger.info("Call ping. Status:{}", status)
        return ResponseEntity.status(status).body("ok")
    }
}
