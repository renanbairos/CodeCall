package br.com.codecall.codecall.model

class Usuario {
    var nome: String = ""
    var authID: String = ""
    var cpf: String = ""
    var tipoUsuario: Int = 0

    constructor()

    constructor(nome: String, authID: String, cpf: String, tipoUsuario: Int) {
        this.nome = nome
        this.authID = authID
        this.cpf = cpf
        this.tipoUsuario = tipoUsuario
    }
}