package work.inabajun.ranabauts

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import work.inabajun.ranabauts.domain.command.Command

@RestController
@RequestMapping("/command")
class CommandController {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Command API Handler.
     *
     * # Spec
     * This api returns http request body.
     *
     * @param status HTTP status code
     */
    @PostMapping
    fun command(@RequestBody command: Command): Any? {
        logger.info("Call command. Command:{}", command)
        val result = command.executeCommands()
        return result.body
    }
}
