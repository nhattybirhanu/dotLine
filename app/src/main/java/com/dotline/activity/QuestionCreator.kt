package com.dotline.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dotline.R
import com.dotline.UploadManager
import com.dotline.Util.QuestionCreatorUtil
import com.dotline.Util.QuestionCreatorUtil.Companion.compreesImages
import com.dotline.Util.QuestionCreatorUtil.Companion.uris
import com.dotline.adapter.ImageAdapter
import com.dotline.adapter.TextSelectorAdapter
import com.dotline.callbacks.Selected
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.fragments.SearchFragment
import com.dotline.fragments.TagSelector
import com.dotline.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import id.zelory.compressor.constraint.*
import kotlinx.android.synthetic.main.post_editor.*
import kotlinx.android.synthetic.main.post_editor.body
import kotlinx.android.synthetic.main.post_editor.file_list
import kotlinx.android.synthetic.main.post_editor.toolbar
import kotlinx.android.synthetic.main.question_detail.*
import kotlinx.android.synthetic.main.sign_up.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class QuestionCreator:AppCompatActivity() {
    lateinit var imageAdapter:ImageAdapter;
    lateinit var tagAdapter:TextSelectorAdapter;
    lateinit var fileAdapter:TextSelectorAdapter;
    lateinit var mention:TextSelectorAdapter;
    var tags=ArrayList<Tag>();
    var tagSelector:TagSelector?=null
    var question:Boolean=true;
     var linkQuestion:String?=null;
    var questionOwner:String?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_editor)
      list_adapter()
      question=  intent.getBooleanExtra("question",true);
        linkQuestion=  intent.getStringExtra("question_id");
        questionOwner=  intent.getStringExtra("question_owner");
    if (!question){
        qtitle_layout.setVisibility(View.GONE);
        toolbar.setTitle("Answer")
        body_layout.hint="Answer body";
        mention_btn.visibility=View.GONE;
        tag_btn.visibility=View.GONE;
    }
        toolbar.setNavigationOnClickListener {
            close()
        }

    }

    fun close(){
        var dialog=AlertDialog.Builder(this);
        dialog.setTitle("Do you want close ?")
        dialog.setPositiveButton("Close", DialogInterface.OnClickListener(){
            dialog, which ->
            dialog.dismiss()
            finish()
        })
        dialog.setNegativeButton("Cancle", DialogInterface.OnClickListener(){
                dialog, which ->
            dialog.dismiss()
        })
        dialog.setNeutralButton("Save and cancle", DialogInterface.OnClickListener(){
                dialog, which ->
            dialog.dismiss()
            finish()

        })
        dialog.show()
    }
    fun  list_adapter(){
        imageAdapter= ImageAdapter(this,ArrayList(), ImageConfig(200,200,false,true),null);
        media_list.layoutManager=layoutManager();
        media_list.setHasFixedSize(true);
        media_list.adapter=imageAdapter;
        tagAdapter= TextSelectorAdapter(ArrayList());
        tag_list.layoutManager=layoutManager();
        tag_list.setHasFixedSize(true);
        tag_list.adapter=tagAdapter;

        fileAdapter= TextSelectorAdapter(ArrayList());
        file_list.layoutManager=layoutManager();
        file_list.setHasFixedSize(true);
        file_list.adapter=fileAdapter;

        mention= TextSelectorAdapter(ArrayList());
        mentions_list.layoutManager=layoutManager();
        mentions_list.setHasFixedSize(true);
        mentions_list.adapter=mention;

    }
    fun mention(view:View){
        var search=SearchFragment();
        var bundle=Bundle();
        bundle.putBoolean("custom",true);
        search.arguments=bundle;
        mention_title.visibility=View.VISIBLE;
        search.callback= object : UserProfileCallBack() {
            override fun profiles(profiles: ArrayList<Profile>) {
                    mention.addAll(Selector.toProfileString(profiles));

                super.profiles(profiles)
            }
        }
        search.show(supportFragmentManager,null)
    }
    fun layoutManager():RecyclerView.LayoutManager{
        return       LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode== RESULT_OK){
        if (requestCode==1){
            if (data?.clipData!=null) addImage(ImageModel.build(uris(data.clipData!!)))
            else
            if (data?.data!=null) addImage(ImageModel.build(arrayListOf(data?.data as Any)))

        } else if (requestCode==2){
            if (data?.clipData!=null)
                addFiles(QuestionCreatorUtil.urisToSelector(data.clipData!!,this@QuestionCreator))
                else
                if (data?.data!=null)
                {
                    var file=QuestionCreatorUtil.uriToFile(data.data!!,this);
                    if (file!=null){
                        addFiles(arrayListOf(Selector("",file.name,file.path)))
                    }
                }

        }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun pickIMage(view: View){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }
    fun pickDocument(view: View){
        val mimeTypes = arrayOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .doc & .docx
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",  // .ppt & .pptx
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // .xls & .xlsx
            "text/plain",
            "application/pdf",
        )
        val intent = Intent()
        intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
        if (mimeTypes.size > 0) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Document"), 2)
    }
    fun addImage(images:ArrayList<ImageModel>){
        Log.i("Picker", "onActivityResult: $images")

        imageAdapter.addImages(images)
        media_title.setVisibility(if(imageAdapter.images.size>0) View.VISIBLE else View.GONE)
    }
    fun addFiles(files:ArrayList<Selector>){
        fileAdapter.update(files)
        file_title.setVisibility(if(fileAdapter.selector.size>0) View.VISIBLE else View.GONE)
    }

    fun openTag(view: View){
        if (tagSelector==null){
            tagSelector= TagSelector();
            tagSelector?.selector=object:Selected{
                override fun select(objects: Tag, selected: Boolean, poistion: Int) {
                        if (selected) tags.add(objects) else tags.remove(objects)

                }

                override fun done() {
                    tag_title.setVisibility(if(tags.size>0) View.VISIBLE else View.GONE)
                    tagAdapter.update(Selector.tagconverter(tags));
                    Log.i("tags","${Selector.tagconverter(tags)}")

                }
            }


        }
        tagSelector?.show(supportFragmentManager,"Tags")
    }


    fun send(view:View){
        qtitle_layout.error=null;
        var dbPath=if (question) "Questions" else "Answers";
        body_layout.error=null;
        if(question&&TextUtils.isEmpty(qtitle.text)){
            qtitle_layout.error="Please mention question title shortly"
            return;
        }
        if(TextUtils.isEmpty(body.text)){
            body_layout.error="Please mention question title shortly"
            return;
        }
        if (question&&tags.isEmpty()){
            showMessage("Tag your question at least one")
            return
        }
        if (!question&&linkQuestion==null){
            showMessage("Please link a question for answer ")
            return
        }
        var uris =ArrayList<Uri>();
        imageAdapter.images.forEach{
            if (it.src is Uri)
                uris.add(it.src as Uri)
        }

        var files=ArrayList<String>();
        fileAdapter.selector.forEach { files.add(it.path) }

        var progess=ProgressDialog(this@QuestionCreator)
        progess.setTitle("Posting")
        progess.setCancelable(false)
        if (uris.isNotEmpty())
        progess.setMessage("Compressing")

        progess.show();


        GlobalScope.launch {
            if (Looper.myLooper() == null)
             Looper.prepare();
                        var images= compreesImages(uris,this@QuestionCreator,)
            progess.setMessage("Uploading")
            UploadManager(dbPath,images,
                files,ArrayList(),object:UploadManager.Task{
                override fun taskisdone(done: Boolean, data: Map<String, ArrayList<String>>) {
                    if (done){
                        Log.i("Uploading","$data")
                        progess.setMessage("Posting in wall")
                        publish(false,question,null,data,progess,linkQuestion)
                    } else {
                        progess.cancel()
                        showMessage("Posting Failed")
                    }

                }
            })


        }

    }
    fun showMessage(message:String): Snackbar {
        var  snack= Snackbar.make(toolbar,message, Snackbar.LENGTH_SHORT);

        snack.show();
        return snack;
    }
    fun publish(
        update: Boolean,
        forquestion: Boolean,
        old_id: String?,
        data: Map<String, ArrayList<String>>,
        progess: ProgressDialog,
        question_id:String?
    ){
        var user=FirebaseAuth.getInstance().currentUser;
        if (user==null) {
            showMessage("Please sign in first")
            return
        }
        var content=HashMap<String,Any>();
     if (forquestion)
        {
            content.put("title",qtitle.text.toString());
            content.put("a_count",0);
            if (mention.selector.isNotEmpty()) content.put("mentions",mention.selector.map { selector -> selector.id }.toList())

        }
        else{
         content.put("que_id",question_id!!);
         content.put("que_owner",questionOwner!!);

        }
        content.put("body",body.text.toString());
        content.put("date",Timestamp.now().seconds)
        content.put("tags",Selector.toStirngArray(tagAdapter.selector));
        content.put("owner",user.uid);
        for (key in data.keys)
        {
            data.get(key)?.let { content.put(key, it) }

        }
       if(forquestion) {
            var collection = FirebaseFirestore.getInstance()
                .collection("Questions")
            var document: DocumentReference? = null;
            if (update) document = old_id?.let { collection.document(it) }
            else document = collection.document();
            document?.set(content, SetOptions.merge())?.addOnCompleteListener {
                runOnUiThread {

                    progess.dismiss()
                    finish()
                }
            };
        } else {
            val database=FirebaseDatabase.getInstance().reference.child("Answers");
           database.push().setValue(content).addOnSuccessListener {
               runOnUiThread {
                   if (!forquestion) {
                       FirebaseFirestore.getInstance().collection("Questions")
                           .document(question_id!!)
                           .update("a_count", FieldValue.increment(1));
                   }
                   progess.dismiss()
                   finish()
               }
           }
        }
    }



}