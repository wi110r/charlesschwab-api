package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.stockchart

import java.util.*

data class StockChartCSBasic(
    val ticker: String,
    val candleSize: String,
    val periodSize: String,
    val prepost: Boolean,
    val datetime: List<Date>,
    val timestamp: List<Long>,
    val open: List<Double>,
    val high: List<Double>,
    val low: List<Double>,
    val close: List<Double>,
    val volume: List<Int>,
    val candles: List<Candle>
)

data class Candle(
    val dateTime: Date,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Int
)
