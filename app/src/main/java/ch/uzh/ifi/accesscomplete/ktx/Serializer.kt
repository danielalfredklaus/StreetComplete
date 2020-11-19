package ch.uzh.ifi.accesscomplete.ktx

import ch.uzh.ifi.accesscomplete.util.Serializer

inline fun <reified T> Serializer.toObject(bytes: ByteArray):T = toObject(bytes, T::class.java)
