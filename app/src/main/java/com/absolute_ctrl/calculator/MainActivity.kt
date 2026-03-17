package com.absolute_ctrl.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var firstOperand: Double = 0.0
    private var operator: String? = null
    private var currentInput: String = "0"

    private var resetInputOnNextDigit: Boolean = false

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)

        val digitButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in digitButtons) {
            findViewById<Button>(id).setOnClickListener {
                val digit = (it as Button).text.toString()
                onDigitPressed(digit)
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener {
            onDotPressed()
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener { onOperatorPressed("+") }
        findViewById<Button>(R.id.btnSub).setOnClickListener { onOperatorPressed("−") }
        findViewById<Button>(R.id.btnMul).setOnClickListener { onOperatorPressed("×") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { onOperatorPressed("÷") }

        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            onEqualsPressed()
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            onClearPressed()
        }

        findViewById<Button>(R.id.btnSign).setOnClickListener {
            onSignPressed()
        }

        findViewById<Button>(R.id.btnPercent).setOnClickListener {
            onPercentPressed()
        }
    }

    private fun onDigitPressed(digit: String) {
        if (resetInputOnNextDigit) {
            // Start fresh after an operator or equals
            currentInput = digit
            resetInputOnNextDigit = false
        } else {
            // Append — but don't let "0" grow into "007"
            if (currentInput == "0") {
                currentInput = digit
            } else {
                currentInput += digit
            }
        }
        updateDisplay()
    }

    private fun onDotPressed() {
        if (resetInputOnNextDigit) {
            currentInput = "0."
            resetInputOnNextDigit = false
        } else if (!currentInput.contains(".")) {
            currentInput += "."
        }
        updateDisplay()
    }

    private fun onOperatorPressed(newOperator: String) {
        // If there's already a pending operator, calculate first (chaining: 3 + 5 + ...)
        if (operator != null && !resetInputOnNextDigit) {
            calculateResult()
        } else {
            firstOperand = currentInput.toDouble()
        }

        operator = newOperator
        resetInputOnNextDigit = true

        // Show the expression so far: "42 +"
        tvExpression.text = "${formatNumber(firstOperand)} $newOperator"
    }

    private fun onEqualsPressed() {
        if (operator == null) return  // nothing to calculate

        val secondOperand = currentInput.toDouble()
        tvExpression.text = "${formatNumber(firstOperand)} $operator ${formatNumber(secondOperand)} ="

        calculateResult()
        operator = null  // clear the pending operation
    }

    private fun onClearPressed() {
        firstOperand = 0.0
        operator = null
        currentInput = "0"
        resetInputOnNextDigit = false
        tvExpression.text = ""
        updateDisplay()
    }

    private fun onSignPressed() {
        if (currentInput == "0") return
        currentInput = if (currentInput.startsWith("-")) {
            currentInput.substring(1)
        } else {
            "-$currentInput"
        }
        updateDisplay()
    }

    private fun onPercentPressed() {
        val value = currentInput.toDouble() / 100.0
        currentInput = formatNumber(value)
        updateDisplay()
    }

    private fun calculateResult() {
        val secondOperand = currentInput.toDouble()

        val result = when (operator) {
            "+"  -> firstOperand + secondOperand
            "−"  -> firstOperand - secondOperand
            "×"  -> firstOperand * secondOperand
            "÷"  -> {
                if (secondOperand == 0.0) {
                    // Division by zero — show error, then reset
                    tvResult.text = "Error"
                    currentInput = "0"
                    firstOperand = 0.0
                    operator = null
                    resetInputOnNextDigit = true
                    return  // bail out early
                }
                firstOperand / secondOperand
            }
            else -> return
        }

        // Store the result so the user can chain operations
        firstOperand = result
        currentInput = formatNumber(result)
        resetInputOnNextDigit = true
        updateDisplay()
    }

    private fun updateDisplay() {
        tvResult.text = currentInput
    }

    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}