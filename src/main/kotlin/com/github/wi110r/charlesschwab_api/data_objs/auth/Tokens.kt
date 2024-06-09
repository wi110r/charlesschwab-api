package com.github.wi110r.com.github.wi110r.charlesschwab_api.data_objs.auth

data class Tokens(
    val refresh_token: String,
    val access_token: String,
    val id_token: String,
    val accessTokenExpiryInMs: Long,
    val refreshTokenExpiryInMs: Long,
)
