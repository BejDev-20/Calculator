package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.viewbinding.ViewBinding
import com.example.calculator.databinding.ActivityMainBinding
import java.lang.NumberFormatException


private const val STATE_OPERAND1_STORED = "Operand1_Stored"

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    // Variables to hold the operands and type of calculation
    private var operand1: Double? = null
    private var operand2: Double = 0.0
    private var pendingOperation = "="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val listener = View.OnClickListener { v ->
            val b = v as Button
            binding.newNumber.append(b.text)
        }

        binding.button0.setOnClickListener(listener)
        binding.button1.setOnClickListener(listener)
        binding.button2.setOnClickListener(listener)
        binding.button3.setOnClickListener(listener)
        binding.button4.setOnClickListener(listener)
        binding.button5.setOnClickListener(listener)
        binding.button6.setOnClickListener(listener)
        binding.button7.setOnClickListener(listener)
        binding.button8.setOnClickListener(listener)
        binding.button9.setOnClickListener(listener)
        binding.buttonDot.setOnClickListener(listener)

        val opListener = View.OnClickListener { v ->
            val op = (v as Button).text.toString()
            try {
                val value = binding.newNumber.text.toString().toDouble()
                performOperation(value, op)
            } catch (e: NumberFormatException){
                binding.newNumber.setText("")
            }
            pendingOperation = op
            binding.operation.text = pendingOperation
        }

        binding.buttonEquals.setOnClickListener(opListener)
        binding.buttonDivide.setOnClickListener(opListener)
        binding.buttonMultiply.setOnClickListener(opListener)
        binding.buttonMinus.setOnClickListener(opListener)
        binding.buttonPlus.setOnClickListener(opListener)
        binding.buttonNeg.setOnClickListener {
            val value = binding.newNumber.text.toString()
            if (value.isEmpty()) {
                binding.newNumber.setText("-")
            } else {
                try {
                    var doubleValue = value.toDouble()
                    doubleValue *= -1
                    binding.newNumber.setText(doubleValue.toString())
                } catch (e: NumberFormatException) {
                    //new number was "-" or "." so clear it
                    binding.newNumber.setText("")
                }
            }
        }
    }

    private fun performOperation(value: Double, operation: String){
        if (operand1 == null){
            operand1 = value
        } else {
            if (pendingOperation == "="){
                pendingOperation = operation
            }
            when (pendingOperation){
                "=" -> operand1 = value
                "/" -> operand1 = if (value == 0.0) {
                    Double.NaN //attempt to divide by 0
                } else {
                    operand1!! / value
                }
                "*" -> operand1 = operand1!! * value
                "-" -> operand1 = operand1!! - value
                "+" -> operand1 = operand1!! + value
            }
        }
        binding.result.setText(operand1.toString())
        binding.newNumber.setText("")
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        if (operand1 != null) {
            outState.putDouble("operand1", operand1!!)
        }
        outState.putString("pendingOperation", pendingOperation)
        outState.putBoolean(STATE_OPERAND1_STORED, true)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState.getBoolean(STATE_OPERAND1_STORED)) {
            operand1 = savedInstanceState.getDouble("operand1")
        } else {
            operand1 = null
        }
        pendingOperation = savedInstanceState.getString("pendingOperation").toString()
        binding.operation.setText(pendingOperation)
        binding.result.setText(operand1.toString())
    }

    inline fun <T : ViewBinding> ComponentActivity.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
    ) = lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }

}