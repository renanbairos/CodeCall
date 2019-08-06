package br.com.codecall.codecall.model

import java.io.Serializable

class Materia : Serializable {
    var idCurso: String = ""
    var idProfessor: String = ""
    var nome: String = ""
    var periodo: String = ""
    var sigla: String = ""

    constructor()

    constructor(idCurso: String, idProfessor: String, nome: String, periodo: String, sigla: String) {
        this.idCurso = idCurso
        this.idProfessor = idProfessor
        this.nome = nome
        this.periodo = periodo
        this.sigla = sigla
    }
}
