package br.com.codecall.codecall.model

import java.util.*

class Chamada {
    var dataCriacao: Date = Date()
    var idMateria: String = ""

    constructor()

    constructor(dataCriacao: Date, idMateria: String) {
        this.dataCriacao = dataCriacao
        this.idMateria = idMateria
    }
}
