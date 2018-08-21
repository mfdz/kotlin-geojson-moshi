package eu.quiqua.geojson.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import eu.quiqua.geojson.geometry.Point
import eu.quiqua.geojson.geometry.Position
import eu.quiqua.geojson.geometry.Type
import eu.quiqua.geojson.geometry.ValidationResult
import java.lang.NullPointerException

class PointJsonAdapter {
    companion object {
        private const val COORDINATES_ATTRIBUTE = "coordinates"
        private const val TYPE_ATTRIBUTE = "type"
    }

    private val options: JsonReader.Options = JsonReader.Options.of(COORDINATES_ATTRIBUTE, TYPE_ATTRIBUTE)
    private val positionJsonAdapter = PositionJsonAdapter()

    @FromJson
    fun fromJson(reader: JsonReader): Point {
        var type: Type? = null
        var position: Position? = null
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> position = positionJsonAdapter.fromJson(reader)
                1 -> type = Type.convertFromString(reader.nextString())
                -1 -> {
                    reader.skipName()
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        if (position == null) {
            throw JsonDataException("Required positions are missing at ${reader.path}")
        }
        if (type == null) {
            throw JsonDataException("Required type is missing at ${reader.path}")
        }
        if (type !== Type.Point) {
            throw JsonDataException("Required type is not a Point at ${reader.path}")
        }
        val point = Point(position)
        val validationResult = point.validate()
        return when (validationResult) {
            is ValidationResult.Ok -> point
            is ValidationResult.OutOfRangeError -> throw JsonDataException(validationResult.reason)
            else -> throw JsonDataException("Unknown error during deserialization at ${reader.path}")
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: Point?) {
        if (value == null) {
            throw NullPointerException("Point was null! Wrap in .nullSafe() to write nullable values.")
        }
        writer.beginObject()
        writer.name(COORDINATES_ATTRIBUTE)
        positionJsonAdapter.toJson(writer, value.coordinates)
        writer.name(TYPE_ATTRIBUTE)
        writer.value(Type.convertToString(value.type))
        writer.endObject()
    }
}