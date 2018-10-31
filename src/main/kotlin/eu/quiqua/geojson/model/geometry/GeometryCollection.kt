package eu.quiqua.geojson.model.geometry

import eu.quiqua.geojson.extension.getFirstErrorOrOk
import eu.quiqua.geojson.model.GeoJson

data class GeometryCollection(val geometries: List<Geometry>) : GeoJson {
    override fun validate(): ValidationResult {
        return geometries.map { it.validate() }.getFirstErrorOrOk()
    }

    override val type: Type
        get() = Type.GeometryCollection
}
