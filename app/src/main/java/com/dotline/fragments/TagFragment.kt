package com.dotline.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dotline.R
import com.dotline.adapter.TagAdapter
import com.dotline.callbacks.Selected
import com.dotline.model.Tag
import com.dotline.viewModels.UserProfileModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.tag_fragment.*
import kotlinx.android.synthetic.main.tags_sign_up.view.*

class TagFragment:DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tag_fragment,container,false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var  userViewModel=ViewModelProvider(requireActivity()).get(UserProfileModel::class.java);
        userViewModel.profileLiveData().observe(this, Observer {
            profile->
            var tags= arrayListOf<Tag>();
                if (profile!=null){
                  tags = Tag.toTagList(profile.tags)
                    var adapter:TagAdapter=TagAdapter(ArrayList(tags),object :Selected{
                        override fun select(objects: Tag, selected: Boolean, poistion: Int) {

                        }

                        override fun done() {

                        }
                    }, requireActivity() as AppCompatActivity,false)
                    val layoutManager = FlexboxLayoutManager(requireContext())
                    layoutManager.flexDirection = FlexDirection.ROW
                    layoutManager.justifyContent = JustifyContent.SPACE_EVENLY
                    recyclerview.   setLayoutManager(layoutManager)
                    recyclerview.adapter=adapter;

                    edit.setEnabled(true);
                    edit.setOnClickListener {
                        var tagSelected=TagSelector();
                        tagSelected.selector=object :Selected{
                            override fun select(objects: Tag, selected: Boolean, poistion: Int) {
                                if (selected){
                                    tags.add(objects)
                                    adapter.addTag(objects)
                                }
                                else {
                                    tags.remove(objects);
                                    adapter.remove(objects)

                                }
                            }

                            override fun done() {
                                if (tags.size>2)
                                FirebaseFirestore.getInstance().collection("Users").document(profile.id).update("tags",Tag.toStringList(tags))
                            }

                        }
                        tagSelected.show(childFragmentManager,"TagFragment");
                        Handler().postDelayed(object :Runnable{
                            override fun run() {
                                tagSelected.setSelected(tags)

                            }
                        },500)

                    }
                }




        })
    }
}