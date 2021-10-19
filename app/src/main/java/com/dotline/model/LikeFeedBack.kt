package com.dotline.model

class LikeFeedBack {
    lateinit var id:String;
    var  feedBack:Long=-1;

    constructor(id: String, feedBack: Long) {
        this.id = id
        this.feedBack = feedBack
    }

    companion object{
        fun feedbacks(replayData:Map<String,Long>):ArrayList<LikeFeedBack>{
            var  replay= arrayListOf<LikeFeedBack>();
            for (key in replayData.keys){
                replay.add(LikeFeedBack(key, replayData.get(key)!!))
            }
            return replay;
        }
        fun getUserFeedBack(user_id:String,feedBacks: ArrayList<LikeFeedBack>):Long{
            var feedback:Long=-1;
            feedBacks.forEach { if (it.id.equals(user_id)) return it.feedBack }
        return feedback
        }
        fun getCountForFeedBack(feedBack:Long,allfeedBacks: ArrayList<LikeFeedBack>):Long{
            var count:Long=0;
            allfeedBacks.forEach { if (feedBack==it.feedBack) count++ }
            return count;
        }
        fun removeFeedBackFor(user_id:String,feedBacks: ArrayList<LikeFeedBack>):ArrayList<LikeFeedBack>{
            for ((pos,feed) in feedBacks.withIndex()){
                if (feed.id.equals(user_id)) feedBacks.removeAt(pos)
            }
            return feedBacks
        }

    }

}