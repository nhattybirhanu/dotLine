package com.dotline.model

class Replay {
    lateinit var id:String;
    lateinit var message:String;

    constructor(id: String, message: String) {
        this.id = id
        this.message = message
    }
    companion object{
        fun replays(replayData:Map<String,String>):ArrayList<Replay>{
            var  replay= arrayListOf<Replay>();
                for (key in replayData.keys){
                    replay.add(Replay(key, replayData.get(key)!!))
                }
            return replay;
        }
    }
}