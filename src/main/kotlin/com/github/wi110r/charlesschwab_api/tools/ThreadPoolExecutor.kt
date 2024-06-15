package com.github.wi110r.com.github.wi110r.charlesschwab_api.tools

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

// Consider making this a class to keep the thread pool alive.
/*
* CHATGPT Says cached thread pool would be best:
*  Cached Thread Pool: For applications with a large number of short-lived tasks,
* consider using a cached thread pool (Executors.newCachedThreadPool()). This type
* of pool creates new threads as needed but reuses previously constructed threads when they are available.
* */
fun <T> threadPoolHandler(tasks: List<Callable<T>> ,maxThreads: Int = 5, timeoutMS: Long = 7000): List<T> {
    val executor = Executors.newFixedThreadPool(maxThreads)
    val futures = mutableListOf<Future<T>>()
    val data = mutableListOf<T>()
    try {

        // Start tasks
        try {
            for (t in tasks){
                futures.add(executor.submit(t))
            }
        } catch (e: Exception) {
            Log.w("threadHandler()", "Task Failed with exception: ${e.message}")
        }

        // Collect results
        try {
            for (f in futures) {
                val d = f.get(timeoutMS, TimeUnit.MILLISECONDS)
                data.add(d)
            }
        } catch (e: Exception) {
            Log.w("threadHandler()", "Task Failed to return result: ${e.message}")
        }

    } finally {
        executor.shutdown()
    }
    return data
}


fun main() {
    val tasks = mutableListOf<Callable<String>>()
    for (i in 1..10) {
        tasks.add(
            Callable {
//                Thread.sleep((200 * i).toLong())
                println(i)
                return@Callable "$i"
            }
        )
    }
    val x = threadPoolHandler(tasks)
}