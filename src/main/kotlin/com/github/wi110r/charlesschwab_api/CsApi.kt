package com.github.wi110r.charlesschwab_api

import com.github.wi110r.charlesschwab_api.data_objs.auth.Authorization
import com.github.wi110r.com.github.wi110r.charlesschwab_api.auth.responses.AccessTokenResponse
import com.github.wi110r.com.github.wi110r.charlesschwab_api.auth.responses.RefreshTokenResponse
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.OptionChain
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.OptionQuote
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.StockQuote
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.TopStockLists
import com.github.wi110r.charlesschwab_api.data_objs.responses.AccountNumbersResponse
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses.*
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.stockchart.StockChartCSBasic
import com.github.wi110r.com.github.wi110r.charlesschwab_api.tools.*
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.Request
import java.text.DecimalFormat
import java.util.*
import kotlin.system.exitProcess


class CsApi private constructor(
    appKey: String,
    appSecret: String,
    authSavePath: String? = null,
) {
    private val threadLockAccessToken = Any()
    private var auth: Authorization
    private lateinit var topStockLists: TopStockLists
    private val account_base_endpoint = "https://api.schwabapi.com/trader/v1"
    private val market_data_base_endpoint = "https://api.schwabapi.com/marketdata/v1"
    private val auth_base_endpoint = "https://api.schwabapi.com/v1/oauth"
    private val authPath: String





    init {

        authPath = authSavePath ?: "csApi_auth.json"
        // Try to load auth keys
        auth = initAuthJson(appKey, appSecret)
        // Check status of Refresh Token
        init_check_refresh_token()
        loadTopStocksList()
    }


    private fun initAuthJson(key: String, secret: String): Authorization {
        // Try to load
        try {
            val json = FileHelper.readFileToString(authPath)
            return gson.fromJson(json, Authorization::class.java)
        } catch (e: Exception) {
            println("\n#############################################################################################")
            println("\nWarning -- No Auth File Found. New Auth Created. Please Login.\n")
            println("#############################################################################################\n")
            val a = Authorization(
                key, secret
            )
            FileHelper.writeFile(authPath, gson.toJson(a))
            return a
        }
    }


    /** Checks the status of the Refresh token. Notifies how many days are left until Refresh token expires. */
    private fun init_check_refresh_token() {

        // Check for refresh token expiry
        val timeUntilExpiry = (auth.refreshTokenExpiryInMs - System.currentTimeMillis()).toDouble()
        if (timeUntilExpiry > 0) {
//                val daysTilExpiry = DecimalFormat("#.###").format((timeUntilExpiry / 86_400_000))
            val daysTilExpiry = (timeUntilExpiry / (86_400_000).toDouble())
            val printable = DecimalFormat("#.##").format(daysTilExpiry)
            println("\n#############################################################################################")
            println("\nWarning -- Refresh Token Expires in: $printable days.\n")
            println("#############################################################################################\n")
        } else {
            println("\n#############################################################################################")
            println("\nWarning -- Refresh Token is EXPIRED.\nPlease use CSAuth.login() to update.\n")
            println("#############################################################################################\n")
        }
    }


    private fun loadTopStocksList() {
//        val l = FileHelper.readFileToString("src/main/resources/top_stock_lists.json")
        val l = FileHelper.loadResourceToString("top_stock_lists.json")

        topStockLists = gson.fromJson(l, TopStockLists::class.java)
    }


    /** Peforms the login required to obtain Refresh Token. Refresh Token expires every 7 days. */
    fun login() {
        // Build login url
        val url = "$auth_base_endpoint/authorize?client_id=key_here&redirect_uri=https://127.0.0.1"
            .replace("key_here", auth.key)

        // Get input from user + extract code
        print("Please login to Charles Schwab using this url, then paste the final url below...\n\n$url\n\n>>>: ")
        val code_url = readLine()
        if (!code_url!!.contains("https://127.0.0.1/?code=")) {
            throw Exception("Something Went Wrong! No 'code=' found in url")
        }
        val auth_code = code_url.substringAfter("?code=").substringBeforeLast("%40&") + "@"

        // Create form body
        val formBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", auth_code)
            .add("redirect_uri", "https://127.0.0.1")
            .build()

        // Create Headers
        val base64Credentials = Base64.getEncoder().encodeToString("${auth.key}:${auth.secret}".toByteArray())
        val req = Request.Builder()
            .url(auth_base_endpoint + "/token")
            .post(formBody)
            .addHeader("Authorization", "Basic $base64Credentials")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        // Make request
        val response = NetworkClient.getClient().newCall(req).execute()
        if (response.isSuccessful) {

            // Read response and convert to usable data class with expiration time in Ms
            val tokenResponse = gson.fromJson(response.body?.string(), RefreshTokenResponse::class.java)

            // A-Token expires in 30m. Minus 1min for time safety
            val accessTokenExpiry = System.currentTimeMillis() + 1_800_000 - 60_000
            // R-Token expires in 7days. Minus 1hour for time safety
            val refreshTokenExpiry = System.currentTimeMillis() + 604_800_000 - 3_600_000

            auth = Authorization(
                auth.key,
                auth.secret,
                accountNumber = "",
                accountNumberHashValue =  "",
                refresh_token = tokenResponse.refresh_token,
                access_token = tokenResponse.access_token,
                id_token = tokenResponse.id_token,
                accessTokenExpiryInMs = accessTokenExpiry,
                refreshTokenExpiryInMs = refreshTokenExpiry
            )


            println("Tokens Acquired...Now fetching Account Numbers...")
            var attempts = 0
            var actKeys: AccountNumbersResponse? = null
            while (attempts != 5) {
                actKeys = getAccountNumbers()
                if (actKeys != null){
                    break
                }
                attempts += 1
            }
            if (actKeys == null){
                println("Failed to get account number keys. Please login() again")
                exitProcess(0)
            }

            println("Account Numbers retrieved successfully.")

            val updatedAuth = Authorization(
                auth.key,
                auth.secret,
                accountNumber = actKeys.accountNumber,
                accountNumberHashValue =  actKeys.hashValue,
                refresh_token = tokenResponse.refresh_token,
                access_token = tokenResponse.access_token,
                id_token = tokenResponse.id_token,
                accessTokenExpiryInMs = accessTokenExpiry,
                refreshTokenExpiryInMs = refreshTokenExpiry
            )
            auth = updatedAuth

            // Save to file
            FileHelper.writeFile(authPath, gson.toJson(updatedAuth))

        } else {
            Log.w("${CsApi::class.java.simpleName}.login()", "Login Failed.")
            throw Exception("Request failed: ${response.code}\n${response.message} \n${response.body?.string()}")
        }

    }


    /** Returns Access Token. Will update the Access token if needed using a valid Refresh token */
    private fun getAccessToken(): String? {

        synchronized(threadLockAccessToken){
            try {
                val rtoken = auth.refresh_token
                val rexpiry = auth.refreshTokenExpiryInMs
                val atoken = auth.access_token
                val aexpiry = auth.accessTokenExpiryInMs

                // Check if refresh token has expired
                if (rexpiry < System.currentTimeMillis()) {
                    println("\nWarning -- Refresh Token has expired. Please use function CSAuth().login() to update.")
                    exitProcess(0)
                }

                // Check if access token needs to be updated, if not return access token
                if (aexpiry > System.currentTimeMillis()) {
                    return atoken
                }

                // Use refresh token to update access token
                val postBody = FormBody.Builder()
                    .add("grant_type", "refresh_token")
                    .add("refresh_token", rtoken)
                    .build()

                val base64Credentials = Base64.getEncoder().encodeToString("${auth.key}:${auth.secret}".toByteArray())
                val request = Request.Builder()
                    .url(auth_base_endpoint + "/token")
                    .post(postBody)
                    .addHeader("Authorization", "Basic $base64Credentials")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build()

                val requestCall = NetworkClient.getClient().newCall(request).execute()

                if (requestCall.isSuccessful) {
                    val body = gson.fromJson(requestCall.body?.string(), AccessTokenResponse::class.java)
                    val newAccessExpiry = System.currentTimeMillis() + 1_800_000 - 60_000       // Minus 1min for time safety
                    val newAuth = Authorization(
                        auth.key,
                        auth.secret,
                        auth.accountNumber,
                        auth.accountNumberHashValue,
                        auth.refresh_token,
                        body.access_token,
                        body.id_token,
                        newAccessExpiry,
                        auth.refreshTokenExpiryInMs
                    )
                    val js = gson.toJson(newAuth)
                    FileHelper.writeFile(authPath, js)
                    auth = newAuth
                    return auth.access_token
                }
                else {
                    Log.w("getAccessToken()", "Failed Response: ${requestCall.body?.string()}")
                    return null
                }
            } catch (e: Exception){
                Log.w("getAccessToken()", "Failed Response: ${e.message}")
                e.printStackTrace()
                return null
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //    Market Data Start


    fun getTopStocks(): TopStockLists {
        return topStockLists
    }


    private fun getQuote(symbol: String): String? {

        try {
            val token = getAccessToken()
            val s = symbol.uppercase()
            val req = Request.Builder()
                .header("Authorization", "Bearer $token")
                .header("accept", "application/json")
                .get()
                .url(market_data_base_endpoint + "/${s}/quotes")
                .build()
            val resp = NetworkClient.getClient().newCall(req).execute()
            if (resp.isSuccessful) {
                return resp.body?.string()

            } else {
                Log.w("getQuote()", "Response not Successful. Code: ${resp.code}. Message: ${resp.message}\n" +
                        "Body: ${resp.body}")
                return null
            }
        } catch (e: Exception){
            Log.w("getQuote()", "Failed Response. ${e.message}")
            return null
        }
    }


    fun getStockQuote(symbol: String): StockQuote? {
        try {
            val s = symbol.uppercase()
            val body = getQuote(s)
            println(body)
            val jsonObject = gson.fromJson(body, Map::class.java)
            val assetJson = gson.toJson(jsonObject[s])
            val asset = gson.fromJson(assetJson, StockQuoteResponse::class.java)
            return asset.toStockQuote()
        } catch (e: Exception) {
            Log.w("getStockQuote()", "Failed Response: ${e.message}")
            return null
        }
    }


    /** Gets quote for option symbol.
     * Note: All prices are x100
     * */
    fun getOptionQuote(symbol: String): OptionQuote? {
        try {
            val s = symbol.uppercase()
            val body = getQuote(s)
            val jsonObject = gson.fromJson(body, Map::class.java)
            val assetJson = gson.toJson(jsonObject[s])
            val asset = gson.fromJson(assetJson, OptionQuoteResp::class.java).toOptionQuote()
            return asset
        }catch (e: Exception) {
            Log.w("getOptionQuote()", "Failed Response: ${e.message}")
            return null
        }
    }


    /** Gets option chain for symbol.
     * Note: All prices are x100
     * */
    fun getOptionChain(
        symbol: String,
        contractType: String? = "ALL",
        strikeCount: Int? = 5,      // ?
        includeUnderlyingQuote: Boolean? = true,
        range: String? = "NTM",     // ?
        strike: Double? = null,
        fromDate: Long? = null,
        toDate: Long? = null,
    ) : OptionChain? {
        try {
            val fDate = if (fromDate == null)
                convertTimestampToDateyyyyMMdd(System.currentTimeMillis())
            else convertTimestampToDateyyyyMMdd(fromDate)

            val tDate = if (toDate == null)                                         // + 1 week
                convertTimestampToDateyyyyMMdd(System.currentTimeMillis() + 604_800_000L)
            else convertTimestampToDateyyyyMMdd(toDate)

            val params = mutableListOf<String>()
            params.add("symbol=${symbol.uppercase()}")
            contractType?.let { params.add("contractType=$it") }
            strikeCount?.let { params.add("strikeCount=$it") }
            includeUnderlyingQuote?.let { params.add("includeUnderlyingQuote=$it") }
            strike?.let { params.add("strike=$it") }
            range?.let { params.add("range=$it") }
            fromDate?.let { params.add("fromDate=${fDate }") }
            toDate?.let { params.add("toDate=${tDate}") }

            val url = "$market_data_base_endpoint/chains?${params.joinToString("&")}"
            val token = getAccessToken()
            val req = Request.Builder()
                .header("Authorization", "Bearer $token")
                .header("accept", "application/json")
                .get()
                .url(url)
                .build()
            val resp = NetworkClient.getClient().newCall(req).execute()
            if (resp.isSuccessful) {
                val body = resp.body?.string()
                val ocr = gson.fromJson(body, OptionChainResponse::class.java)
                val n = ocr.convertToOptionChain()
                return n

                // TODO DELETE ME
//                val cExpiryMap = mutableMapOf<String, Map<String, Option>>()
//                val pExpiryMap = mutableMapOf<String, Map<String, Option>>()
//                for (date in ocr.callExpDateMap.keys){
//                    val callStrikeMapOrig = ocr.callExpDateMap.get(date)!!
//                    val putStrikeMapOrig = ocr.putExpDateMap.get(date)!!
//                    val csf = mutableMapOf<String, Option>()
//                    val psf = mutableMapOf<String, Option>()
//                    for (s in callStrikeMapOrig.keys) {
//                        csf.put(s, callStrikeMapOrig[s]!!.first())
//                        psf.put(s, putStrikeMapOrig[s]!!.first())
//                    }
//                    cExpiryMap.put(date, csf)
//                    pExpiryMap.put(date, psf)
//                }
//                return ocr.convertToOptionChain()
            }
            else {
                Log.w("getOptionChain", "Request Failed, MSG:\t" + resp.body?.string())
                return null
            }
        } catch (e: Exception){
            Log.w("getOptionChain", "Request Failed. ${e.message}")
            return null
        }
    }





    private fun getHistoricData(
        symbol: String,
        periodType: String = "day",     // day, month, year, ytd
        period: Int = 10,
        frequencyType: String = "minute",
        frequency: Int = 5,
        startDate: Long? = null,        // not needed
        endDate: Long? = null,
        needExtendedHoursData: Boolean = true

    ): StockChartCSBasic? {
        try {

            val endpoint = market_data_base_endpoint + "/pricehistory"

            val end = endDate ?: System.currentTimeMillis()
            val params = mutableListOf<String>()
            params.add("symbol" + "=" + symbol)
            params.add("periodType" + "=" + periodType)
            params.add("period" + "=" + period.toString())
            params.add("frequencyType" + "=" + frequencyType)
            params.add("frequency" + "=" + frequency.toString())
            params.add("endDate" + "=" + end.toString())
            params.add("needExtendedHoursData" + "=" + needExtendedHoursData.toString())
            if (startDate != null) params.add("startDate=$startDate")

            val url = endpoint + "?" + params.joinToString("&")
            val token = getAccessToken()
            val req = Request.Builder()
                .header("Authorization", "Bearer $token")
                .header("accept", "application/json")
                .get()
                .url(url)
                .build()
            val resp = NetworkClient.getClient().newCall(req).execute()
            if (resp.isSuccessful) {
                val body = resp.body?.string()
                val chartResp = gson.fromJson(body, ChartResponse::class.java)
                val timeInterval = "$frequency${frequencyType.get(0)}"
                val periodRange = "$period${periodType.get(0)}"
                return chartResp.convertToStockChart(timeInterval, periodRange)

            } else {
                Log.w("getHistoricData()", "Failed Response. ${resp.body?.string()}")
            }
        } catch (e: Exception) {
            Log.w("getHistoricData()", "Failed Response. Null")
        }
        return null
    }


    /** Fetch a current Stock Chart with 1 minute candles.
     * First candle of day is 9:30am.
     * @param symbol Stock Symbol.
     * @param days Valid values are 1, 2, 3, 4, 5, 10.
     * @param prepost Include extended hours.
     * */
    fun getHistoricData1min(symbol: String, days: Int, prepost: Boolean): StockChartCSBasic? {
        return getHistoricData(
            symbol = symbol,
            periodType = "day",
            period = days,
            frequencyType = "minute",
            frequency = 1,
            endDate = System.currentTimeMillis(),
            needExtendedHoursData = prepost
            )
    }


    /** Fetch a current Stock Chart with 5 minute candles.
     * First candle of day is 9:30am.
     * @param symbol Stock Symbol.
     * @param days Valid values are 1, 2, 3, 4, 5, 10.
     * @param prepost Include extended hours.
     * */
    fun getHistoricData5min(symbol: String, days: Int, prepost: Boolean): StockChartCSBasic? {
        return getHistoricData(
            symbol = symbol,
            periodType = "day",
            period = days,
            frequencyType = "minute",
            frequency = 5,
            endDate = System.currentTimeMillis(),
            needExtendedHoursData = prepost
        )
    }


    /** Fetch a current Stock Chart with 15 minute candles.
     * First candle of day is 9:30am.
     * @param symbol Stock Symbol.
     * @param days Valid values are 1, 2, 3, 4, 5, 10.
     * @param prepost Include extended hours.
     * */
    fun getHistoricData15min(symbol: String, days: Int, prepost: Boolean): StockChartCSBasic? {
        return getHistoricData(
            symbol = symbol,
            periodType = "day",
            period = days,
            frequencyType = "minute",
            frequency = 15,
            endDate = System.currentTimeMillis(),
            needExtendedHoursData = prepost

        )
    }


    /** Fetch a current Stock Chart with 30 minute candles.
     * First candle of day is 9:30am.
     * @param symbol Stock Symbol.
     * @param days  Valid values are 1, 2, 3, 4, 5, 10.
     * @param prepost Include extended hours.
     * */
    fun getHistoricData30min(symbol: String, days: Int, prepost: Boolean): StockChartCSBasic? {
        return getHistoricData(
            symbol = symbol,
            periodType = "day",
            period = days,
            frequencyType = "minute",
            frequency = 30,
            endDate = System.currentTimeMillis(),
            needExtendedHoursData = prepost
        )
    }


    /** Fetch a current Stock Chart with 1 day candles.
     * First candle of day is 1:00am.
     * @param symbol Stock Symbol.
     * @param months Valid values are 1, 2, 3, 6.
     * @param prepost Include extended hours.
     * */
    fun getHistoricData1day(symbol: String, months: Int, prepost: Boolean): StockChartCSBasic? {
        return getHistoricData(
            symbol = symbol,
            periodType = "month",
            period = months,
            frequencyType = "daily",
            frequency = 1,
            endDate = System.currentTimeMillis(),
            needExtendedHoursData = prepost
        )
    }


    /** Fetch a current Stock Chart with 1 week candles.
     * First candle of day is 1:00am.
     * @param symbol Stock Symbol aka. Ticker.
     * @param years Valid values are 1, 2, 3, 5, 10
     * @param prepost Include extended hours.
     * */
    fun getHistoricData1week(symbol: String, years: Int, prepost: Boolean): StockChartCSBasic? {
        return getHistoricData(
            symbol = symbol,
            periodType = "year",
            period = years,
            frequencyType = "weekly",
            frequency = 1,
            endDate = System.currentTimeMillis(),
            needExtendedHoursData = prepost
        )
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //    Account Data Start

    private fun getAccountNumbers(): AccountNumbersResponse?{
        val at = getAccessToken()
        val req = Request.Builder()
            .get()
            .url(account_base_endpoint + "/accounts/accountNumbers")
            .header("Authorization", "Bearer $at")
            .header("accept", "application/json")
            .build()

        val resp = NetworkClient.getClient().newCall(req).execute()
        if (resp.isSuccessful) {
            val body = resp.body?.string()
            val accountListType = object : TypeToken<List<AccountNumbersResponse>>() {}.type
            val accountKeys = gson.fromJson<List<AccountNumbersResponse>?>(body, accountListType).get(0)

            return accountKeys
        } else {
            return null
        }
    }


    fun test() {
        val x = getHistoricData("SPY")
        println(x?.periodSize)
        println(x?.candleSize)
    }

    companion object {
        @Volatile private var instance: CsApi? = null
        private var path: String? = null

        fun getInstance(): CsApi {
            if (instance != null) {
                return instance!!
            }
            else {
                println("CsApi() Has not been built yet. Please call CsApi.buildApi() with " +
                        "App-Key & App-Secret.")
                exitProcess(0)
            }
        }

        fun buildApi(
            appKey: String,
            appSecret: String,
            savePath: String? = null
        ): CsApi {
            if (instance == null){
                instance = CsApi(appKey, appSecret, savePath)
                return instance!!
            } else {
                println("CsApi() Has already been built with Auth JSON Path set to: $path")
                return instance!!
            }
        }
    }
}


