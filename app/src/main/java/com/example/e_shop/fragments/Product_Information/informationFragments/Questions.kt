package com.example.e_shop.fragments.Product_Information.informationFragments

import android.os.Binder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_shop.R
import com.example.e_shop.databinding.FragmentQuestionsBinding
import com.example.e_shop.fragments.Product_Information.informationViewmodels.QuestionsViewModel

class Questions : Fragment() {

    private lateinit var binding: FragmentQuestionsBinding

    companion object {
        fun newInstance() = Questions()
    }

    private lateinit var viewModel: QuestionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(QuestionsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}