package com.github.xtermi2.virtualrun.model

const val USER_STRING = "USER"
const val ADMIN_STRING = "ADMIN"

enum class SecurityRole(val text: String) {
    USER(USER_STRING),
    ADMIN(ADMIN_STRING);
}