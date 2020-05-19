package work.inabajun.ranabauts

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

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
        return ResponseEntity.status(status).body("ok")
    }
}
