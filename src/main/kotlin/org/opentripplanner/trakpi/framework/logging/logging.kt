package org.opentripplanner.trakpi.store.framework.logging

/**
 * It might seem strange that we don´t use a standard logging framework. But for now - this is all
 * we need. As long as the application is a command line tool - we only need to log to the console.
 * <p/>
 * There is no _error(...)_ logging method, throw an exception instead - using a fail-early strategy
 * for error handling.
 */
class Log(val source : String) {
    companion object {
        var debugEnabled = false
    }

    fun debug(msg: String) {
        if (debugEnabled) println("DEBUG | $source | $msg")
    }

    fun info(msg: String) {
        println("INFO  | $source | $msg")
    }

    fun warn(msg: String) {
        System.err.println("WARN  | $source | $msg")
    }
}
