package com.fiap.ariachallenge.data.mock

import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole

object MockUsers {

    val operador1 = User(
        id = "u1",
        name = "Rafael Costa",
        email = "rafael.costa@aguiabranca.com",
        role = UserRole.OPERADOR,
        department = "Operações - SP",
        totalIdeas = 8,
        approvedIdeas = 3
    )

    val operador2 = User(
        id = "u2",
        name = "Maria Silva",
        email = "maria.silva@aguiabranca.com",
        role = UserRole.OPERADOR,
        department = "Manutenção - RJ",
        totalIdeas = 12,
        approvedIdeas = 5
    )

    val operador3 = User(
        id = "u3",
        name = "João Ferreira",
        email = "joao.ferreira@aguiabranca.com",
        role = UserRole.OPERADOR,
        department = "Logística - MG",
        totalIdeas = 4,
        approvedIdeas = 1
    )

    val operador4 = User(
        id = "u4",
        name = "Ana Beatriz",
        email = "ana.beatriz@aguiabranca.com",
        role = UserRole.OPERADOR,
        department = "Atendimento - BA",
        totalIdeas = 6,
        approvedIdeas = 2
    )

    val gestor1 = User(
        id = "g1",
        name = "Carlos Mendes",
        email = "carlos.mendes@aguiabranca.com",
        role = UserRole.GESTOR,
        department = "Gestão de Operações"
    )

    val gestor2 = User(
        id = "g2",
        name = "Patrícia Lima",
        email = "patricia.lima@aguiabranca.com",
        role = UserRole.GESTOR,
        department = "Gestão de Tecnologia"
    )

    val lider1 = User(
        id = "l1",
        name = "Carlos Mendes",
        email = "carlos.mendes.diretor@aguiabranca.com",
        role = UserRole.LIDER,
        department = "Diretoria de Inovação"
    )

    val currentOperador = operador1
    val currentGestor = gestor1
    val currentLider = lider1

    val allUsers = listOf(operador1, operador2, operador3, operador4, gestor1, gestor2, lider1)

    fun getByRole(role: UserRole) = allUsers.filter { it.role == role }
    fun getById(id: String) = allUsers.find { it.id == id }

    val loginCredentials = mapOf(
        "operador@aria.com" to Pair("aria123", operador1),
        "gestor@aria.com"   to Pair("aria123", gestor1),
        "lider@aria.com"    to Pair("aria123", lider1),

        "rafael.costa@aguiabranca.com"           to Pair("aria123", operador1),
        "carlos.mendes@aguiabranca.com"          to Pair("aria123", gestor1),
        "carlos.mendes.diretor@aguiabranca.com"  to Pair("aria123", lider1),
    )
}
