package com.preeyanut.cjhevie


import AssocieAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.preeyanut.loginfragment.R
import kotlinx.android.synthetic.main.fragment_playloto.buttonAddCombination
import kotlinx.android.synthetic.main.fragment_playloto.gridViewNumbers
import kotlinx.android.synthetic.main.fragment_playloto.listView
import kotlinx.android.synthetic.main.fragment_playloto.spinnerBetType1
import kotlinx.android.synthetic.main.fragment_playloto.spinnerBetType2
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.Locale

class PlaylotoFragment : Fragment() {
    private lateinit var textViewProductType: TextView
    private lateinit var textViewGameTime: TextView

    private lateinit var numbersGridView: GridView
    private lateinit var numberAdapter: NumberAdapter
    private val numbersList: List<Int> = (1..90).toList() // Liste de 1 à 90
    private lateinit var editTextNumbers: EditText  // Nouvelle propriété
    private val selectedNumbers: MutableList<Int> = mutableListOf() // Déclaration de la liste de numéros sélectionnés
    private lateinit var editTextAmount: EditText
    private lateinit var buttonClearSelections: Button
    private var numBase: String = ""
    private var associe: String = ""
    private var montant: String = ""
    private lateinit var listViewFullscreen: ListView
    private lateinit var closeButton: Button

    // Ajoutez une propriété pour suivre le nombre de bases sélectionnées
    private var selectedBaseCount = 0
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?



    ): View? {
        val root = inflater.inflate(R.layout.fragment_playloto, container, false)
        textViewProductType = root.findViewById(R.id.textViewProductType)
        textViewGameTime = root.findViewById(R.id.textViewGameTime)
        editTextAmount = root.findViewById(R.id.editTextAmount)
        buttonClearSelections = root.findViewById(R.id.buttonClearSelections)
        editTextNumbers = root.findViewById(R.id.editTextNumbers)  // Initialisation de la référence

        editTextNumbers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Ici, vous devez mettre à jour les sélections en fonction du texte dans l'EditText


            }
        })


        // ... Initialisations et configurations ...


        numbersGridView = root.findViewById(R.id.gridViewNumbers)

        // Liste vide au début
        val selectedNumbers: MutableList<Int> = mutableListOf()

        // Initialise l'adaptateur avec la liste de numéros et les numéros sélectionnés
        numberAdapter = NumberAdapter(requireContext(), numbersList, selectedNumbers, editTextNumbers)
        numbersGridView.adapter = numberAdapter


        // Effacement de la liste des numéros sélectionnés

        buttonClearSelections = root.findViewById(R.id.buttonClearSelections)
        buttonClearSelections.setOnClickListener {
            numberAdapter.clearSelections() // Appel de la fonction de l'adaptateur
            editTextAmount.text.clear() // Effacement du texte dans l'EditText'
            spinnerBetType1.setSelection(0)
            spinnerBetType2.setSelection(0)

        }



        // Appel de la fonction pour ajouter dans la liste view les numéros sélectionnés"
        val buttonAddCombination = root.findViewById<Button>(R.id.buttonAddCombination)

        buttonAddCombination.setOnClickListener {
            addCombinationOnClick()
        }






        // Appel de la fonction pour obtenir la valeur de "Horaire du jeu"
        val gameTime = getGameTime()
        textViewGameTime.text = "Tirage de : $gameTime"

        // Obtenir la valeur réelle du type de produit depuis la source de données
        val productTypeText = getProductType()
        textViewProductType.text = "Type de produit : $productTypeText"


        val betTypeOptions = resources.getStringArray(R.array.type_de_pari)
        val betTypeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item_layout, betTypeOptions)
        val spinnerBetType1: Spinner = root.findViewById(R.id.spinnerBetType1)
        val spinnerBetType2 = root.findViewById<Spinner>(R.id.spinnerBetType2)



        // val buttonAddCombination = findViewById<Button>(R.id.buttonAddCombination)

        //buttonAddCombination.setOnClickListener {
          //  addCombinationOnClick()
        //}

        spinnerBetType1.adapter = betTypeAdapter
        spinnerBetType1.dropDownVerticalOffset = resources.getDimensionPixelSize(R.dimen.spinner_dropdown_offset)


