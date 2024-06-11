package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs

import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses.Option
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses.OptionChainResponse
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses.Underlying

data class OptionChain(
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
    val callExpDateMap: Map<String, Map<String, Option>>,         // TODO should not be List<Option> just Option
    val putExpDateMap: Map<String, Map<String, Option>>
)


