package com.dotline.model

class Selector {
    var id:String;
    var title:String;
    lateinit var path:String;
    var  remote=false;

    constructor(id: String, title: String) {
        this.id = id
        this.title = title
    }

    constructor(id: String, title: String, path: String) {
        this.id = id
        this.title = title
        this.path = path
    }

    constructor(id: String, title: String, path: String, remote: Boolean) {
        this.id = id
        this.title = title
        this.path = path
        this.remote = remote
    }


    companion object{
        fun tagconverter(tags:ArrayList<Tag>):ArrayList<Selector>{
            var  selectors=ArrayList<Selector>();
            tags.forEach { selectors.add(Selector("",it.name)) }
        return selectors;
        }
        fun toStirngArray(selectors:ArrayList<Selector>):ArrayList<String>{
            var names=ArrayList<String>();
            selectors.forEach { names.add(it.title) }
        return names
        }
        fun toProfileString(selectors: ArrayList<Profile>):ArrayList<Selector>{
            var selectedProfile= arrayListOf<Selector>()
            selectors.forEach {
                selectedProfile.add(Selector(it.id,it.displayName));
            }
            return  selectedProfile;
        }
    }

    override fun toString(): String {
        return  title
    }
}