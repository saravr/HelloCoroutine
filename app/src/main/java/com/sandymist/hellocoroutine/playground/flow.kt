package com.sandymist.hellocoroutine.playground

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import java.lang.Exception
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
fun main() {
    Dispatchers.setMain(Dispatchers.Unconfined)

    // cold flows
    println("Simple cold flow ...")
    val numbers = flowOf(1, 2, 3, 4 ,5)
    runBlocking { // collect is a suspend function, so needs to be run as a coroutine
        numbers.collect(::print)
    }
    println()

    println("A list converted into a flow ...")
    val letters = listOf('k', 'o', 't', 'l', 'i', 'n')
    runBlocking {
        letters.asFlow().collect(::print)
    }
    println()

    println("Flow using a builder ...")
    val words = flow {
        emit("kotlin")
        delay(500)
        emit("flows")
        delay(500)
        /*try {
            emit("error")
            throw Exception("some exception")
        } catch (e: Exception) {
            println("ERROR: got exception")
        }*/
        emit("are")
        delay(500)
        emit("awesome")
    }.map(::capitalize)

    runBlocking {
        words.collect(::print)
    }
    println()

    // demonstrate context in flows
    println("Demonstrate context in flows ...")
    val values = flowOf(100, 200, 300)
        .map {
            println("Mapping: $it with context: ${Thread.currentThread().name}")
            it + 5
        }
    //.flowOn(Dispatchers.IO)

    CoroutineScope(Dispatchers.Main).launch {
    //runBlocking {
        values.collect {
            println("Value: $it")
        }
        println("Done collecting!!!")
    }

    println("Testing mis matched speed")
    // mismatched speed (producer is slow)
    val itemFlow = flow {
        emit(1)
        delay(500)
        emit(2)
        emit(3)
        delay(900)
        emit(4)
        emit(5)
        delay(900)
        emit(6)
        emit(7)
        delay(1200)
        emit(8)
        emit(9)
        emit(10)
        delay(900)
        emit(11)
        emit(12)
    }

    runBlocking {
        val duration = measureTimeMillis {
            itemFlow
                //.conflate() // process latest, skip in between
                .buffer() // will make consumer save time!!!
                .collect {
                    delay(400)
                    println("Value: $it")
                }
        }
        println("Done collecting (mismatched speed)!!! duration $duration")
    }

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // handle ...
    }
    val context = Dispatchers.Main + SupervisorJob() + CoroutineName("My coroutine") + exceptionHandler
    CoroutineScope(context)
}

fun capitalize(string: String) = string.uppercase()

suspend fun separate(strings: List<String>) = strings.joinToString()

