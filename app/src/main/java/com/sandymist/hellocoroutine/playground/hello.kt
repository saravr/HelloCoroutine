package com.sandymist.hellocoroutine.playground

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    launch {
        launch {
            delay(3000)
            println("One")
        }
        launch {
            delay(500)
            println("Two")
        }
        launch {
            delay(3000)
            println("Three")
        }
    }
}

fun old2() = runBlocking {
    //val coroutineScope = CoroutineScope(Job())

    try {
        coroutineScope {
            launch {
                try {
                    throw RuntimeException()
                } catch (e: IllegalAccessException) {
                    println("Caught exception inside: ${e.message}")
                }
            }
        }
    } catch (e: Exception) {
        println("Caught exception outside: ${e.message}")
    }

    Thread.sleep(1000)
}

fun old1() {
    val coroutineScope = CoroutineScope(Job())
    val job = coroutineScope.async {
        println("Hello")
        delay(1000)
        throw RuntimeException()
        println("World")
    }

    coroutineScope.launch {
        job.await()
    }

    Thread.sleep(2000)
}

fun old() {
    CoroutineScope(Job()).launch {
        println("Hello")
        launch {
            delay(5000)
            println("World 1")
        }
        launch {
            delay(2000)
            println("World 2")
        }
    }
    Thread.sleep(7000)
}