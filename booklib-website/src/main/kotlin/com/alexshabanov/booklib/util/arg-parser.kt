package com.alexshabanov.booklib.util

/** Argument parser */

val DEFAULT_PORT = 8080
val DEFAULT_CONFIG = "classpath:settings/default.properties"

class ArgParser(val args: Array<String>) {
  var pos: Int = 0
  var port = DEFAULT_PORT
  var configPath = DEFAULT_CONFIG

  private fun showHelp() {
    println("Usage:\n" +
        "--help,-h        Show help.\n" +
        "--port {NUMBER}  Port number, default value=${DEFAULT_PORT}\n" +
        "--config {PATH}  Path to config file, default value=${DEFAULT_CONFIG}\n" +
        "\n")
  }

  private fun argValue(valueName: String): String {
    val nextPos = pos + 1
    if (nextPos < args.size()) {
      pos = nextPos
      return args[pos]
    }
    throw IllegalStateException("Extra argument expected for ${valueName}")
  }

  private fun doParse(): Int {
    while (pos < args.size()) {
      if ("--help".equals(args[pos]) or "-h".equals(args[pos])) {
        showHelp()
        return 0
      }

      if ("--port".equals(args[pos])) {
        try {
          port = Integer.parseInt(argValue("port number"))
        } catch (e: NumberFormatException) {
          throw IllegalStateException("Unable to parse port number", e)
        }
      } else if ("--config".equals(args[pos])) {
        configPath = argValue("config location")
      }

      ++pos // go to the next position
    }

    return 0
  }

  fun parse(): Int {
    try {
      return doParse()
    } catch (e: IllegalStateException) {
      println("Error: ${e}\n")
      showHelp()
      return -1
    }
  }
}
