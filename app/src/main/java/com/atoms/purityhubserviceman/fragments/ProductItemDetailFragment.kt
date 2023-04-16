package com.atoms.purityhubserviceman.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.databinding.FragmentProductItemDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ProductItemDetailFragment(
    Id: Int,
    var Title: String,
    var Price: String,
    var mrp: String,
    var image: String
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentProductItemDetailBinding
    var itemQuantity = ""
    var callback: OnAddItemListener? = null
    var productId = Id
    interface OnAddItemListener{
        fun onAddItem(
            itemId: String,
            itemName: String,
            itemPrice: String,
            itemQuantity: String,
            totalItemPrice: Int,
            itemMrp: String,
            itemImage: String)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_item_detail, container, false)

        binding.itemName.text = Title
        binding.itemPrice.text = "\u20B9" +Price

        binding.addItem.setOnClickListener {
            itemQuantity = binding.itemQuantity.text.toString()
            if (itemQuantity.isEmpty()){
                Toast.makeText(requireContext(), "Please enter quantity", Toast.LENGTH_SHORT).show()
            }else {
                val totalItemPrice = Price.toInt() * itemQuantity.toInt()
                callback!!.onAddItem(productId.toString(), Title, Price,
                    itemQuantity,totalItemPrice, mrp, image)
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