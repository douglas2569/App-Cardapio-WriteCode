package tech.carlosdouglas.appcardapio

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Food(
    val id: String,
    val name: String,
    val preparationTime: String,
    val price: String,
    val domain: String
) {

    companion object {

        fun getAllAppetizers(context: Context): List<Food> {
            val desserts = mutableListOf<Food>()
            try {
                val inputStream = context.resources.openRawResource(R.raw.data)
                val json = inputStream.bufferedReader().use { it.readText() }
                val menu = JSONObject(json)
                desserts.addAll(parseFoods(menu.getJSONArray("appetizers")))
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return desserts
        }

        fun getAllMainCourses(context: Context): List<Food> {
            val desserts = mutableListOf<Food>()
            try {
                val inputStream = context.resources.openRawResource(R.raw.data)
                val json = inputStream.bufferedReader().use { it.readText() }
                val menu = JSONObject(json)
                desserts.addAll(parseFoods(menu.getJSONArray("main_courses")))
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return desserts
        }

        fun getAllBeverages(context: Context): List<Food> {
            val desserts = mutableListOf<Food>()
            try {
                val inputStream = context.resources.openRawResource(R.raw.data)
                val json = inputStream.bufferedReader().use { it.readText() }
                val menu = JSONObject(json)
                desserts.addAll(parseFoods(menu.getJSONArray("beverages")))
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return desserts
        }

        fun getAllDesserts(context: Context): List<Food> {
            val desserts = mutableListOf<Food>()
            try {
                val inputStream = context.resources.openRawResource(R.raw.data)
                val json = inputStream.bufferedReader().use { it.readText() }
                val menu = JSONObject(json)
                desserts.addAll(parseFoods(menu.getJSONArray("desserts")))
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("Test", "IOException")
                Log.d("Test", e.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return desserts
        }

        private fun parseFoods(jsonArray: JSONArray): List<Food> {
            val foods = mutableListOf<Food>()
            for (i in 0 until jsonArray.length()) {
                val foodObject = jsonArray.getJSONObject(i)
                val id = foodObject.getString("id")
                val name = foodObject.getString("name")
                val image = foodObject.getString("image")
                val preparationTime = foodObject.getString("preparation_time")
                val price = foodObject.getString("price")
                val domain = foodObject.getString("domain")
                val food = Food(id, name, preparationTime, price, domain)
                foods.add(food)
            }
            return foods
        }

    }
}
