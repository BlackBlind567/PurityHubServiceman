package com.atoms.purityhubserviceman.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.atoms.purityhubserviceman.R
import com.atoms.purityhubserviceman.databinding.ProductsLayoutBinding
import com.atoms.purityhubserviceman.fragments.ProductItemDetailFragment
import com.atoms.purityhubserviceman.model.ProductDataData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target

class ProductsAdapter(context: Context, arrayList: ArrayList<ProductDataData>) :
    BlindAdapter<ProductDataData, ProductsLayoutBinding>(context, arrayList) {
    override val layoutResId: Int
        get() = R.layout.products_layout

    override fun onBindData(
        model: ProductDataData?,
        position: Int,
        dataBinding: ProductsLayoutBinding
    ) {
//        println("modeldata == $model")
        dataBinding.title.setText(model!!.title)
        dataBinding.mrp.setText(model.mrp)
        dataBinding.price.text = "Price: " + model.price
        dataBinding.mrp.paintFlags = dataBinding.mrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        Glide.with(context).load(model.image).into(dataBinding.shapeIv)
//        dataBinding.title.setText(model.getTitle());

        //        dataBinding.title.setText(model.getTitle());
        dataBinding.addLayout.setOnClickListener { //                Intent intent = new Intent(getContext(), GenerateBillActivity.class);
//                intent.putExtra("id", String.valueOf(model.getId()));
//                intent.putExtra("title", String.valueOf(model.getTitle()));
//                intent.putExtra("price", String.valueOf(model.getPrice()));
//                getContext().startActivity(intent);
            val bottomSheet = ProductItemDetailFragment(
                model.id, model.title,
                model.price, model.mrp, model.image
            )
            bottomSheet.show(
                (context as FragmentActivity).supportFragmentManager,
                "BrandsFragment"
            )
        }
    }

    override fun onBindDataHolder(
        t: ProductDataData?,
        position: Int,
        mDataBinding: ProductsLayoutBinding,
        holder: RecyclerView.ViewHolder?
    ) {

    }

    override fun onItemClick(model: ProductDataData?, position: Int) {

    }

    fun addAll(dataResults: ArrayList<ProductDataData>) {
        for (result in dataResults) {
            arrayList.add(result)
            notifyItemInserted(arrayList.size -1)
        }
    }
}