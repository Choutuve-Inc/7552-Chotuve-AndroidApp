package com.app.chotuve.utils

import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class JSONArraySorter {
    companion object{
        fun sortJSONArrayByFirstLevelIntField(myJsonArray: JSONArray, field: String): JSONArray{
            val hash = HashMap<Int, JSONObject>()
            for (i in 0 until myJsonArray.length()) {
                val item = myJsonArray.getJSONObject(i)
                hash[item.getInt(field)] = item
            }
            return JSONArray(hash.toSortedMap().values)
        }

        fun sortJSONArrayByFirstLevelStringField(myJsonArray: JSONArray, field: String): JSONArray{
            val hash = HashMap<String, JSONObject>()
            for (i in 0 until myJsonArray.length()) {
                val item = myJsonArray.getJSONObject(i)
                hash[item.getString(field).toUpperCase()] = item
            }
            val treeMap =TreeMap(hash)
            return JSONArray(hash.toSortedMap().values)
        }
    }
}