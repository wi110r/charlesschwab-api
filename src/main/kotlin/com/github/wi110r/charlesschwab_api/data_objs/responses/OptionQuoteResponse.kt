package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses

import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.OptionQuote

data class OptionQuoteResp(
    val assetMainType: String,
    val realtime: Boolean,
    val ssid: Long,
    val symbol: String,
    val quote: OptionQuoteRespData,
    val reference: Ref
)

data class OptionQuoteRespData(
    val weekHigh52: Double,
    val weekLow52: Double,
    val askPrice: Double,
    val askSize: Int,
    val bidPrice: Double,
    val bidSize: Int,
    val closePrice: Double,
    val delta: Double,
    val gamma: Double,
    val highPrice: Double,
    val indAskPrice: Double,
    val indBidPrice: Double,
    val indQuoteTime: Long,
    val impliedYield: Double,
    val lastPrice: Double,
    val lastSize: Int,
    val lowPrice: Double,
    val mark: Double,
    val markChange: Double,
    val markPercentChange: Double,
    val moneyIntrinsicValue: Double,
    val netChange: Double,
    val netPercentChange: Double,
    val openInterest: Int,
    val openPrice: Double,
    val quoteTime: Long,
    val rho: Double,
    val securityStatus: String,
    val theoreticalOptionValue: Double,
    val theta: Double,
    val timeValue: Double,
    val totalVolume: Int,
    val tradeTime: Long,
    val underlyingPrice: Double,
    val vega: Double,
    val volatility: Double
)

data class Ref(
    val contractType: String,
    val daysToExpiration: Int,
    val deliverables: String,
    val description: String,
    val exchange: String,
    val exchangeName: String,
    val exerciseType: String,
    val expirationDay: Int,
    val expirationMonth: Int,
    val expirationType: String,
    val expirationYear: Int,
    val isPennyPilot: Boolean,
    val lastTradingDay: Long,
    val multiplier: Double,
    val settlementType: String,
    val strikePrice: Double,
    val underlying: String
)

fun OptionQuoteResp.convertToOptionQuote(): OptionQuote {
    return OptionQuote(
        assetMainType = this.assetMainType,
        realtime = this.realtime,
        ssid = this.ssid,
        symbol = this.symbol,

        weekHigh52 = this.quote.weekHigh52,
        weekLow52 = this.quote.weekLow52,
        askPrice = this.quote.askPrice,
        askSize = this.quote.askSize,
        bidPrice = this.quote.bidPrice,
        bidSize = this.quote.bidSize,
        closePrice = this.quote.closePrice,
        delta = this.quote.delta,
        gamma = this.quote.gamma,
        highPrice = this.quote.highPrice,
        indAskPrice = this.quote.indAskPrice,
        indBidPrice = this.quote.indBidPrice,
        indQuoteTime = this.quote.indQuoteTime,
        impliedYield = this.quote.impliedYield,
        lastPrice = this.quote.lastPrice,
        lastSize = this.quote.lastSize,
        lowPrice = this.quote.lowPrice,
        mark = this.quote.mark,
        markChange = this.quote.markChange,
        markPercentChange = this.quote.markPercentChange,
        moneyIntrinsicValue = this.quote.moneyIntrinsicValue,
        netChange = this.quote.netChange,
        netPercentChange = this.quote.netPercentChange,
        openInterest = this.quote.openInterest,
        openPrice = this.quote.openPrice,
        quoteTime = this.quote.quoteTime,
        rho = this.quote.rho,
        securityStatus = this.quote.securityStatus,
        theoreticalOptionValue = this.quote.theoreticalOptionValue,
        theta = this.quote.theta,
        timeValue = this.quote.timeValue,
        totalVolume = this.quote.totalVolume,
        tradeTime = this.quote.tradeTime,
        underlyingPrice = this.quote.underlyingPrice,
        vega = this.quote.vega,
        volatility = this.quote.volatility,

        contractType = this.reference.contractType,
        daysToExpiration = this.reference.daysToExpiration,
        deliverables = this.reference.deliverables,
        description = this.reference.description,
        exchange = this.reference.exchange,
        exchangeName = this.reference.exchangeName,
        exerciseType = this.reference.exerciseType,
        expirationDay = this.reference.expirationDay,
        expirationMonth = this.reference.expirationMonth,
        expirationType = this.reference.expirationType,
        expirationYear = this.reference.expirationYear,
        isPennyPilot = this.reference.isPennyPilot,
        lastTradingDay = this.reference.lastTradingDay,
        multiplier = this.reference.multiplier,
        settlementType = this.reference.settlementType,
        strikePrice = this.reference.strikePrice,
        underlying = this.reference.underlying
    )
}