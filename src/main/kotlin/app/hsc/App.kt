package app.hsc

import com.sun.jna.platform.win32.Advapi32Util.*
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Discord
import io.kotest.common.runBlocking
import kotlinx.coroutines.DelicateCoroutinesApi
import java.nio.file.Files
import java.nio.file.Path



class App {
    @OptIn(DelicateCoroutinesApi::class)
    fun run(args: Array<String>) {
        if (args.isEmpty()) {
            val message = "1 parameter: 1 - attributes.xml path, 2 - Steam name in case no registry found"
            println(message)
            throw RuntimeException(message)
        }
        val huntAttributesPath = Path.of(args[0])
        val playerName = PlayerNameRetriever.getPlayerName(args)
        val config = Config()
        val context = config.configureContext()
        println("Start")
        val body = Files.readString(huntAttributesPath)

        runBlocking {
            if (!context.supabase.gotrue.loadFromStorage()) {
                context.supabase.gotrue.loginWith(Discord)
            }
            context.sender.sendMatch(playerName, body)
        }

//        val job = GlobalScope.launch {
////            context.supabase.gotrue.loginWith(Discord)
//            println("Start")
//
//            context.tracker.track(path) {event ->
//                println("File Change: ${event.kind.kind}")
//                if(event.kind.kind == "modified") {
//                    println("File has been modified")
//                    val body = Files.readString(path)
//                    runBlocking { context.sender.sendMatch(playerName, userId, body) }
//                }
//
//            }
//
//        }
//        Awaitility.await().forever().pollInterval(Duration.ofSeconds(5)).until {
//            println("job is active")
//            job.isActive
//        }


        println("Finish")
    }


}