package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.stockchart

import java.util.*

data class Candle(
    val dateTime: Date,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Int
)