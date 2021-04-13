package com.delonagames.funnystory.activities.createsentence

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.DialogFragment
import com.delonagames.funnystory.R

class OneWordFragment : Fragment() {
    private lateinit var textView: TextView
    private lateinit var editText: EditText
    private lateinit var buttonEnter: Button

    private lateinit var createSentenceActivity: CreateSentenceActivityInterface

    companion object {
        fun newInstance(text: String): OneWordFragment {
            val bundle = Bundle().apply {
                putString("text", text)
            }

            return OneWordFragment().apply {
                arguments = bundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.one_word_fragment, container, false)

        textView = rootView.findViewById(R.id.textView)
        textView.text = arguments?.getString("text")

        editText = rootView.findViewById(R.id.editText)
        /*editText.requestFocus()
        val imm: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)*/

        buttonEnter = rootView.findViewById(R.id.buttonEnter)
        buttonEnter.setOnClickListener {
            createSentenceActivity.goNext(getWord() + " ")
        }

        return rootView
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        createSentenceActivity = context as CreateSentenceActivityInterface
    }

    private fun getWord(): String {
        return editText.text.toString()
    }

}