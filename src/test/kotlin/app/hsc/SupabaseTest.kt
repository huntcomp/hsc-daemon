package app.hsc

import io.kotest.core.spec.style.FunSpec
import io.github.jan.supabase.gotrue.SessionStatus.Authenticated
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
class SupabaseTest : FunSpec({

    test("successfully logs in with discord").config{
        //given
        val context = Config().configureContext()
        val supabase = Supabase(context)

        //when
        val result = supabase.logIn()

        //then
        result should beInstanceOf<Authenticated>()
    // Data(1, "2023-02-01 20:59:11.341776+00", "drt", 32)
    }
})
