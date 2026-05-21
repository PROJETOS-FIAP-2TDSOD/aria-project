package com.fiap.ariachallenge.data.local

import com.fiap.ariachallenge.data.remote.StoredAuthAccount
import org.json.JSONArray
import org.json.JSONObject

object AuthAccountJson {
    fun encodeAll(accounts: Collection<StoredAuthAccount>): String {
        val array = JSONArray()
        accounts.forEach { account ->
            encode(account)?.let { array.put(it) }
        }
        return array.toString()
    }

    fun decodeAll(json: String): List<StoredAuthAccount> {
        if (json.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            buildList {
                for (i in 0 until array.length()) {
                    decode(array.getJSONObject(i))?.let { add(it) }
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun encode(account: StoredAuthAccount): JSONObject? =
        runCatching {
            val userJson = UserSessionJson.encode(account.user) ?: return@runCatching null
            JSONObject()
                .put("email", account.email)
                .put("password", account.password)
                .put("user", userJson)
        }.getOrNull()

    private fun decode(obj: JSONObject): StoredAuthAccount? =
        runCatching {
            val email = obj.getString("email").trim().lowercase()
            val password = obj.getString("password")
            val user = UserSessionJson.decode(obj.getString("user")) ?: return@runCatching null
            StoredAuthAccount(email = email, password = password, user = user)
        }.getOrNull()
}
