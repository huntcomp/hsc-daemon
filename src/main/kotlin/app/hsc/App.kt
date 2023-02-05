package app.hsc

import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Discord
import io.kotest.common.runBlocking
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.processNextEventInCurrentThread
import org.awaitility.Awaitility
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

class App {
    @OptIn(DelicateCoroutinesApi::class)
    fun run(args: Array<String> ){

        if(args.size < 3) {
            val message = "3 parameters: 1 - attributes.xml path, 2 - player name, 3 - userid -> contact Viters or Rumcajs for support"
            println(message)
            throw RuntimeException(message)
        }
        val path = Path.of(args[0])
        val playerName = args[1]
        val userId = args[2]
        val config = Config()
        val context = config.configureContext()

        val job = GlobalScope.launch {
//            context.supabase.gotrue.loginWith(Discord)
            println("Start")
            context.tracker.track(path) {event ->
                println("File Change: ${event.kind.kind}")
                if(event.kind.kind == "modified") {
                    println("File has been modified")
                    val body = Files.readString(path)
                    runBlocking { context.sender.sendMatch(playerName, userId, body) }
                }

            }

        }
        Awaitility.await().forever().pollInterval(Duration.ofSeconds(5)).until {
            println("job is active")
            job.isActive
        }


        println("Finish")
    }
}