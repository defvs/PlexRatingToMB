package util

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

fun <T> ArgParser.Delegate<T>.optional(): ArgParser.Delegate<T?> = default { null }