// Ajoutez un écouteur au premier spinner pour charger les price banker
        spinnerBetType1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedItem = spinnerBetType1.adapter.getItem(position).toString()

                val selectedBetType = betTypeOptions[position]
                updateSpinner2Options(selectedBetType)



                // Vérifier si l'option sélectionnée est "Tree Nap" et appeler handleTreeNapSelection()
                if (selectedItem == "Tree Nap") {

                    handleTreeNapSelection()
                    editTextAmount.isEnabled = true
                } else if (selectedItem == "Base") {

                    handleBaseSelection(selectedBetType)
                } else if (selectedItem == "Banker") {

                    val bankerOptions = resources.getStringArray(R.array.formule_Banker)
                    val spinner2Adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bankerOptions)
                    spinner2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerBetType2.adapter = spinner2Adapter

                    // Appeler la fonction updateAmount avec l'option sélectionnée du deuxième spinner
                    spinnerBetType2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val selectedItem2 = spinnerBetType2.adapter.getItem(position).toString()
                            updateAmount(selectedItem2)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Gérez si rien n'est sélectionné
                        }
                    }

                    editTextAmount.isEnabled = true
                }
                else if (selectedItem == "Poto") {

                    handlePotoSelection()
                }
                else if (selectedItem == "Vedette") {

                    handleVedetteSelection()                }



            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Gérez si rien n'est sélectionné
            }
        }

        // Ajoutez un écouteur au deuxième spinner pour charger les price banker

        spinnerBetType2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
