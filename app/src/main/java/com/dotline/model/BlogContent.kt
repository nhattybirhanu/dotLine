package com.dotline.model

import java.io.Serializable

class BlogContent:Serializable {
    lateinit var id:String;
    lateinit var owner:String;
    lateinit var title:String;
    lateinit var body:String;
    var date:Long=0;
    var question:Boolean=false;
  var question_id:String?=null;
    var tags=arrayListOf<String>();
    var images=arrayListOf<String>();
    var files=arrayListOf<String>();
    var codes=arrayListOf<String>();
    var feedbacks=arrayListOf<LikeFeedBack>();
    var a_count: Long=0;
    var question_owner:String?=null;
    var closed=false;

    var replays= arrayListOf<Replay>()

    override fun equals(other: Any?): Boolean {
        if (other is BlogContent) return other.id.equals(id)
                ||(question_id!=null&&other.question_id.equals(question_id));
        return false
    }

    companion object {
        fun blog(question:Boolean,id:String,data:Map<String,Any>):BlogContent{
            var blog=BlogContent();
            try{
                blog.id = id;
                blog.question = question;
                blog.owner = data.get("owner") as String
                blog.body = data.get("body") as String
                blog.date = data.get("date") as Long
                if (data.containsKey("closed"))
                    blog.closed = data.get("closed") as Boolean;
                if (data.containsKey("tags"))
                    blog.tags = data.get("tags") as ArrayList<String>;
                if (data.containsKey("images"))
                    blog.images = data.get("images") as ArrayList<String>;
                if (data.containsKey("files"))
                    blog.files = data.get("files") as ArrayList<String>;
                if (data.containsKey("codes"))
                    blog.codes = data.get("codes") as ArrayList<String>;
                if (question) {
                    blog.title = data.get("title") as String


                    blog.a_count = data.get("a_count") as Long;

                } else {
                    if (data.containsKey("que_owner"))
                    blog.question_owner=data.get("que_owner") as String;
                    if (data.containsKey("que_id"))
                    blog.question_id=data.get("que_id") as String;
                    if (data.containsKey("replay"))
                        blog.replays = Replay.replays(data.get("replay") as Map<String, String>);
                    if (data.containsKey("feedbacks"))
                        blog.feedbacks =
                            LikeFeedBack.feedbacks(data.get("feedbacks") as Map<String, Long>)

                }
            } catch (e:Exception){

            }

            return blog;
        }
    }

    override fun toString(): String {
        return "BlogContent(id='$id', body='$body')"
    }


}