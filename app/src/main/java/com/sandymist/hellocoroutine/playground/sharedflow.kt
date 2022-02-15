package com.sandymist.hellocoroutine.playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
fun main() {
    Dispatchers.setMain(Dispatchers.Unconfined)

    val myFlow = MutableSharedFlow<Int>()

    // consumer A
    CoroutineScope(Dispatchers.Main).launch {
        myFlow.collect {
            println("Got A: $it")
        }
        // NOT REACHED!!! Shared flow never completes
        println("Never shows up")
    }

    // consumer B
    CoroutineScope(Dispatchers.Main).launch {
        myFlow.collect {
            println("Got B: $it")
        }
        // NOT REACHED!!!
        println("Never shows up")
    }

    CoroutineScope(Dispatchers.Main).launch {
        myFlow.emit(1)
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
        myFlow.emit(12)
        myFlow.emit(12)
        myFlow.emit(12)

        println("Done sending!")
    }

    Thread.sleep(50000)
}