package dev.slne.authserver.serializer

import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

typealias SerializablePropertyMap = @Serializable(with = PropertyMapSerializer::class) PropertyMap

object PropertySerializer : KSerializer<Property> {
    override val descriptor = buildClassSerialDescriptor("Property") {
        element<String>("name")
        element<String>("value")
        element<String?>("signature")
    }

    override fun serialize(encoder: Encoder, value: Property) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeStringElement(descriptor, 1, value.value)
            value.signature?.let { encodeStringElement(descriptor, 2, it) }
        }
    }

    override fun deserialize(decoder: Decoder): Property {
        return decoder.decodeStructure(descriptor) {
            var name: String? = null
            var value: String? = null
            var signature: String? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, 0)
                    1 -> value = decodeStringElement(descriptor, 1)
                    2 -> signature = decodeStringElement(descriptor, 2)
                    -1 -> break
                    else -> throw SerializationException("Unexpected index $index")
                }
            }
            Property(
                name ?: throw SerializationException("Missing 'name'"),
                value ?: throw SerializationException("Missing 'value'"),
                signature
            )
        }
    }
}

object PropertyMapSerializer : KSerializer<PropertyMap> {
    override val descriptor: SerialDescriptor = ListSerializer(PropertySerializer).descriptor

    override fun serialize(encoder: Encoder, value: PropertyMap) {
        val propertiesList = value.asMap().flatMap { (key, properties) ->
            properties.map { property ->
                Property(property.name, property.value, property.signature)
            }
        }
        encoder.encodeSerializableValue(ListSerializer(PropertySerializer), propertiesList)
    }

    override fun deserialize(decoder: Decoder): PropertyMap {
        val propertiesList = decoder.decodeSerializableValue(ListSerializer(PropertySerializer))
        return PropertyMap().apply {
            propertiesList.forEach { property ->
                put(property.name, property)
            }
        }
    }
}
