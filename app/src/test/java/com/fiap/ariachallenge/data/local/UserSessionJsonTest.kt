package com.fiap.ariachallenge.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import com.fiap.ariachallenge.data.mock.MockUsers

class UserSessionJsonTest {

    @Test
    fun encode_decode_roundTrip() {
        val user = MockUsers.currentOperador
        val json = UserSessionJson.encode(user)
        org.junit.Assert.assertNotNull("encode must work on JVM (org.json)", json)
        assertEquals(user, UserSessionJson.decode(json!!))
    }

    @Test
    fun decode_invalid_returnsNull() {
        assertNull(UserSessionJson.decode("{not json"))
    }

    @Test
    fun allRoles_roundTrip() {
        for (u in listOf(
            MockUsers.currentOperador,
            MockUsers.currentGestor,
            MockUsers.currentLider,
        )) {
            val back = UserSessionJson.decode(UserSessionJson.encode(u)!!)
            assertEquals(u.role, back!!.role)
            assertEquals(u.id, back.id)
        }
    }
}
