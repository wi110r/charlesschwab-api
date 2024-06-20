package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses

import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.StockQuote
import com.google.gson.annotations.SerializedName

data class StockQuoteResponse(
    @SerializedName("assetMainType") val assetMainType: String?,
    @SerializedName("assetSubType") val assetSubType: String?,
    @SerializedName("quoteType") val quoteType: String?,
    @SerializedName("realtime") val realtime: Boolean?,
    @SerializedName("ssid") val ssid: Long?,
    @SerializedName("symbol") val symbol: String?,
    @SerializedName("fundamental") val fundamental: Fundamental,
    @SerializedName("quote") val basicData: BasicData,
    @SerializedName("reference") val reference: Reference,
    @SerializedName("regular") val regular: MinimumData
)

data class Fundamental(
    @SerializedName("avg10DaysVolume") val avg10DaysVolume: Long?,
    @SerializedName("avg1YearVolume") val avg1YearVolume: Long?,
    @SerializedName("divAmount") val divAmount: Double?,
    @SerializedName("divFreq") val divFreq: Int?,
    @SerializedName("divPayAmount") val divPayAmount: Double?,
    @SerializedName("divYield") val divYield: Double?,
    @SerializedName("eps") val eps: Double?,
    @SerializedName("fundLeverageFactor") val fundLeverageFactor: Double?,
    @SerializedName("lastEarningsDate") val lastEarningsDate: String?,
    @SerializedName("peRatio") val peRatio: Double?
)

data class BasicData(
    @SerializedName("52WeekHigh") val week52High: Double?,
    @SerializedName("52WeekLow") val week52Low: Double?,
    @SerializedName("askMICId") val askMICId: String?,
    @SerializedName("askPrice") val askPrice: Double?,
    @SerializedName("askSize") val askSize: Int?,
    @SerializedName("askTime") val askTime: Long?,
    @SerializedName("bidMICId") val bidMICId: String?,
    @SerializedName("bidPrice") val bidPrice: Double?,
    @SerializedName("bidSize") val bidSize: Int?,
    @SerializedName("bidTime") val bidTime: Long?,
    @SerializedName("closePrice") val closePrice: Double?,
    @SerializedName("highPrice") val highPrice: Double?,
    @SerializedName("lastMICId") val lastMICId: String?,
    @SerializedName("lastPrice") val lastPrice: Double?,
    @SerializedName("lastSize") val lastSize: Int?,
    @SerializedName("lowPrice") val lowPrice: Double?,
    @SerializedName("mark") val mark: Double?,
    @SerializedName("markChange") val markChange: Double?,
    @SerializedName("markPercentChange") val markPercentChange: Double?,
    @SerializedName("netChange") val netChange: Double?,
    @SerializedName("netPercentChange") val netPercentChange: Double?,
    @SerializedName("openPrice") val openPrice: Double?,
    @SerializedName("postMarketChange") val postMarketChange: Double?,
    @SerializedName("postMarketPercentChange") val postMarketPercentChange: Double?,
    @SerializedName("quoteTime") val quoteTime: Long?,
    @SerializedName("securityStatus") val securityStatus: String?,
    @SerializedName("totalVolume") val totalVolume: Long?,
    @SerializedName("tradeTime") val tradeTime: Long?
)

data class Reference(
    @SerializedName("cusip") val cusip: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("exchange") val exchange: String?,
    @SerializedName("exchangeName") val exchangeName: String?,
    @SerializedName("isHardToBorrow") val isHardToBorrow: Boolean?,
    @SerializedName("isShortable") val isShortable: Boolean?,
    @SerializedName("htbRate") val htbRate: Double?
)

data class MinimumData(
    @SerializedName("regularMarketLastPrice") val regularMarketLastPrice: Double?,
    @SerializedName("regularMarketLastSize") val regularMarketLastSize: Long?,
    @SerializedName("regularMarketNetChange") val regularMarketNetChange: Double?,
    @SerializedName("regularMarketPercentChange") val regularMarketPercentChange: Double?,
    @SerializedName("regularMarketTradeTime") val regularMarketTradeTime: Long?
)

