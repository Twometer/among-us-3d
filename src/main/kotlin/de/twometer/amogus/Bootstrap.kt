package de.twometer.amogus

import de.twometer.amogus.client.AmongUsClient
import de.twometer.amogus.server.AmongUsServer

fun main(args: Array<String>) {
    println(
        """
   ___                          __  __      ____ ___ 
  / _ | __ _  ___  ___  ___ _  / / / /__   |_  // _ \
 / __ |/  ' \/ _ \/ _ \/ _ `/ / /_/ (_-<  _/_ </ // /
/_/ |_/_/_/_/\___/_//_/\_, /  \____/___/ /____/____/ 
                      /___/                          
    """.trimIndent()
    )

    when (if (args.isNotEmpty()) args[0] else "client") {
        "client" -> AmongUsClient.run()
        "server" -> AmongUsServer.main()
        else -> println("Invalid startup option")
    }
}