package no.uio.ifi.in2000.team8.data.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import no.uio.ifi.in2000.team8.Settings
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer: Serializer<Settings> {
    override val defaultValue: Settings = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.parseFrom(input)
        }catch (e: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto,", e)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream ) {
        t.writeTo(output)
    }
}