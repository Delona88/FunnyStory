package com.delonagames.funnystory

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class OneWordFragment : Fragment() {
    private lateinit var textView: TextView

    private lateinit var editText: EditText

    private lateinit var buttonEnter: Button

    private lateinit var mainActivity: MainActivityInterface

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

        buttonEnter = rootView.findViewById(R.id.buttonEnter)
        buttonEnter.setOnClickListener(onClickListener)

        return rootView
    }

    private val onClickListener = OnClickListener {
        when (it.id) {
            buttonEnter.id -> mainActivity.goNext(getWord() + " ")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivityInterface
    }

    private fun getWord(): String {
        return editText.text.toString()
    }


}