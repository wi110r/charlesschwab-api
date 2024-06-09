package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses

import com.google.gson.annotations.SerializedName

data class Quote(
    @SerializedName("assetMainType") val assetMainType: String,
    @SerializedName("assetSubType") val assetSubType: String,
    @SerializedName("quoteType") val quoteType: String,
    @SerializedName("realtime") val realtime: Boolean,
    @SerializedName("ssid") val ssid: Long,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("fundamental") val fundamental: Fundamental,
    @SerializedName("quote") val basicData: BasicData,
    @SerializedName("reference") val reference: Reference,
    @SerializedName("regular") val regular: MinimumData
)

data class Fundamental(
    @SerializedName("avg10DaysVolume") val avg10DaysVolume: Long,
    @SerializedName("avg1YearVolume") val avg1YearVolume: Long,
    @SerializedName("divAmount") val divAmount: Double,
    @SerializedName("divFreq") val divFreq: Int,
    @SerializedName("divPayAmount") val divPayAmount: Double,
    @SerializedName("divYield") val divYield: Double,
    @SerializedName("eps") val eps: Double,
    @SerializedName("fundLeverageFactor") val fundLeverageFactor: Double,
    @SerializedName("lastEarningsDate") val lastEarningsDate: String,
    @SerializedName("peRatio") val peRatio: Double
)

data class BasicData(
    @SerializedName("52WeekHigh") val week52High: Double,
    @SerializedName("52WeekLow") val week52Low: Double,
    @SerializedName("askMICId") val askMICId: String,
    @SerializedName("askPrice") val askPrice: Double,
    @SerializedName("askSize") val askSize: Int,
    @SerializedName("askTime") val askTime: Long,
    @SerializedName("bidMICId") val bidMICId: String,
    @SerializedName("bidPrice") val bidPrice: Double,
    @SerializedName("bidSize") val bidSize: Int,
    @SerializedName("bidTime") val bidTime: Long,
    @SerializedName("closePrice") val closePrice: Double,
    @SerializedName("highPrice") val highPrice: Double,
    @SerializedName("lastMICId") val lastMICId: String,
    @SerializedName("lastPrice") val lastPrice: Double,
    @SerializedName("lastSize") val lastSize: Int,
    @SerializedName("lowPrice") val lowPrice: Double,
    @SerializedName("mark") val mark: Double,
    @SerializedName("markChange") val markChange: Double,
    @SerializedName("markPercentChange") val markPercentChange: Double,
    @SerializedName("netChange") val netChange: Double,
    @SerializedName("netPercentChange") val netPercentChange: Double,
    @SerializedName("openPrice") val openPrice: Double,
    @SerializedName("postMarketChange") val postMarketChange: Double,
    @SerializedName("postMarketPercentChange") val postMarketPercentChange: Double,
    @SerializedName("quoteTime") val quoteTime: Long,
    @SerializedName("securityStatus") val securityStatus: String,
    @SerializedName("totalVolume") val totalVolume: Long,
    @SerializedName("tradeTime") val tradeTime: Long
)

data class Reference(
    @SerializedName("cusip") val cusip: String,
    @SerializedName("description") val description: String,
    @SerializedName("exchange") val exchange: String,
    @SerializedName("exchangeName") val exchangeName: String,
    @SerializedName("isHardToBorrow") val isHardToBorrow: Boolean,
    @SerializedName("isShortable") val isShortable: Boolean,
    @SerializedName("htbRate") val htbRate: Double
)

data class MinimumData(
    @SerializedName("regularMarketLastPrice") val regularMarketLastPrice: Double,
    @SerializedName("regularMarketLastSize") val regularMarketLastSize: Long,
    @SerializedName("regularMarketNetChange") val regularMarketNetChange: Double,
    @SerializedName("regularMarketPercentChange") val regularMarketPercentChange: Double,
    @SerializedName("regularMarketTradeTime") val regularMarketTradeTime: Long
)