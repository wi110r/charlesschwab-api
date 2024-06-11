package com.github.wi110r.com.github.wi110r.charlesschwab_api

import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.auth.AccountKeys
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.auth.AuthKeys
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.auth.Tokens
import com.github.wi110r.com.github.wi110r.charlesschwab_api.auth.responses.AccessTokenResponse
import com.github.wi110r.com.github.wi110r.charlesschwab_api.auth.responses.RefreshTokenResponse
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.OptionChain
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.Quote
import com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.responses.*
import com.github.wi110r.com.github.wi110r.charlesschwab_api.tools.*
import com.github.wi110r.com.github.wi110r.charlesschwab_api.tools.Log
import com.github.wi110r.com.github.wi110r.charlesschwab_api.tools.NetworkClient
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.Request
import java.text.DecimalFormat
import java.util.*
import kotlin.system.exitProcess

object CharlesSchwabApi {

    private val authKeys: AuthKeys
    private var accountKeys: AccountKeys? = null
    private var tokens: Tokens? = null
    private val account_base_endpoint = "https://api.schwabapi.com/trader/v1"
    private val market_data_base_endpoint = "https://api.schwabapi.com/marketdata/v1"
    private val auth_base_endpoint = "https://api.schwabapi.com/v1/oauth"

    init {
        // Try to load auth keys
        try {
            authKeys = gson.fromJson(FileHelper.readFileToString(AUTH_JSON), AuthKeys::class.java)
        } catch (e: Exception) {
            println(
                "\nWARNING -- 'resources\\auth.json' not found. This file is needed for the APP KEY and APP SECRET codes.\n" +
                        "Please create a JSON file in the 'resources' folder which contains the following key value pairs:\n" +
                        "'key' : 'your app key'\n" +
                        "'secret' : 'your app secret'\n" +
                        "The 'key' and 'secret' can be found on the Charles Schwab Api webpage of your app."
            )
            exitProcess(0)
        }
        // Check status of Refresh Token
        init_check_refresh_token()

        // Check status of account keys
        initCheckAccountKeys()
    }

    /** Checks the status of the Refresh token. Notifies how many days are left until Refresh token expires. */
    private fun init_check_refresh_token() {

        // Try to load tokens. Print message on failure.
        try {
            tokens = gson.fromJson(FileHelper.readFileToString(TOKENS_JSON), Tokens::class.java)
        } catch (e: Exception) {
            println(
                "\nWarning -- 'resources\\tokens.json' not found. This file is needed to update the tokens.\n" +
                        "Please use function CSAuth().login() and follow the steps to create the file.\n" +
                        "This will have to be done once per week to maintain valid Refresh Token"
            )
        }

        // Check for refresh token expiry
        if (tokens != null) {
            val timeUntilExpiry = (tokens!!.refreshTokenExpiryInMs - System.currentTimeMillis()).toDouble()
            if (timeUntilExpiry > 0) {
//                val daysTilExpiry = DecimalFormat("#.###").format((timeUntilExpiry / 86_400_000))
                val daysTilExpiry = (timeUntilExpiry / (86_400_000).toDouble())
                val printable = DecimalFormat("#.##").format(daysTilExpiry)
                println("#############################################################################################")
                println("\nWarning -- Refresh Token Expires in: $printable days.\n")
                println("#############################################################################################")
            } else {
                println("#############################################################################################")
                println("\nWarning -- Refresh Token is EXPIRED.\nPlease use CSAuth.login() to update.\n")
                println("#############################################################################################")
            }
        }
    }

    /** Loads the Account Keys for user */
    private fun initCheckAccountKeys() {
//        accountKeys = gson.fromJson(FileHelper.readFileToString(_accountInfoPath), AccountKeys::class.java)

        try {
            accountKeys = gson.fromJson(FileHelper.readFileToString(ACCOUNT_JSON), AccountKeys::class.java)
        } catch (e: Exception) {
            println(
                "\nWarning -- 'resources\\account_info.json' not found. This file is needed to make account " +
                        "related requests, including placing trades, checking positions, and checking balance.\n" +
                        "Please run getAccountNumbers(). Access token will be needed so login() first."
            )
        }
    }

