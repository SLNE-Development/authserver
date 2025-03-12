package dev.slne.authserver.types

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlin.collections.iterator

@Serializable
data class Property(
    val name: String,
    val value: String,
    val signature: String? = null
)

@Serializable(with = PropertyMapSerializer::class)
data class PropertyMap(
    val properties: MutableMap<String, MutableList<Property>> = mutableMapOf()
)

object PropertyMapSerializer : KSerializer<PropertyMap> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        mapSerialDescriptor(String.serializer().descriptor, listSerialDescriptor(Property.serializer().descriptor))

    override fun serialize(encoder: Encoder, value: PropertyMap) {
        encoder.encodeStructure(descriptor) {
            for ((key, properties) in value.properties) {
                encodeSerializableElement(descriptor, 0, String.serializer(), key)
                encodeSerializableElement(descriptor, 1, ListSerializer(Property.serializer()), properties)
            }
        }
    }

    override fun deserialize(decoder: Decoder): PropertyMap {
        return decoder.decodeStructure(descriptor) {
            val properties = mutableMapOf<String, MutableList<Property>>()
            while (true) {
                val key = decodeSerializableElement(descriptor, 0, String.serializer())
                val values = decodeSerializableElement(descriptor, 1, ListSerializer(Property.serializer()))
                properties[key] = values.toMutableList()
            }
            PropertyMap(properties)
        }
    }
}