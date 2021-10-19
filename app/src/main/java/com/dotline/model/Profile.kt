package com.dotline.model

class Profile {
    lateinit var id:String
    lateinit var userName:String
    lateinit var displayName:String
    var email:String=""
    lateinit var department:String
    var verfied:Boolean=false
    lateinit var tags:ArrayList<String>
    var fav_questions=ArrayList<String>()
    var selected:Boolean=false
    var following= arrayListOf<String>()
    var followers:Long=0
    var profile_picture:String?=null;
    companion object{
        fun instance( map:Map<String,Any>,uid:String):Profile{
         var profile=Profile()
            profile.id=uid
            if (map.containsKey("username"))
            profile.userName= map.get("username") as String
            if (map.containsKey("displayname"))
            profile.displayName= map.get("displayname") as String
            if (map.containsKey("profile_picture"))
                profile.profile_picture= map.get("profile_picture") as String

            if (map.containsKey("email"))
            profile.email= map.get("email") as String

            if (map.containsKey("following"))
                profile.following= map.get("following") as ArrayList<String>

            if (map.containsKey("followers"))
                profile.followers= map.get("followers") as Long

            if (map.containsKey("verified"))
            profile.verfied= map.get("verified") as Boolean
            if (map.containsKey("department"))
            profile.department= map.get("department") as String
            if (map.containsKey("tags"))
            profile.tags=map.get("tags") as ArrayList<String>
            if (map.containsKey("fav_q"))
                profile.fav_questions=map.get("fav_q") as ArrayList<String>
            return  profile
        }

    }
}