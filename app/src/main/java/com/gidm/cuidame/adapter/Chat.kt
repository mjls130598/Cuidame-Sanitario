package com.gidm.cuidame.adapter

import java.util.*

data class Chat(val uid: String, val mensaje: String, val emisor: String,
                val receptor: String, val fecha: Date)
