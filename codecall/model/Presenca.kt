package br.com.codecall.codecall.model

import java.util.*

class Presenca {
    var id: Int = 0
    var usuarioCpf: String = ""
    var horaChamada: Int = 0

    constructor(usuarioCpf: String, horaChamada: Int) {
        this.usuarioCpf = usuarioCpf
        this.horaChamada = horaChamada
    }
}