// Réinitialisez la sélection précédente à la position par défaut
                val selectedItem = spinnerBetType2.adapter.getItem(position).toString()

                if (selectedItem.startsWith("Perm")) {

                    handlePermSelection(selectedItem)
                } else if (selectedItem == "1 Base" || selectedItem == "2 Base" || selectedItem == "3 Base") {

                    handleBaseSelection(selectedItem)
                }

                else {

                    // Si ce n'est ni "Perm" ni "Base", désactiver la zone de saisie de montant
                //  updateAmount(selectedItem)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Gérer le cas où aucun élément n'est sélectionné
            }
        }


        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun getProductType(): String {
        val dayOfWeek = LocalDate.now().dayOfWeek
        val productType = if ((dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.TUESDAY ||
                    dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.FRIDAY)) {
            "Fortune"
        } else if (dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.SATURDAY ||
            dayOfWeek == DayOfWeek.SUNDAY) {
            "Star"
        } else {
            ""
        }
        return productType
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getGameTime(): String {
        val currentTime = LocalTime.now()
        return when {
            currentTime.isAfter(LocalTime.of(6, 0)) && currentTime.isBefore(LocalTime.of(11, 15)) -> {
                "11H"
            }
            currentTime.isAfter(LocalTime.of(11, 0)) && currentTime.isBefore(LocalTime.of(14, 15)) -> {
                "14H"
            }
            currentTime.isAfter(LocalTime.of(14, 0)) && currentTime.isBefore(LocalTime.of(18, 15)) -> {
                "18H"
            }
            else -> ""
        }
    }

    // Fonction pour calculer le montant en fonction de la sélection


    @SuppressLint("ClickableViewAccessibility")
    private fun updateAmount(selectedItem: String) {

        val amountEditText = requireView().findViewById<EditText>(R.id.editTextAmount)
        editTextAmount.setOnTouchListener { _, event ->
            val drawableEnd = editTextAmount.compoundDrawablesRelative[2]
            if (event.action == MotionEvent.ACTION_UP && event.rawX >= (editTextAmount.right - drawableEnd.bounds.width())) {
                // Rien ne se passe lors du clic sur le bouton "+"
            }
            true
        }
        if (selectedItem.startsWith("0")) {
            // Convertir le nombre dans la chaîne en entier et calculer le montant
            val numberOfPrises = selectedItem.substringBefore(" ").toInt()
            val priseAmount = 890
            val totalAmount = numberOfPrises * priseAmount
            amountEditText.setText("$totalAmount FCFA")
        }
            else if (selectedItem.startsWith("10")) {
                // Calculer le montant pour 10 Prises
                val priseAmount = 890
                val totalAmount = priseAmount * 10
            amountEditText.setText("$totalAmount FCFA")
        } else {
            amountEditText.setText("")
        }

        amountEditText.isEnabled = true
    }



    private fun updateSpinner2Options(selectedBetType: String) {


        val optionsArrayId = when (selectedBetType) {
            "Perm" -> R.array.formule_de_perm

            "Two Sure" -> R.array.formule_nada
            "Poto" -> R.array.formule_nada
            "Base" -> R.array.formule_base
            "Banker" -> R.array.formule_Banker
            "Vedette" -> R.array.formule_nada
            "Tree Nap" -> R.array.formule_nada
            else -> R.array.formule_nada // Gérer les autres cas
        }

        val permOptions = resources.getStringArray(optionsArrayId)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, permOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBetType2.adapter = adapter


        // Appeler la logique pour traiter la sélection de la base et des numéros associés
       // handleBaseSelection(selectedBetType)
    }



    // Dans la fonction handleBaseSelection
    private fun handleBaseSelection(selectedBetType: String) {


        val selectedItem = spinnerBetType2.selectedItem.toString()

        if (selectedItem == "1 Base" || selectedItem == "2 Base" || selectedItem == "3 Base") {
            // Mettre à jour le nombre de bases sélectionnées en fonction de l'élément choisi
            selectedBaseCount = when (selectedItem) {
                "1 Base" -> 1
                "2 Base" -> 2
                "3 Base" -> 3
                else -> selectedBaseCount
            }
            // Bloquer le GridVie
            gridViewNumbers.isEnabled = false

            // Afficher la boîte de dialogue pour saisir le nombre de numéros de base
            showNumBaseInputDialog(selectedItem)
        } else {
            // ...
        }
    }



    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun handlePermSelection(selectedItem: String) {


        val editTextNumbers = requireView().findViewById<EditText>(R.id.editTextNumbers)
        val editTextAmount = requireView().findViewById<EditText>(R.id.editTextAmount)


        val permNumberRange = when (selectedItem) {
            "Perm de 2" -> 3..30
            "Perm de 3" -> 4..30
            "Perm de 4" -> 5..30
            "Perm de 5" -> 6..30
            else -> throw IllegalArgumentException("Invalid perm selection")
        }

        editTextNumbers.isEnabled = true
        editTextAmount.isEnabled = false

        editTextAmount.setOnTouchListener { _, event ->
            val drawableEnd = editTextAmount.compoundDrawablesRelative[2]
            if (event.action == MotionEvent.ACTION_UP && event.rawX >= (editTextAmount.right - drawableEnd.bounds.width())) {
                val amountText = editTextAmount.text.toString().trim()
                if (amountText.isNotBlank()) {
                    var amount = amountText.toInt()
                    amount += 10
                    editTextAmount.setText(amount.toString())
                } else {
                    editTextAmount.setText("10")
                }
                true
            } else {
                false
            }
        }

        editTextNumbers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val numbersText = s?.toString() ?: ""
                val numNumbers = numbersText.split(",").filter { it.isNotBlank() }.size

                if (numNumbers in permNumberRange) {
                    editTextAmount.isEnabled = true

                    val amountText = editTextAmount.text.toString().trim()
                    if (amountText.isNotBlank()) {
                        var amount = amountText.toInt()
                        if (amount < 10) {
                            amount = 10
                            editTextAmount.setText(amount.toString())
                        }
                        if (amount % 10 == 0 && amount in 10..100_000) {
                            // Show success message
                            showToast("Contrôle de zone de saisie terminé.")
                        } else {
                            // Show error message for amount input
                            showToast("Montant invalide! Le montant doit être un multiple de 10, entre 10 et 100 000 FCFA.")
                        }
                    } else {
                        // Show error message for empty amount input
                        showToast("Montant invalide! Le montant est obligatoire.")
                    }
                } else {
                    // Show error message for numbers input
                    showToast("Nombre invalide! Le nombre de numéros doit être entre ${permNumberRange.first} et ${permNumberRange.last}.")
                    editTextAmount.isEnabled = false
                }
            }
        })
    }


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun handleTreeNapSelection() {


        val editTextNumbers = requireView().findViewById<EditText>(R.id.editTextNumbers)
        val editTextAmount = requireView().findViewById<EditText>(R.id.editTextAmount)
        val MIN_AMOUNT = 50

        // Contrôle du nombre de numéros saisis (3 au maximum)
        editTextNumbers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val numbersText = s?.toString() ?: ""
                val numNumbers = numbersText.split(",").filter { it.isNotBlank() }.size

                if (numNumbers > 3) {
                    val firstThreeNumbers = numbersText.split(",").take(3).joinToString(",")
                    editTextNumbers.setText(firstThreeNumbers)
                    editTextNumbers.setSelection(firstThreeNumbers.length) // Déplacer le curseur à la fin du texte
                    // Afficher un message indiquant que seuls 3 numéros sont autorisés
                    Toast.makeText(requireContext(), "Maximum de 3 numéro autorisé", Toast.LENGTH_SHORT).show()
                }

                // Vérifier si les 3 numéros ont été saisis, sinon effacer le montant
                if (numNumbers != 3) {
                    editTextAmount.setText("")
                }
            }
        })

        editTextAmount.isEnabled = false

        // Vérification du montant saisi (multiple de 10, minimum 50 FCFA)
        editTextAmount.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val drawableEnd = editTextAmount.compoundDrawablesRelative[2]
                if (event?.action == MotionEvent.ACTION_UP && event.rawX >= (editTextAmount.right - drawableEnd.bounds.width())) {
                    val amountText = editTextAmount.text.toString().toIntOrNull()
                    val amount = amountText ?: MIN_AMOUNT
                    editTextAmount.setText(amount.toString())
                    // Afficher un message indiquant le montant minimum
                    if (amount < MIN_AMOUNT) {
                        Toast.makeText(requireContext(), "Le montant minimun est $MIN_AMOUNT", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
                return false
            }
        })

        editTextAmount.setOnTouchListener { _, event ->
            val drawableEnd = editTextAmount.compoundDrawablesRelative[2]
            if (event.action == MotionEvent.ACTION_UP && event.rawX >= (editTextAmount.right - drawableEnd.bounds.width())) {
                val amountText = editTextAmount.text.toString().trim()
                if (amountText.isNotBlank()) {
                    var amount = amountText.toInt()
                    amount += 10
                    editTextAmount.setText(amount.toString())
                } else {
                    editTextAmount.setText("50")
                }
                true
            } else {
                false
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun handlePotoSelection() {



        val editTextNumbers = requireView().findViewById<EditText>(R.id.editTextNumbers)
        val editTextAmount = requireView().findViewById<EditText>(R.id.editTextAmount)
        val MIN_AMOUNT = 50

        // Contrôle du nombre de numéros saisis (1 au maximum)
        editTextNumbers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val numbersText = s?.toString() ?: ""
                val numNumbers = numbersText.split(",").filter { it.isNotBlank() }.size

                if (numNumbers > 1) {
                    val firstNumbers = numbersText.split(",").take(1).joinToString(",")
                    editTextNumbers.setText(firstNumbers)
                    editTextNumbers.setSelection(firstNumbers.length) // Déplacer le curseur à la fin du texte
                    // Afficher un message indiquant que seuls 1 numéros sont autorisés
                    Toast.makeText(requireContext(), "Maximum de 1 numéro autorisé", Toast.LENGTH_SHORT).show()
                }

                // Vérifier si les 1 numéros ont été saisis, sinon effacer le montant
                if (numNumbers != 1) {
                    editTextAmount.setText("")
                }
            }
        })

        editTextAmount.isEnabled = false

        // Vérification du montant saisi (multiple de 10, minimum 50 FCFA)
        editTextAmount.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val drawableEnd = editTextAmount.compoundDrawablesRelative[2]
                if (event?.action == MotionEvent.ACTION_UP && event.rawX >= (editTextAmount.right - drawableEnd.bounds.width())) {
                    val amountText = editTextAmount.text.toString().toIntOrNull()
                    val amount = amountText ?: MIN_AMOUNT
                    editTextAmount.setText(amount.toString())
                    // Afficher un message indiquant le montant minimum
                    if (amount < MIN_AMOUNT) {
                        Toast.makeText(requireContext(), "Le montant minimum est $MIN_AMOUNT", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
                return false
            }
        })

        editTextAmount.setOnTouchListener { _, event ->
            val drawableEnd = editTextAmount.compoundDrawablesRelative[2]
            if (event.action == MotionEvent.ACTION_UP && event.rawX >= (editTextAmount.right - drawableEnd.bounds.width())) {
                val amountText = editTextAmount.text.toString().trim()
                if (amountText.isNotBlank()) {
                    var amount = amountText.toInt()
                    amount += 10
                    editTextAmount.setText(amount.toString())
                } else {
                    editTextAmount.setText("50")
                }
                true
            } else {
                false
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun handleVedetteSelection() {
        val MIN_AMOUNT = 310

        val numbersLimit = 5 // Limite de 5 numéros pour vedette

        // Contrôler le nombre de numéros saisis (5 exactement)
        editTextNumbers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val numbersText = s?.toString() ?: ""
                val numNumbers = numbersText.split(",").filter { it.isNotBlank() }.size

                if (numNumbers != numbersLimit) {
                    // Afficher un message indiquant que seul 5 numéros sont autorisés
                    Toast.makeText(requireContext(), "Veuillez sélectionner exactement 5 numéros", Toast.LENGTH_SHORT).show()
                }
            }
        })

        editTextAmount.isEnabled = true

        editTextAmount.setOnTouchListener { _, event ->
            val drawableEnd = editTextAmount.compoundDrawablesRelative[2]
            if (event.action == MotionEvent.ACTION_UP && event.rawX >= (editTextAmount.right - drawableEnd.bounds.width())) {
                val amountText = editTextAmount.text.toString().trim()
                if (amountText.isNotBlank()) {
                    var amount = amountText.toInt()
                    amount += 310
                    editTextAmount.setText(amount.toString())
                    // Afficher un message indiquant le montant minimum
                    if (amount < MIN_AMOUNT) {
                        editTextAmount.setText("310")

                        Toast.makeText(requireContext(), "Le montant minimum est $MIN_AMOUNT", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    editTextAmount.setText("310")
                }
                true
            } else {
                false
            }
        }



    }

    private fun showNumBaseInputDialog(selectedItem: String) {


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Saisir les numéros de base")
        val inputView = layoutInflater.inflate(R.layout.fragment_dialog_num_base_input, null)
        builder.setView(inputView)

        val numBaseContainer = inputView.findViewById<LinearLayout>(R.id.numBaseContainer)
        val btnConfirm = inputView.findViewById<Button>(R.id.buttonValider)

        val selectedBaseCount = when (selectedItem) {
            "1 Base" -> 1
            "2 Base" -> 2
            "3 Base" -> 3
            else -> 0
        }

// Ajouter les cases de saisie en fonction du nombre de numéros de base choisi
        repeat(selectedBaseCount) {
            val editText = EditText(requireContext())
            editText.hint = "Numéro ${it + 1}"
            editText.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            editText.inputType = InputType.TYPE_CLASS_NUMBER  // Ajout du clavier numérique
            numBaseContainer.addView(editText)
        }

        val dialog = builder.create()
        dialog.show()

        btnConfirm.setOnClickListener {
            val enteredNumbers = mutableListOf<String>()

            // Récupérer les numéros entrés dans les cases de saisie
            for (i in 0 until selectedBaseCount) {
                val editText = numBaseContainer.getChildAt(i) as EditText
                val enteredNumber = editText.text.toString().trim()

                if (enteredNumber.isBlank()) {
                    val errorMessage = "Veuillez entrer le ou les numéros de base."
                    showErrorMessage(errorMessage)
                    return@setOnClickListener
                }

                val baseNumber = enteredNumber.toIntOrNull()
                if (baseNumber == null || baseNumber < 1 || baseNumber > 90) {
                    val errorMessage = "Veuillez entrer un numéro de base valide entre 1 et 90."
                    showErrorMessage(errorMessage)
                    return@setOnClickListener
                }

                enteredNumbers.add(enteredNumber)
            }

            dialog.dismiss()
            val numBase = enteredNumbers.joinToString(", ")

            // Afficher la boîte de dialogue pour saisir les numéros associés
            showAssocieInput(numBase)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAssocieInput(numBase: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cocher les numéros associés")

        val inputView = layoutInflater.inflate(R.layout.fragment_dialog_num_base_input, null)
        builder.setView(inputView)

        val associeContainer = inputView.findViewById<LinearLayout>(R.id.numBaseContainer)
        val associeGridView = inputView.findViewById<GridView>(R.id.associeGridView)
        val btnConfirm = inputView.findViewById<Button>(R.id.buttonValider)

        val selectedNumbers = mutableSetOf<Int>()
        val associeAdapter = AssocieAdapter(requireContext(), (1..90).toList(), selectedNumbers)
        associeGridView.adapter = associeAdapter



        val dialog = builder.create()
        dialog.show()

        val editTextNumbers = requireView().findViewById<EditText>(R.id.editTextNumbers)
        val editTextAmount = requireView().findViewById<EditText>(R.id.editTextAmount)

        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val associe = selectedNumbers.joinToString(", ")
                editTextNumbers.setText("Base : $numBase\nAssociés : $associe\n ${s.toString()} FCFA")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnConfirm.setOnClickListener {
            dialog.dismiss()

            val associe = selectedNumbers.joinToString(", ")

            editTextAmount.isEnabled = true
            editTextAmount.requestFocus()

            // Vérifier si des associés sont sélectionnés
            if (selectedNumbers.isNotEmpty()) {
                val toastMessage = "Veuillez saisir le montant avant de continuer"
                val toast = Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

            updateAmounts(editTextAmount.text.toString())

        }
    }

    private fun updateAmounts(selectedItem: String) {


        val amountEditText = requireView().findViewById<EditText>(R.id.editTextAmount)

        val selectedNumbersCount = selectedBaseCount // Nombre d'associés choisis
        val basePrice = 10 // Prix pour chaque associé

        amountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Pas besoin d'implémenter cette méthode ici
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val enteredAmountText = s.toString()
                val enteredAmount = enteredAmountText.toIntOrNull()

                if (enteredAmount == null || enteredAmount < selectedNumbersCount * basePrice) {
                    montant = ""
                    showToast("Veuillez entrer un montant valide (minimum de ${selectedNumbersCount * basePrice} FCFA).")
                    return
                }

                val totalPrice = enteredAmount
                val maxTotalPrice = selectedNumbersCount * basePrice * 10 // Montant maximum en fonction du nombre d'associés

                if (totalPrice > maxTotalPrice) {
                    montant = ""
                    showToast("Veuillez entrer un montant inférieur ou égal à 2 fois le prix maximum de $maxTotalPrice FCFA.")
                    return
                }

                montant = "$enteredAmount FCFA"
                showToast("Montant validé : $montant")
            }

            override fun afterTextChanged(s: Editable?) {
                // Pas besoin d'implémenter cette méthode ici
            }
        })
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
// ...

    // Dans la fonction de confirmation finale
    private fun confirmFinalSelection() {
        val finalText = "Base : $numBase==> Associé : $associe\nMontant : $montant"
        val editTextNumbers = requireView().findViewById<EditText>(R.id.editTextNumbers)
        editTextNumbers.setText(finalText)
    }

    private fun showInstructionsDialog(instructions: String, onComplete: (List<String>) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Instructions pour les numéros associés")
        builder.setMessage(instructions)
        builder.setPositiveButton("Compris") { _, _ ->
            val enteredText = editTextNumbers.text.toString()
            val enteredAssociatedNumbers = enteredText.split(" ")
            onComplete(enteredAssociatedNumbers)
        }
        builder.show()
    }

// ... Le reste de votre code ...

    // Ajoutez cette fonction pour construire le texte d'instructions en fonction des éléments choisis
    private fun buildInstructions(selectedItem: String, associatedNumbers: List<String>): String {
        val baseInstructions = "Base sélectionnée : $selectedItem"
        val associatedNumbersInstructions = if (associatedNumbers.isNotEmpty()) {
            "Numéros associés : ${associatedNumbers.joinToString(" ")}"
        } else {
            "Aucun numéro associé pour le moment."
        }

        val maxAssociatedNumbers = if (selectedBaseCount == 1) 30 else 30
        val maxAssociatedNumbersInstructions = "Vous pouvez ajouter jusqu'à $maxAssociatedNumbers numéros associés."

        return "$baseInstructions\n$associatedNumbersInstructions\n$maxAssociatedNumbersInstructions"
    }


    private fun showInstructionsDialog(instructions: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Instructions pour les numéros associés")
        builder.setMessage(instructions)
        builder.setPositiveButton("Compris", null)
        //val editTextNumbers = EditText(requireContext())
        //builder.setView(editTextNumbers)
        builder.show()
    }




    private fun showErrorMessage(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Erreur")
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.show()
    }


    val combinaisons = ArrayList<Combinaison>() // Déplacer cette ligne en dehors de la fonction
    var compteur = 0 // Déclarer la variable compteur en dehors de la fonction

    fun addCombinationOnClick() {
        // Le code pour créer et ajouter la combinaison en fonction des choix du client
        buttonAddCombination.setOnClickListener {


            val productType = textViewProductType.text.toString()
            val gameTime = textViewGameTime.text.toString()
            val betType1 = spinnerBetType1.selectedItem.toString()
            val betType2 = spinnerBetType2.selectedItem.toString()
            val numbers = editTextNumbers.text.toString()
            val amount = editTextAmount.text.toString()
            //val headerTextView = layoutInflater.inflate(R.layout.header_listview, listView, false) as TextView

            val context: Context = requireContext()
            val adapter = CombinaisonAdapter(context, combinaisons)

            // Créez une instance de la vue d'entête


// Ajoutez l'entête à la ListView
            // Créez une instance de la vue d'entête
            val headerView = layoutInflater.inflate(R.layout.header_listview, listView, false) as TextView
            // Obtenez l'heure actuelle
            val currentTime = Date()

// Formatez l'heure actuelle en utilisant le format souhaité
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val time =dateTimeFormat.format(currentTime)
            val ticketReference = generateTicketReference()

// Ajoutez les informations nécessaires à l'entête

            val headerText = "$productType - $gameTime\n $time\n  Ticket Reference: $ticketReference"
            headerView.text = headerText

// Ajoutez l'entête à la ListView
// Vérifiez si l'en-tête n'a pas encore été ajouté à la liste
            if (listView.headerViewsCount == 0) {
                listView.addHeaderView(headerView)
            }

            //listView.textViewHeader.text = headerText
            listView.adapter = adapter


            // contrôle des champs vides
            if (productType.isEmpty() || gameTime.isEmpty() || betType1.isEmpty() || betType2.isEmpty() || numbers.isEmpty() || amount.isEmpty()) {
                showErrorMessage("Veuillez remplir tous les champs")
                return@setOnClickListener
            }

            val specialChar = "=>" // Caractère spécial devant les numéros
            val combination = "${specialChar}${compteur.toString().padStart(2, '0')} - $numbers"
            val combinationToAdd = "${specialChar}${compteur.toString().padStart(2, '0')} - $numbers"




            // Création du résumé à partir des détails du pari
            var summary =
                    "$betType1 ($betType2)\n" +
                    "Numéros : $numbers\n" +
                    "Montant : $amount FCFA "


// Mise à jour du résumé avec le ticketReference
          //  summary += "Ticket Reference : $ticketReference\n"


            // Ajout des détails spécifiques pour chaque type de pari
            when (betType1) {
                "Perm" -> {
                    summary += ""
                    // Ajoutez ici les détails spécifiques pour le pari de type Perm
                    // par exemple, summary += "... détails pour Perm ..."
                }
                "Two Sure" -> {
                    summary += ""
                    // Ajoutez ici les détails spécifiques pour le pari de type Two Sure
                    // par exemple, summary += "... détails pour Two Sure ..."
                }
                "Poto" -> {
                    summary += ""
                    // Ajoutez ici les détails spécifiques pour le pari de type Poto
                    // par exemple, summary += "... détails pour Poto ..."
                }
                "Base" -> {
                    summary += ""
                    // Ajoutez ici les détails spécifiques pour le pari de type Base
                    // par exemple, summary += "... détails pour Base ..."
                   // summary = summary.replace("Montant : ", "")

                }
                "Banker" -> {
                    summary += ""
                    // Ajoutez ici les détails spécifiques pour le pari de type Banker
                    // par exemple, summary += "... détails pour Banker ..."
                }
                "Vedette" -> {
                    summary += ""
                    // Ajoutez ici les détails spécifiques pour le pari de type Vedette
                    // par exemple, summary += "... détails pour Vedette ..."
                }
            }

            // Création de l'objet de combinaison avec le résumé approprié


            summary = " $summary"
            val combinaison = Combinaison(compteur, summary, "Type de produit", "Heure du match", "Type de pari 1", "Type de pari 2", combination, 10.0)
            try {
                combinaisons.add(combinaison)

                // Réorganiser les numéros de 1 à la taille de la liste après l'ajout d'une combinaison
                for (i in 0 until combinaisons.size) {
                    combinaisons[i].id = i + 1
                }

                adapter.notifyDataSetChanged()
            } catch (e: IllegalArgumentException) {
                e.message?.let { it1 -> showErrorMessage(it1) }
            }


            resetPage() // Réinitialisez la page pour chaque nouvelle sélection

        }

    }

    private fun generateTicketReference(): String {
        // Générer la référence du ticket en utilisant la date et l'heure actuelles
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
    private fun generateBarcode(reference: String) {
        // Utilisez la bibliothèque ZXing pour générer le code-barres avec la référence du ticket
        val barcodeWriter = Code128Writer()

        val bitMatrix = barcodeWriter.encode(reference, BarcodeFormat.CODE_128, 300, 150)

        val barcodeBitmap =
            Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.ARGB_8888)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                barcodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
    }

    private fun removeCombination(position: Int) {

        val context: Context = requireContext()
        val adapter = CombinaisonAdapter(context, combinaisons)
        listView.adapter = adapter
        if (position in 0 until combinaisons.size) {
            combinaisons.removeAt(position)

            // Réorganiser les numéros de 1 à 10
            for (i in 0 until combinaisons.size) {
                combinaisons[i].id = i
            }

            adapter.notifyDataSetChanged()
            compteur--

        }
    }
    private fun afficherDialoguePersonnalise() {
        // Use requireContext() to get the underlying context of the fragment
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_layout)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val listViewFullscreen = dialog.findViewById<ListView>(R.id.listViewFullscreen)
        val closeButton = dialog.findViewById<Button>(R.id.buttonClose)

        // ... Configure the ListView with the appropriate data ...

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            dialog.show()
        }
    }

    fun resetPage() {

        // Réinitialisez les Spinners en définissant leur sélection sur 0
      //  spinnerBetType1.setSelection(0)
       // spinnerBetType2.setSelection(0)
// Nettoyer la vue numberAdapter
        numberAdapter.clearSelections()
        // Réinitialisez les EditText en effaçant leur texte
        editTextAmount.text.clear()
        // Réinitialisez d'autres composants selon vos besoins
        spinnerBetType1.setSelection(0)
        spinnerBetType2.setSelection(0)
        // Réinitialisez d'autres variables de statut ou de données si nécessaire

        // Appelez les fonctions nécessaires pour réinitialiser d'autres éléments ou données spécifiques
    }


}
