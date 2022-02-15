package com.sandymist.hellocoroutine.playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
fun main() {
    Dispatchers.setMain(Dispatchers.Unconfined)

    collect()
    //collectLatest()
    //channel()
}

@ExperimentalCoroutinesApi
fun collect() {
    val myFlow = MutableStateFlow(1)

    // consumer A
    CoroutineScope(Dispatchers.Main).launch {
        myFlow.collect {
            println("Got A: $it")
        }
        // NOT REACHED!!! State flow never completes
        println("Never shows up")
    }
/*
    // consumer B
    CoroutineScope(Dispatchers.Main).launch {
        myFlow.collect {
            println("Got B: $it")
            delay(2000)
        }
        // NOT REACHED!!!
        println("Never shows up")
    }
*/
    // producer
    // (note: not necessary to use coroutine here though)
    CoroutineScope(Dispatchers.Main).launch {
        delay(500)
        myFlow.emit(2) // skipped (conflated!!), (see channels)
        myFlow.emit(3)
        delay(400)
        myFlow.emit(4)
        myFlow.emit(5)
        delay(400)
        myFlow.emit(6)
        myFlow.emit(7)
        delay(200)
        myFlow.emit(8)
        myFlow.emit(9)
        myFlow.emit(10)
        delay(600)
        myFlow.emit(11)
        myFlow.emit(11)
        delay(200)
        myFlow.emit(11)
        delay(200)
        myFlow.emit(12)
        delay(200)
        myFlow.emit(11)

        println("Done sending!")
    }

    Thread.sleep(20000)
}

@ExperimentalCoroutinesApi
fun collectLatest() {
    val myFlow = MutableStateFlow(100)

    // consumer A
    CoroutineScope(Dispatchers.Main).launch {
        myFlow.collectLatest {
            println("Got A: $it")
        }
        // NOT REACHED!!!
        println("Never shows up")
    }

    // consumer B
    CoroutineScope(Dispatchers.Main).launch {
        myFlow.collectLatest {
            println("Got B: $it")
            delay(2000)
        }
        // NOT REACHED!!!
        println("Never shows up")
    }

    // producer
    // (note: not necessary to use coroutine here though)
    CoroutineScope(Dispatchers.Main).launch {
        delay(1000)
        myFlow.value = 200 // skipped (conflated!!), (see channels)
        myFlow.value = 300
        delay(2000)
        myFlow.value = 400
        myFlow.value = 500

        println("Done sending!")
    }

    Thread.sleep(20000)
}

fun channel() {
    val myChannel = Channel<Int>()

    // producer
    CoroutineScope(Dispatchers.Main).launch {
        (1..15000).forEach {
            myChannel.send(it * 2)
            if (it == 10) {
                println("Reached 10, so pause for 10s")
                delay(10000)
            }
        }
        myChannel.close()
    }

    // consumer A (fast)
    CoroutineScope(Dispatchers.Main).launch {
        for (i in myChannel) {
            println("Got A: $i")
            delay(200)
        }
    }

    // consumer B (slow)
    CoroutineScope(Dispatchers.Main).launch {
        for (i in myChannel) {
            println("Got B: $i")
            delay(800)
        }
    }

    Thread.sleep(5000000)
}