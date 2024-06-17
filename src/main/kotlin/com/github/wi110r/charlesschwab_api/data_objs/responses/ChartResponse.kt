package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses

import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.stockchart.Candle
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.stockchart.StockChart
import java.util.*


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

fun ChartResponse.convertToStockChart(timeInterval: String, periodRange: String): StockChart {
    val datetime = this.candles.map { Date(it.datetime) }
    val timestamp = this.candles.map { it.datetime } // Assuming you want to convert to Int

    return StockChart(
        ticker = this.symbol,
        candleSize = timeInterval,  // Assuming a default interval, modify as needed
        periodSize = periodRange,  // Assuming a default period range, modify as needed
        prepost = this.empty,  // Map 'empty' directly
        datetime = datetime,
        timestamp = timestamp,
        open = this.candles.map { it.open },
        high = this.candles.map { it.high },
        low = this.candles.map { it.low },
        close = this.candles.map { it.close },
        volume = this.candles.map { it.volume },
        candles = this.candles.map { Candle(
            dateTime = Date(it.datetime),
            timestamp = it.datetime,
            open = it.open,
            high = it.high,
            low = it.low,
            close = it.close,
            volume = it.volume
        )
        }
    )
}