    /** Peforms the login required to obtain Refresh Token. Refresh Token expires every 7 days. */
    fun login() {
        // Build login url
        val url = "$auth_base_endpoint/authorize?client_id=key_here&redirect_uri=https://127.0.0.1"
            .replace("key_here", authKeys.key)

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
        val base64Credentials = Base64.getEncoder().encodeToString("${authKeys.key}:${authKeys.secret}".toByteArray())
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
            val tokens = Tokens(
                tokenResponse.refresh_token,
                tokenResponse.access_token,
                tokenResponse.id_token,
                accessTokenExpiry,
                refreshTokenExpiry
            )

            // Save to file
            FileHelper.writeFile(TOKENS_JSON, gson.toJson(tokens))
        } else {
            throw Exception("Request failed: ${response.code} ${response.message}")
        }

    }

    /** Returns Access Token. Will update the Access token if needed using a valid Refresh token */
    private fun get_access_token(): String {

        // Check if tokens have been loaded
        if (tokens == null) {
            try {
                tokens = gson.fromJson(FileHelper.readFileToString(TOKENS_JSON), Tokens::class.java)
            } catch (e: Exception) {
                println(
                    "\nWarning -- 'resources\\tokens.json' not found. This file is needed to update the tokens.\n" +
                            "Please use function CSAuth().login() and follow the steps to create the file.\n" +
                            "This will have to be done once per week to maintain valid Refresh Token"
                )
                exitProcess(0)
            }
        }

        // Check if refresh token has expired
        if (tokens!!.refreshTokenExpiryInMs < System.currentTimeMillis()) {
            println("\nWarning -- Refresh Token has expired. Please use function CSAuth().login() to update.")
            exitProcess(0)
        }

        // Check if access token needs to be updated, if not return access token
        if (tokens!!.accessTokenExpiryInMs > System.currentTimeMillis()) {
            return tokens!!.access_token
        }

        // Use refresh token to update access token
        val postBody = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", tokens!!.refresh_token)
            .build()

        val base64Credentials = Base64.getEncoder().encodeToString("${authKeys.key}:${authKeys.secret}".toByteArray())
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
            val newToken = Tokens(
                body.refresh_token,
                body.access_token,
                body.id_token,
                accessTokenExpiryInMs = newAccessExpiry,
                refreshTokenExpiryInMs = tokens!!.refreshTokenExpiryInMs,
            )
            tokens = newToken
            FileHelper.writeFile(TOKENS_JSON, gson.toJson(newToken))
        }
        return ""
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //    Market Data Start

    /**
     * Notes
     * - Requests need to have a header 'Authorization': 'Bearer <accessToken>'
     * - Requests related to account need to have the hash value of the account number
     * */
    fun getQuote(symbol: String): Quote? {
        val token = get_access_token()
        val s = symbol.uppercase()
        val req = Request.Builder()
            .header("Authorization", "Bearer $token")
            .header("accept", "application/json")
            .get()
            .url(market_data_base_endpoint + "/${s}/quotes")
            .build()
        val resp = NetworkClient.getClient().newCall(req).execute()
        if (resp.isSuccessful) {
            val body = resp.body?.string()
            val jsonObject = gson.fromJson(body, Map::class.java)
            val assetJson = gson.toJson(jsonObject[s])
            val asset = gson.fromJson(assetJson, QuoteResponse::class.java)
            return asset.convertToQuote()
        } else {
            println("response not successful. Code: ${resp.code}")
            return null
        }
    }       // TODO Check for a non existant sym

    fun getOptionChain(
        symbol: String,
        contractType: String? = "ALL",
        strikeCount: Int? = 5,      // ?
        includeUnderlyingQuote: Boolean? = true,
        range: String? = "NTM",     // ?
        strike: Double? = null,
        fromDate: String? = null,
        toDate: String? = null,
    ) : OptionChain? {
        // TODO if from/toDate are null make them today
        // TODO rebuild call/putexpdatemap. It returns the indiv options in a list format
        val baseUrl = market_data_base_endpoint + "/chains"
        val params = mutableListOf<String>()

        params.add("symbol=${symbol.uppercase()}")
        contractType?.let { params.add("contractType=$it") }
        strikeCount?.let { params.add("strikeCount=$it") }
        includeUnderlyingQuote?.let { params.add("includeUnderlyingQuote=$it") }
        strike?.let { params.add("strike=$it") }
        range?.let { params.add("range=$it") }
        fromDate?.let { params.add("fromDate=$it") }
        toDate?.let { params.add("toDate=$it") }

        val url = "$market_data_base_endpoint/chains?${params.joinToString("&")}"
        val token = get_access_token()
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
            val cExpiryMap = mutableMapOf<String, Map<String, Option>>()
            val pExpiryMap = mutableMapOf<String, Map<String, Option>>()
            for (date in ocr.callExpDateMap.keys){
                val callStrikeMapOrig = ocr.callExpDateMap.get(date)!!
                val putStrikeMapOrig = ocr.putExpDateMap.get(date)!!
                val csf = mutableMapOf<String, Option>()
                val psf = mutableMapOf<String, Option>()
                for (strike in callStrikeMapOrig.keys) {
                    csf.put(strike, callStrikeMapOrig[strike]!!.first())
                    psf.put(strike, putStrikeMapOrig[strike]!!.first())
                }
                cExpiryMap.put(date, csf)
                pExpiryMap.put(date, psf)
            }
            println(cExpiryMap.get(cExpiryMap.keys.first())?.keys)
            return ocr.convertToOptionChain(cExpiryMap, pExpiryMap)
        }
        else {
            Log.w("getOptionChain", resp.body?.string())        // TODO Logger
            return null
        }
    }

    fun getTopOptionVolumeTickers(): List<String>? {
        val today = convertTimestampToDateyyyyMMdd(System.currentTimeMillis())



        return null
    }




        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //    Account Data Start

    fun getAccountNumbers() {
            val at = get_access_token()
            val req = Request.Builder()
                .get()
                .url(account_base_endpoint + "/accounts/accountNumbers")
                .header("Authorization", "Bearer $at")
                .header("accept", "application/json")
                .build()

            val resp = NetworkClient.getClient().newCall(req).execute()
            if (resp.isSuccessful) {
                val body = resp.body?.string()
                val accountListType = object : TypeToken<List<AccountKeys>>() {}.type
                accountKeys = gson.fromJson<List<AccountKeys>?>(body, accountListType).get(0)
                FileHelper.writeFile(ACCOUNT_JSON, gson.toJson(accountKeys))
            }


}

    fun test() {
        getTopOptionVolumeTickers()
    }
}