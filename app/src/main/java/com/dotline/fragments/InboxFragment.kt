package com.dotline.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dotline.Custom.DividerItem
import com.dotline.R
import com.dotline.adapter.InboxAdapter
import com.dotline.viewModels.HomeViewModel
import com.dotline.viewModels.InboxViewModel
import kotlinx.android.synthetic.main.inbox_layout.*

class InboxFragment:Fragment() {
   lateinit var adapter:InboxAdapter
   lateinit var inboxViewModel:InboxViewModel;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.inbox_layout,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter= InboxAdapter(activity as AppCompatActivity, arrayListOf())
        recyclerview.layoutManager=LinearLayoutManager(context);
        recyclerview.addItemDecoration(DividerItem(requireContext(),resources.getDrawable(R.drawable.list_divider)))
        recyclerview.adapter=adapter;
       inboxViewModel= ViewModelProvider(requireActivity()).get(InboxViewModel::class.java);
       inboxViewModel.liveInbox().observe(viewLifecycleOwner, Observer { inboxs->
            adapter.addAllInbox(inboxs!!);

        });

    }
}