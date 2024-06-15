package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs

import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses.CandleResponse

data class StockChart(
    val symbol: String,
    val empty: Boolean,
    val previousCloseDate: Long,
    val candles: List<CandleResponse>

) {

}

data class Candle(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Int,
    val datetime: Long,
    val dateyyyyMMdd: String,
)