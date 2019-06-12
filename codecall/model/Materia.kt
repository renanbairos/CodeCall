package br.com.codecall.codecall.model

import java.io.Serializable
import java.time.chrono.ChronoPeriod
import java.util.*

class Materia : Serializable {
    var idCurso: Int = 0
    var idMateria: Int = 0
    var idProfessor: String = ""
    var nome: String = ""
    var periodo: String = ""
    var sigla: String = ""

    constructor()

    constructor(idCurso: Int, idMateria: Int, idProfessor: String, nome: String, periodo: String, sigla: String) {
        this.idCurso = idCurso
        this.idMateria = idMateria
        this.idProfessor = idProfessor
        this.nome = nome
        this.periodo = periodo
        this.sigla = sigla
    }
}
