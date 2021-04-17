package com.phpexpert.bringme.activities.employee

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutAddNewCardBinding
import com.phpexpert.bringme.utilities.BaseActivity

class NewCardActivity : BaseActivity() {
    private lateinit var cardActivityBinding: LayoutAddNewCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        cardActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_add_new_card)


        cardActivityBinding.expiryDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p1 == 1 && p1 + p3 == 2 && p0?.contains('/') == false) {
                    cardActivityBinding.expiryDate.text = Editable.Factory.getInstance().newEditable("$p0/")
                } else if (p1 == 3 && p1 - p2 == 2 && p0?.contains('/') == true) {
                    cardActivityBinding.expiryDate.text = Editable.Factory.getInstance().newEditable(p0.toString().replace("/", ""))
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        cardActivityBinding.cardNumber.addTextChangedListener(object : TextWatcher {
            private val TOTAL_SYMBOLS = 19 // size of pattern 0000-0000-0000-0000

            private val TOTAL_DIGITS = 16 // max numbers of digits in pattern: 0000 x 4

            private val DIVIDER_MODULO = 5 // means divider position is every 5th symbol beginning with 1

            private val DIVIDER_POSITION = DIVIDER_MODULO - 1 // means divider position is every 4th symbol beginning with 0

            private val DIVIDER = '-'
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!isInputCorrect(s!!, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length, buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER))
                }
            }

            private fun isInputCorrect(s: Editable, totalSymbols: Int, dividerModulo: Int, divider: Char): Boolean {
                var isCorrect = s.length <= totalSymbols // check size of entered string
                for (i in s.indices) { // check that every element is right
                    isCorrect = if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect and (divider == s[i])
                    } else {
                        isCorrect and Character.isDigit(s[i])
                    }
                }
                return isCorrect
            }

            private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String {
                val formatted = StringBuilder()
                for (i in digits.indices) {
                    if (!digits[i].equals(0)) {
                        formatted.append(digits[i])
                        if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                            formatted.append(divider)
                        }
                    }
                }
                return formatted.toString()
            }

            private fun getDigitArray(s: Editable, size: Int): CharArray {
                val digits = CharArray(size)
                var index = 0
                var i = 0
                while (i < s.length && index < size) {
                    val current = s[i]
                    if (Character.isDigit(current)) {
                        digits[index] = current
                        index++
                    }
                    i++
                }
                return digits
            }
        })

        cardActivityBinding.payNowButton.setOnClickListener {
            cardActivityBinding.payNowButton.startAnimation()
            Handler().postDelayed({
                startActivity(Intent(this, CongratulationScreen::class.java))
            }, 1000)
        }

        cardActivityBinding.backLayout.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    override fun onPause() {
        super.onPause()
        cardActivityBinding.payNowButton.revertAnimation()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
}