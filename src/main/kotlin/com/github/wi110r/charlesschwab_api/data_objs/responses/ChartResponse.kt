package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses



data class ChartResponse(
    val symbol: String,
    val empty: Boolean,
    val previousClose: Double,
    val previousCloseDate: Long,
    val candles: List<CandleResponse>
)

data class CandleResponse(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Int,
    val datetime: Long
)