fun StockQuoteResponse.toStockQuote(): StockQuote {
    return StockQuote(
        assetMainType = this.assetMainType ?: "Unknown",
        assetSubType = this.assetSubType ?: "Unknown",
        quoteType = this.quoteType ?: "Unknown",
        realtime = this.realtime ?: false,
        ssid = this.ssid ?: 0L,
        symbol = this.symbol ?: "Unknown",

        avg10DaysVolume = this.fundamental.avg10DaysVolume ?: 0L,
        avg1YearVolume = this.fundamental.avg1YearVolume ?: 0L,
        divAmount = this.fundamental.divAmount ?: 0.0,
        divFreq = this.fundamental.divFreq ?: 0,
        divPayAmount = this.fundamental.divPayAmount ?: 0.0,
        divYield = this.fundamental.divYield ?: 0.0,
        eps = this.fundamental.eps ?: 0.0,
        fundLeverageFactor = this.fundamental.fundLeverageFactor ?: 0.0,
        lastEarningsDate = this.fundamental.lastEarningsDate ?: "Unknown",
        peRatio = this.fundamental.peRatio ?: 0.0,

        week52High = this.basicData.week52High ?: 0.0,
        week52Low = this.basicData.week52Low ?: 0.0,
        askMICId = this.basicData.askMICId ?: "Unknown",
        askPrice = this.basicData.askPrice ?: 0.0,
        askSize = this.basicData.askSize ?: 0,
        askTime = this.basicData.askTime ?: 0L,
        bidMICId = this.basicData.bidMICId ?: "Unknown",
        bidPrice = this.basicData.bidPrice ?: 0.0,
        bidSize = this.basicData.bidSize ?: 0,
        bidTime = this.basicData.bidTime ?: 0L,
        closePrice = this.basicData.closePrice ?: 0.0,
        highPrice = this.basicData.highPrice ?: 0.0,
        lastMICId = this.basicData.lastMICId ?: "Unknown",
        lastPrice = this.basicData.lastPrice ?: 0.0,
        lastSize = this.basicData.lastSize ?: 0,
        lowPrice = this.basicData.lowPrice ?: 0.0,
        mark = this.basicData.mark ?: 0.0,
        markChange = this.basicData.markChange ?: 0.0,
        markPercentChange = this.basicData.markPercentChange ?: 0.0,
        netChange = this.basicData.netChange ?: 0.0,
        netPercentChange = this.basicData.netPercentChange ?: 0.0,
        openPrice = this.basicData.openPrice ?: 0.0,
        postMarketChange = this.basicData.postMarketChange ?: 0.0,
        postMarketPercentChange = this.basicData.postMarketPercentChange ?: 0.0,
        quoteTime = this.basicData.quoteTime ?: 0L,
        securityStatus = this.basicData.securityStatus ?: "Unknown",
        totalVolume = this.basicData.totalVolume ?: 0L,
        tradeTime = this.basicData.tradeTime ?: 0L,

        cusip = this.reference.cusip ?: "Unknown",
        description = this.reference.description ?: "Unknown",
        exchange = this.reference.exchange ?: "Unknown",
        exchangeName = this.reference.exchangeName ?: "Unknown",
        isHardToBorrow = this.reference.isHardToBorrow ?: false,
        isShortable = this.reference.isShortable ?: false,
        htbRate = this.reference.htbRate ?: 0.0,

        regularMarketLastPrice = this.regular.regularMarketLastPrice ?: 0.0,
        regularMarketLastSize = this.regular.regularMarketLastSize ?: 0L,
        regularMarketNetChange = this.regular.regularMarketNetChange ?: 0.0,
        regularMarketPercentChange = this.regular.regularMarketPercentChange ?: 0.0,
        regularMarketTradeTime = this.regular.regularMarketTradeTime ?: 0L
    )
}

// TODO DELETE ME -- Old with no null conversion
//fun QuoteResponse.convertToQuote(): StockQuote {
//    return StockQuote(
//        assetMainType = this.assetMainType,
//        assetSubType = this.assetSubType,
//        quoteType = this.quoteType,
//        realtime = this.realtime,
//        ssid = this.ssid,
//        symbol = this.symbol,
//
//        avg10DaysVolume = this.fundamental.avg10DaysVolume,
//        avg1YearVolume = this.fundamental.avg1YearVolume,
//        divAmount = this.fundamental.divAmount,
//        divFreq = this.fundamental.divFreq,
//        divPayAmount = this.fundamental.divPayAmount,
//        divYield = this.fundamental.divYield,
//        eps = this.fundamental.eps,
//        fundLeverageFactor = this.fundamental.fundLeverageFactor,
//        lastEarningsDate = this.fundamental.lastEarningsDate,
//        peRatio = this.fundamental.peRatio,
//
//        week52High = this.basicData.week52High,
//        week52Low = this.basicData.week52Low,
//        askMICId = this.basicData.askMICId,
//        askPrice = this.basicData.askPrice,
//        askSize = this.basicData.askSize,
//        askTime = this.basicData.askTime,
//        bidMICId = this.basicData.bidMICId,
//        bidPrice = this.basicData.bidPrice,
//        bidSize = this.basicData.bidSize,
//        bidTime = this.basicData.bidTime,
//        closePrice = this.basicData.closePrice,
//        highPrice = this.basicData.highPrice,
//        lastMICId = this.basicData.lastMICId,
//        lastPrice = this.basicData.lastPrice,
//        lastSize = this.basicData.lastSize,
//        lowPrice = this.basicData.lowPrice,
//        mark = this.basicData.mark,
//        markChange = this.basicData.markChange,
//        markPercentChange = this.basicData.markPercentChange,
//        netChange = this.basicData.netChange,
//        netPercentChange = this.basicData.netPercentChange,
//        openPrice = this.basicData.openPrice,
//        postMarketChange = this.basicData.postMarketChange,
//        postMarketPercentChange = this.basicData.postMarketPercentChange,
//        quoteTime = this.basicData.quoteTime,
//        securityStatus = this.basicData.securityStatus,
//        totalVolume = this.basicData.totalVolume,
//        tradeTime = this.basicData.tradeTime,
//
//        cusip = this.reference.cusip,
//        description = this.reference.description,
//        exchange = this.reference.exchange,
//        exchangeName = this.reference.exchangeName,
//        isHardToBorrow = this.reference.isHardToBorrow,
//        isShortable = this.reference.isShortable,
//        htbRate = this.reference.htbRate,
//
//        regularMarketLastPrice = this.regular.regularMarketLastPrice,
//        regularMarketLastSize = this.regular.regularMarketLastSize,
//        regularMarketNetChange = this.regular.regularMarketNetChange,
//        regularMarketPercentChange = this.regular.regularMarketPercentChange,
//        regularMarketTradeTime = this.regular.regularMarketTradeTime
//    )
//}