package eu.quiqua.geojson.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import eu.quiqua.geojson.model.geometry.Geometry
import eu.quiqua.geojson.model.geometry.GeometryCollection
import eu.quiqua.geojson.model.geometry.LineString
import eu.quiqua.geojson.model.geometry.MultiLineString
import eu.quiqua.geojson.model.geometry.MultiPoint
import eu.quiqua.geojson.model.geometry.MultiPolygon
import eu.quiqua.geojson.model.geometry.Point
import eu.quiqua.geojson.model.geometry.Polygon
import eu.quiqua.geojson.model.geometry.Type
import eu.quiqua.geojson.model.geometry.ValidationResult
import java.lang.NullPointerException

class GeometryCollectionJsonAdapter {
    companion object {
        private const val GEOMETRIES_ATTRIBUTE = "geometries"
        private const val TYPE_ATTRIBUTE = "type"
    }

    private val options: JsonReader.Options = JsonReader.Options.of(GEOMETRIES_ATTRIBUTE, TYPE_ATTRIBUTE)

    @FromJson
    fun fromJson(
        reader: JsonReader,
        pointDelegate: JsonAdapter<Point>,
        lineStringDelegate: JsonAdapter<LineString>,
        polygonDelegate: JsonAdapter<Polygon>,
        multiPointDelegate: JsonAdapter<MultiPoint>,
        multiLineStringDelegate: JsonAdapter<MultiLineString>,
        multiPolygonDelegate: JsonAdapter<MultiPolygon>
    ): GeometryCollection {
        var type: Type? = null
        val geometries = mutableListOf<Geometry>()
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        val value = reader.readJsonValue() as Map<String, String>
                        when (value.getOrDefault(TYPE_ATTRIBUTE, "invalid").toLowerCase()) {
                            "point" -> pointDelegate.fromJsonValue(value)?.let { geometries.add(it) }
                            "linestring" -> lineStringDelegate.fromJsonValue(value)?.let { geometries.add(it) }
                            "polygon" -> polygonDelegate.fromJsonValue(value)?.let { geometries.add(it) }
                            "multipoint" -> multiPointDelegate.fromJsonValue(value)?.let { geometries.add(it) }
                            "multilinestring" -> multiLineStringDelegate.fromJsonValue(value)?.let { geometries.add(it) }
                            "multipolygon" -> multiPolygonDelegate.fromJsonValue(value)?.let { geometries.add(it) }
                        }
                    }
                    reader.endArray()
                }
                1 -> type = Type.convertFromString(reader.nextString())
                -1 -> {
                    reader.skipName()
                    reader.skipValue()
                }
            }
        }
        reader.endObject()


        if (type == null) {
            throw JsonDataException("Required type is missing at ${reader.path}")
        }
        if (type !== Type.GeometryCollection) {
            throw JsonDataException("Required type is not a GeometryCollection at ${reader.path}")
        }
        val geometryCollection = GeometryCollection(geometries)
        val validationResult = geometryCollection.validate()
        return when (validationResult) {
            is ValidationResult.Ok -> geometryCollection
            else -> throw JsonDataException(validationResult.reason)
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: GeometryCollection?) {
        if (value == null) {
            throw NullPointerException("GeometryCollection was null! Wrap in .nullSafe() to write nullable values.")
        }
        writer.beginObject()
        writer.name(GEOMETRIES_ATTRIBUTE)
        writer.beginArray()

        writer.endArray()

        writer.name(TYPE_ATTRIBUTE)
        writer.value(Type.convertToString(value.type))
        writer.endObject()
    }
}