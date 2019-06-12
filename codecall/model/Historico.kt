package br.com.codecall.codecall.model

import java.util.*

class Historico {
    var siglaMateria: String = ""
    var nomeProfessor: String = ""
    var horaCriacao: Date = Date()

    constructor()

    constructor(siglaMateria: String, nomeProfessor: String, horaCriacao: Date) {
        this.siglaMateria = siglaMateria
        this.nomeProfessor = nomeProfessor
        this.horaCriacao = horaCriacao
    }
}
