package tech.carlosdouglas.appcardapio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.carlosdouglas.appcardapio.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var appetizersAdapter: FoodAdapter
    private lateinit var amountTextView: TextView

    private var appetizersList = mutableListOf<Food>()
    private var mainCoursesList = mutableListOf<Food>()
    private var dessertsList = mutableListOf<Food>()
    private var beveragesList = mutableListOf<Food>()

    private var amount = Array(1) { 0.00 }

    private lateinit var appetizers:List<Food>

    private val foodMap = mutableMapOf(
        "appetizers" to appetizersList,
        "main_courses" to mainCoursesList,
        "desserts" to dessertsList,
        "beverages" to beveragesList,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAppetizers.setOnClickListener(this)
        binding.buttonMainCourses.setOnClickListener(this)
        binding.buttonBeverages.setOnClickListener(this)
        binding.buttonDesserts.setOnClickListener(this)
        binding.buttonFinalizeOrder.setOnClickListener(this)

        amountTextView = findViewById(R.id.text_amount)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        appetizersAdapter = FoodAdapter(emptyList(), foodMap, amountTextView, amount)
        appetizers = Food.getAllAppetizers(this)
        appetizersAdapter.updateData(appetizers)
        recyclerView.adapter = appetizersAdapter

        binding.textAmount.addTextChangedListener{
            if(amount[0] > 0){
                binding.buttonFinalizeOrder.background = ResourcesCompat.getDrawable(this.getResources(), R.drawable.custom_button, null)
                setBackgroundButtonFinalizeOrder(binding.buttonFinalizeOrder, R.drawable.custom_button)
            }else{
                setBackgroundButtonFinalizeOrder(binding.buttonFinalizeOrder, R.drawable.custom_button_finalize_order_disable)
            }

        }

        manageButtonBorders( binding.buttonAppetizers, listOf(binding.buttonMainCourses, binding.buttonBeverages, binding.buttonDesserts))


    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.button_appetizers -> listAppetizers()
            R.id.button_main_courses -> listMainCourses()
            R.id.button_beverages -> listBeverages()
            R.id.button_desserts -> listDesserts()
            R.id.button_finalize_order -> finalizeOrder()
        }
    }


    private fun listAppetizers() {
        manageButtonBorders( binding.buttonAppetizers, listOf(binding.buttonMainCourses, binding.buttonBeverages, binding.buttonDesserts))

        appetizers = Food.getAllAppetizers(this)
        appetizersAdapter.updateData(appetizers)
        recyclerView.adapter = appetizersAdapter
        reCheckBox(foodMap["appetizers"], appetizers , recyclerView)
    }


    private fun listMainCourses() {
        manageButtonBorders(binding.buttonMainCourses, listOf(binding.buttonAppetizers, binding.buttonBeverages, binding.buttonDesserts))

        val mainCoursesAdapter = FoodAdapter(emptyList(), foodMap, amountTextView, amount)
        val mainCourses = Food.getAllMainCourses(this)

        mainCoursesAdapter.updateData(mainCourses)
        recyclerView.adapter = mainCoursesAdapter

        reCheckBox(foodMap["main_courses"], mainCourses , recyclerView)

    }

    private fun listDesserts() {
        manageButtonBorders(binding.buttonDesserts, listOf(binding.buttonAppetizers, binding.buttonMainCourses, binding.buttonBeverages ))

        val dessertsAdapter = FoodAdapter(emptyList(), foodMap, amountTextView, amount)
        val desserts = Food.getAllDesserts(this)

        dessertsAdapter.updateData(desserts)
        recyclerView.adapter = dessertsAdapter

        reCheckBox(foodMap["desserts"], desserts , recyclerView)

    }

    private fun listBeverages() {
        manageButtonBorders(binding.buttonBeverages, listOf(binding.buttonAppetizers, binding.buttonMainCourses, binding.buttonDesserts ))

        val beveragesAdapter = FoodAdapter(emptyList(), foodMap, amountTextView, amount)
        val beverages = Food.getAllBeverages(this)

        beveragesAdapter.updateData(beverages)
        recyclerView.adapter = beveragesAdapter

        reCheckBox(foodMap["beverages"], beverages , recyclerView)

    }

    private fun reCheckBox(listLocallyStoredFood:MutableList<Food>?, listDatabaseStoredFood:List<Food>, recyclerView:RecyclerView) {
        recyclerView.post {
            if (!listLocallyStoredFood.isNullOrEmpty()) {
                listDatabaseStoredFood.forEach { food ->
                    listLocallyStoredFood.find { it.id == food.id }?.let {
                        val position = listDatabaseStoredFood.indexOf(food)
                        recyclerView.findViewHolderForAdapterPosition(position)?.let { viewHolder ->
                            (viewHolder as? FoodAdapter.FoodViewHolder)?.bindCheckbox(true)
                        }
                    }
                }

            }
        }

    }

    private fun finalizeOrder(){

        if(amount[0] <= 0) {
            showToast("Nenhum item selecionado. Por favor, escolha pelo menos uma opção", R.layout.custom_toast_warning_message, Toast.LENGTH_LONG)

            return
        }

        appetizersList = mutableListOf<Food>()
        mainCoursesList = mutableListOf<Food>()
        dessertsList = mutableListOf<Food>()
        beveragesList = mutableListOf<Food>()

        showToast("Seu pedido foi enviado para o balcão do restaurante.", R.layout.custom_toast_success_message, Toast.LENGTH_SHORT)

        reloadWindow()
    }

    private fun reloadWindow(){
        val i = Intent(this@MainActivity, MainActivity::class.java)
        finish()
        startActivity(i)
    }

    private fun manageButtonBorders(mainBindingButton:Button, buttonsBindingErase:List<Button>){
        mainBindingButton.background = ResourcesCompat.getDrawable(this.getResources(), R.drawable.custom_button_meals, null)

        buttonsBindingErase.forEach {
            it.background = ResourcesCompat.getDrawable(this.getResources(),
                R.drawable.custom_button_empty, null)
        }
    }

    private fun showToast(message:String, layout_custom_toast_message:Int, duration:Int){
        val layoutInflater: LayoutInflater = layoutInflater
        val view: View = layoutInflater.inflate(layout_custom_toast_message, null)
        val text: TextView = view.findViewById(R.id.text_toast)
        val toast = Toast(applicationContext)

        text.text = message
        toast.duration = duration
        toast.view = view
        toast.show()


    }

    private fun setBackgroundButtonFinalizeOrder(buttonFinalizeOrder:Button, id:Int){
        buttonFinalizeOrder.background = ResourcesCompat.getDrawable(this.getResources(), id, null)
    }

}