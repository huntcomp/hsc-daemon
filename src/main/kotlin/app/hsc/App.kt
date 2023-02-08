package app.hsc

import com.sun.jna.platform.WindowUtils
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.Advapi32Util.*
import com.sun.jna.platform.win32.WinReg
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
import kotlin.text.Typography.section

class App {
    @OptIn(DelicateCoroutinesApi::class)
    fun run(args: Array<String> ){
        if(args.isEmpty()) {
            val message = "1 parameter: 1 - attributes.xml path"
            println(message)
            throw RuntimeException(message)
        }
        val path = Path.of(args[0])
        val playerName = getPlayerName()
        val config = Config()
        val context = config.configureContext()
        println("Start")
        val body = Files.readString(path)

        runBlocking {
            if(!context.supabase.gotrue.loadFromStorage()) {
                context.supabase.gotrue.loginWith(Discord)
            }
            context.sender.sendMatch(playerName, body) }

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

    private fun getPlayerName() : String {
        return registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "HKCU:\\SOFTWARE\\Valve\\Steam", "LastGameNameUsed");
    }
}