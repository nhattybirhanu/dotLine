package com.dotline.model

data class Tag(var name:String,var selected:Boolean){

    override fun equals(other: Any?): Boolean {
        if (other is Tag){
            return             other.name.equals(name);

        }
        return false
    }

    companion object demoTags{
        var  tags= arrayListOf<Tag>(Tag("Spring boot",false),
            Tag("Hibernate",false),
            Tag("Microservice",false),
            Tag("React Js",false),
            Tag("Redux",false),
            Tag("Node",false),
            Tag("Express",false),
            Tag("Angular",false),
            Tag("HTML",false),
            Tag("JavaScript",false),
            Tag("CSS",false),
            Tag("JQuery",false),
            Tag("Eureka",false),
            Tag("Big Data",false),
            Tag("Android",false),
            Tag("Hooks",false),
            Tag("JPA",false),
            Tag("Java",false),
            Tag("Kotlin",false),
            Tag("Container",false),
            Tag("Docker",false),
            Tag("Kubernetes",false),
            Tag("Data structure",false),
            Tag("Algorithms",false),
            Tag("MySql",false),
            Tag("Postgress",false),

            )
        fun  toStringList(tags:ArrayList<Tag>):ArrayList<String> {
            var list=ArrayList<String>();
            tags.forEach { list.add(it.name) }
            return list
        }
        fun  toTagList(tags:ArrayList<String>):ArrayList<Tag> {
            var list=ArrayList<Tag>();
            tags.forEach { list.add(Tag(it,false)) }
            return list
        }
    }

    override fun toString(): String {
        return name;
    }


}
