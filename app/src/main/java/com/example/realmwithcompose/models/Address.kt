package com.example.realmwithcompose.models

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


// Teacher -> Address one to one relation
// Teacher -> Course one to many
// Students -> Course many to many



class Address:EmbeddedRealmObject {
   //@PrimaryKey var id : ObjectId = ObjectId()
    var fullName: String = ""
    var street: String = ""
    var houseNumber: Int = 0
    var zipCode: Int = 0
    var city: String = ""
    var teacher: Teacher? =null

}