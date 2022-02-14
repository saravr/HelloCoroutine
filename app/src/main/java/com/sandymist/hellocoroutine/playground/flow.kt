package com.sandymist.hellocoroutine.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main() {

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
        emit("are")
        delay(500)
        emit("awesome")
    }.map {
        "$it "
    }
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
    runBlocking {
        values.collect {
            println("Value: $it")
        }
    }
}