package br.com.codecall.codecall.model

import java.util.*

class Checkin {
    var dataCriacao: Date = Date()
    var idAluno: String = ""
    var idChamada: String = ""

    constructor()

    constructor(dataCriacao: Date, idAluno: String, idChamada: String) {
        this.dataCriacao = dataCriacao
        this.idAluno = idAluno
        this.idChamada = idChamada
    }
}
