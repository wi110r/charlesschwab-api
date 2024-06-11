package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses

import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.OptionChain

data class OptionChainResponse(
    val symbol: String,
    val status: String,
    val underlying: Underlying,
    val strategy: String,
    val interval: Double,
    val isDelayed: Boolean,
    val isIndex: Boolean,
    val interestRate: Double,
    val underlyingPrice: Double,
    val volatility: Double,
    val daysToExpiration: Double,
    val numberOfContracts: Int,
    val assetMainType: String,
    val assetSubType: String,
    val isChainTruncated: Boolean,
    val callExpDateMap: Map<String, Map<String, List<Option>>>,         // TODO should not be List<Option> just Option
    val putExpDateMap: Map<String, Map<String, List<Option>>>
)

data class Underlying(
    val symbol: String,
    val description: String,
    val change: Double,
    val percentChange: Double,
    val close: Double,
    val quoteTime: Long,
    val tradeTime: Long,
    val bid: Double,
    val ask: Double,
    val last: Double,
    val mark: Double,
    val markChange: Double,
    val markPercentChange: Double,
    val bidSize: Int,
    val askSize: Int,
    val highPrice: Double,
    val lowPrice: Double,
    val openPrice: Double,
    val totalVolume: Int,
    val exchangeName: String,
    val fiftyTwoWeekHigh: Double,
    val fiftyTwoWeekLow: Double,
    val delayed: Boolean
)

data class Option(
    val putCall: String,
    val symbol: String,
    val description: String,
    val exchangeName: String,
    val bid: Double,
    val ask: Double,
    val last: Double,
    val mark: Double,
    val bidSize: Int,
    val askSize: Int,
    val bidAskSize: String,
    val lastSize: Int,
    val highPrice: Double,
    val lowPrice: Double,
    val openPrice: Double,
    val closePrice: Double,
    val totalVolume: Int,
    val tradeTimeInLong: Long,
    val quoteTimeInLong: Long,
    val netChange: Double,
    val volatility: Double,
    val delta: Double,
    val gamma: Double,
    val theta: Double,
    val vega: Double,
    val rho: Double,
    val openInterest: Int,
    val timeValue: Double,
    val theoreticalOptionValue: Double,
    val theoreticalVolatility: Double,
    val optionDeliverablesList: List<OptionDeliverable>,
    val strikePrice: Double,
    val expirationDate: String,
    val daysToExpiration: Int,
    val expirationType: String,
    val lastTradingDay: Long,
    val multiplier: Double,
    val settlementType: String,
    val deliverableNote: String,
    val percentChange: Double,
    val markChange: Double,
    val markPercentChange: Double,
    val intrinsicValue: Double,
    val extrinsicValue: Double,
    val optionRoot: String,
    val exerciseType: String,
    val high52Week: Double,
    val low52Week: Double,
    val nonStandard: Boolean,
    val pennyPilot: Boolean,
    val inTheMoney: Boolean,
    val mini: Boolean
)

data class OptionDeliverable(
    val symbol: String,
    val assetType: String,
    val deliverableUnits: Double
)

fun OptionChainResponse.convertToOptionChain(
    cMap: Map<String, Map<String, Option>>,
    pMap: Map<String, Map<String, Option>>): OptionChain {
    return OptionChain(
        symbol = this.symbol,
        status = this.status,
        underlying = this.underlying,
        strategy = this.strategy,
        interval = this.interval,
        isDelayed = this.isDelayed,
        isIndex = this.isIndex,
        interestRate = this.interestRate,
        underlyingPrice = this.underlyingPrice,
        volatility = this.volatility,
        daysToExpiration = this.daysToExpiration,
        numberOfContracts = this.numberOfContracts,
        assetMainType = this.assetMainType,
        assetSubType = this.assetSubType,
        isChainTruncated = this.isChainTruncated,
        callExpDateMap = cMap,
        putExpDateMap = pMap
    )
}