package com.github.xtermi2.virtualrun.repository.mongo.codec

import com.github.xtermi2.virtualrun.model.ActivityId
import com.github.xtermi2.virtualrun.model.UserId
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry

class ActivityIdCodec : Codec<ActivityId> {
    override fun getEncoderClass() = ActivityId::class.java

    override fun encode(writer: BsonWriter, value: ActivityId, encoderContext: EncoderContext) =
            writer.writeString(value.id)

    override fun decode(reader: BsonReader, decoderContext: DecoderContext) =
            when (reader.currentBsonType) {
                BsonType.OBJECT_ID -> ActivityId(reader.readObjectId())
                BsonType.STRING -> ActivityId(reader.readString())
                else -> throw IllegalStateException("unsupported BsonType: ${reader.currentBsonType}")
            }
}

class UserIdCodec : Codec<UserId> {
    override fun getEncoderClass() = UserId::class.java

    override fun encode(writer: BsonWriter, value: UserId, encoderContext: EncoderContext) =
            writer.writeString(value.id)

    override fun decode(reader: BsonReader, decoderContext: DecoderContext) =
            when (reader.currentBsonType) {
                BsonType.OBJECT_ID -> UserId(reader.readObjectId())
                BsonType.STRING -> UserId(reader.readString())
                else -> throw IllegalStateException("unsupported BsonType: ${reader.currentBsonType}")
            }
}

/**
 * This Provider is called automatically by Quarkus
 */
class ObjectIdCodecProvider : CodecProvider {
    override fun <T : Any?> get(clazz: Class<T>?, registry: CodecRegistry?): Codec<T>? {
        return if (clazz === ActivityId::class.java) {
            ActivityIdCodec() as Codec<T>
        } else if (clazz === UserId::class.java) {
            UserIdCodec() as Codec<T>
        } else null
    }
}