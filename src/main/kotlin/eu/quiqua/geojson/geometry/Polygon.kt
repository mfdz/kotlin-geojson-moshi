package eu.quiqua.geojson.geometry

data class Polygon(val coordinates: List<List<Position>>) : Geometry {
    override fun validate(): ValidationResult = GeometryValidation.isPolygon(coordinates)

    override val type: Type
        get() = Type.Polygon

    val exteriorRing: List<Position>?
        get() = if (coordinates.isEmpty()) null else coordinates.first()

    val interiorRings: List<List<Position>>?
        get() = if (coordinates.size < 2) null else coordinates.subList(1, coordinates.size)
}
