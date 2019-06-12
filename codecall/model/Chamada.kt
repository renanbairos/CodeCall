package br.com.codecall.codecall.model

import java.util.*

class Chamada {
    var dataCriacao: Date = Date()
    var idMateria: Int = 0

    constructor()

    constructor(dataCriacao: Date, idMateria: Int) {
        this.dataCriacao = dataCriacao
        this.idMateria = idMateria
    }
}
