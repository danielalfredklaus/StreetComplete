package de.westnordost.accesscomplete.ktx

import de.westnordost.accesscomplete.util.Serializer

inline fun <reified T> Serializer.toObject(bytes: ByteArray):T = toObject(bytes, T::class.java)
