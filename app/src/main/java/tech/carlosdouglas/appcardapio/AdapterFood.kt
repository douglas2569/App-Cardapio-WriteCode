package tech.carlosdouglas.appcardapio

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class FoodAdapter(
    private var items: List<Food>,
    private var foodMap:MutableMap<String, MutableList<Food>>,
    private var amountTextView:TextView,
    private var amount:Array<Double>

    ) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_food, parent, false)
        return FoodViewHolder(view, foodMap, amountTextView, amount)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)

        holder.nameTextView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<Food>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getItems(): List<Food> {
        return items
    }

    class FoodViewHolder(itemView: View,
                         private var foodMap:MutableMap<String, MutableList<Food>>,
                         private val amountTextView: TextView,
                         private val amount: Array<Double>
                        ) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.text_food_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.text_food_price)
        private val preparationTimeTextView: TextView = itemView.findViewById(R.id.text_preparation_time)

        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_food)

        @SuppressLint("SetTextI18n")
        fun bind(food: Food) {
            nameTextView.text = food.name
            try {
                priceTextView.text = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(food.price.toDouble())
            }catch (e:IllegalArgumentException){
                println("Formato de objeto incorreto, passe um  numero: ${e.message}")
            }catch (e: NumberFormatException) {
                println("String invalida: ${e.message}")
            }

            preparationTimeTextView.text = food.preparationTime

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                try {
                    manageLocalList(checkBox.isChecked, food, arrayOf("appetizers","main_courses","desserts","beverages") )

                }catch (e: NumberFormatException) {
                    println("String invalida: ${e.message}")
                }

                amountTextView.text = "Total: R$ ${amount[0].toString()}"


            }

        }

        fun bindCheckbox(isChecked: Boolean) {
            checkBox.isChecked = isChecked
        }

        private fun addFoodToLocalList(listFood: MutableList<Food>, food:Food, amount:Array<Double> ){

            if (listFood.none { it.id == food.id }) {
                listFood.add(
                    Food(
                        food.id,
                        food.name,
                        food.preparationTime,
                        food.price,
                        food.domain
                    )
                )
                amount[0] += food.price.toDouble()
            }
        }

        private fun removeFoodToLocalList(listFood: MutableList<Food>, food:Food, amount:Array<Double> ){
            listFood.removeIf { it.id == food.id }
            amount[0] -= food.price.toDouble()
        }

        private fun manageLocalList(condition:Boolean, food:Food, listOfCases:Array<String>){
            if (condition) {
                listOfCases.forEach { it ->
                    when (food.domain) {
                        it -> {
                            foodMap[it]?.let { addFoodToLocalList(it, food, amount) }
                        }
                    }
                }

            } else {
                listOfCases.forEach { it ->
                    when (food.domain) {
                        it -> {
                            foodMap[it]?.let { removeFoodToLocalList(it, food, amount) }
                        }
                    }
                }

            }
        }
    }
}
