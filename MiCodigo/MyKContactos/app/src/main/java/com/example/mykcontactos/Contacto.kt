package com.example.mykcontactos

class Contacto {
    var idcontacto:Int = 0
    var nombre:String = ""
    var alias:String = ""
    var codigo:String = ""

    //Constructor
    constructor(idcontacto: Int, nombre: String, alias: String, codigo: String) {
        this.idcontacto = idcontacto
        this.nombre = nombre
        this.alias = alias
        this.codigo = codigo
    }
}