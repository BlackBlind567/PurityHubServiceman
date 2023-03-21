package com.atoms.purityhubserviceman.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.databinding.FragmentProductItemDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ProductItemDetailFragment(Id: Int, var Title: String, var Price: String) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentProductItemDetailBinding
    var itemQuantity = ""
    var callback: OnAddItemListener? = null
    var productId = Id
    interface OnAddItemListener{
        fun onAddItem(itemId: String,
            itemName: String,
        itemPrice: String,
        itemQuantity: String,)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_item_detail, container, false)


        binding.addItem.setOnClickListener {
            itemQuantity = binding.itemQuantity.text.toString()
            if (itemQuantity.isEmpty()){
                Toast.makeText(requireContext(), "Please enter quantity", Toast.LENGTH_SHORT).show()
            }else {
                callback!!.onAddItem(productId.toString(), Title, Price, itemQuantity)
            }

        }

        return binding.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        callback = try {
            activity as OnAddItemListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement OnAddItemListener"
            )
        }
    }
}