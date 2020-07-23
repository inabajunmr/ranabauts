package work.inabajun.ranabauts.domain.command

/**
 * Command is series of commands and response for application.
 *
 * Application that accepts this command executes series of commands and return response.
 * This command will be accepted via HTTP request.
 */
abstract class Command(commands: List<Command>, commandType: CommandType) {

    /**
     * Command List to execute by an application that accepts this command
     */
    val commands: List<Command> = commands

    /**
     * Command type
     */
    val type: CommandType = commandType

    /**
     * execute this command
     */
    protected abstract fun execute(): HttpResult

    /**
     * execute commands that this command has
     * @return response
     */
    fun executeCommands(): HttpResult {
        val results = commands.map { c: Command -> c.execute() }.toList()
        return HttpResult(results)
    }
}
