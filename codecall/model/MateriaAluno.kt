package br.com.codecall.codecall.model

import java.io.Serializable

class MateriaAluno : Serializable {
    var idAluno: String = ""
    var idMateria: String = ""

    constructor()

    constructor(idAluno: String, idMateria: String) {
        this.idAluno = idAluno
        this.idMateria = idMateria
    }
}