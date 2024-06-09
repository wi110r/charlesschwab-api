package com.github.wi110r.com.github.wi110r.charlesschwab_api.tools

import java.io.File
import java.io.FileWriter

object FileHelper {

    fun readFileToString(path: String): String {
        val file = File(path)

        // Read the file line by line
        try {
            val lines = file.readText()
            return lines
        } catch (e: Exception) {
            return "Error reading file: ${e.message}"

        }
    }

    fun writeFile(path: String, txt: String) {
        val file = File(path)

        FileWriter(file).use { writer ->
            writer.write(txt)
        }
        println("\nFILE SAVED: $path.\n\"${txt.subSequence(0, 80)}...\"")
    }

}