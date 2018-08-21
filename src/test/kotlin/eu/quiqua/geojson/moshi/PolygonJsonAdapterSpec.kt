package eu.quiqua.geojson.moshi

import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.isA
import com.natpryce.hamkrest.present
import com.natpryce.hamkrest.throws
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import eu.quiqua.geojson.geometry.LineString
import eu.quiqua.geojson.geometry.Polygon
import eu.quiqua.geojson.geometry.Position
import eu.quiqua.geojson.geometry.Type
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.EOFException

internal class PolygonJsonAdapterSpec : Spek({
    val moshi = Moshi.Builder().add(PolygonJsonAdapter()).build()
    val adapter = moshi.adapter<Polygon>(Polygon::class.java)
    describe("From JSON to Object") {
        context("Read a valid point object String") {
            val jsonString =
                "{\"coordinates\": [[[10.0,1.0],[10.0,2.0],[11.0,2.0],[11.0,1.0],[10.0,1.0]]], \"type\": \"polygon\"}"
            it("converts to a Polygon object with two coordinates") {
                val polygon = adapter.fromJson(jsonString)!!
                com.natpryce.hamkrest.assertion.assert.that(polygon.exteriorRing?.count(), equalTo(5))
                com.natpryce.hamkrest.assertion.assert.that(polygon.type, isA<Type.Polygon>())
            }
        }
        context("Read an invalid LineString object String") {
            context("With empty string") {
                val jsonString = ""
                it("Throws a EOFException") {
                    com.natpryce.hamkrest.assertion.assert.that(
                        { adapter.fromJson(jsonString) },
                        throws<EOFException>()
                    )
                }
            }
            context("With wrong object") {
                val jsonString = "{\"foo\": 1}"
                it("Throws a JsonDataException") {
                    com.natpryce.hamkrest.assertion.assert.that(
                        { adapter.fromJson(jsonString) },
                        throws<JsonDataException>()
                    )
                }
            }
            context("With missing attribute type") {
                val jsonString = "{\"coordinates\": [1.0,1.0]}"
                it("Throws a JsonDataException") {
                    com.natpryce.hamkrest.assertion.assert.that(
                        { adapter.fromJson(jsonString) },
                        throws<JsonDataException>()
                    )
                }
            }
            context("With missing attribute coordinates") {
                val jsonString = "{\"type\": \"point\"}"
                it("Throws a JsonDataException") {
                    com.natpryce.hamkrest.assertion.assert.that(
                        { adapter.fromJson(jsonString) },
                        throws<JsonDataException>()
                    )
                }
            }
            context("With wrong attribute type") {
                val jsonString = "{\"coordinates\": [1.0,1.0], \"type\": \"linestring\"}"
                it("Throws a JsonDataException") {
                    com.natpryce.hamkrest.assertion.assert.that(
                        { adapter.fromJson(jsonString) },
                        throws<JsonDataException>()
                    )
                }
            }
            context("With wrong attribute coordinates") {
                val jsonString = "{\"coordinates\": 1.0, \"type\": \"point\"}"
                it("Throws a JsonDataException") {
                    com.natpryce.hamkrest.assertion.assert.that(
                        { adapter.fromJson(jsonString) },
                        throws<JsonDataException>()
                    )
                }
            }
            context("With out of bounds coordinates") {
                val jsonString = "{\"coordinates\": [1111.0, 1.0], \"type\": \"point\"}"
                it("Throws a JsonDataException") {
                    com.natpryce.hamkrest.assertion.assert.that(
                        { adapter.fromJson(jsonString) },
                        throws<JsonDataException>()
                    )
                }
            }
        }
    }

    describe("From Object to JSON") {
        context("Write a Polygon object") {
            val lineString = LineString(
                listOf(
                    Position(longitude = 1.0, latitude = 2.0),
                    Position(longitude = 2.0, latitude = 2.0),
                    Position(longitude = 2.0, latitude = 3.0),
                    Position(longitude = 1.0, latitude = 3.0),
                    Position(longitude = 1.0, latitude = 2.0)
                )
            )
            val polygon = Polygon(listOf(lineString.coordinates))
            it("converts to a JSON string") {
                val jsonString = adapter.toJson(polygon)
                com.natpryce.hamkrest.assertion.assert.that(
                    jsonString,
                    equalTo("{\"coordinates\":[[[1.0,2.0],[2.0,2.0],[2.0,3.0],[1.0,3.0],[1.0,2.0]]],\"type\":\"Polygon\"}")
                )
            }
        }
        context("LineString is null") {
            val polygon: Polygon? = null
            it("Throws a JsonDataException") {
                com.natpryce.hamkrest.assertion.assert.that({ adapter.toJson(polygon) }, throws<JsonDataException>())
            }
            it("Throws a specific error message") {
                val errorMessage = present(containsSubstring("java.lang.NullPointerException: Polygon was null!"))
                com.natpryce.hamkrest.assertion.assert.that(
                    { adapter.toJson(polygon) },
                    throws(has(Exception::message, errorMessage))
                )
            }
        }
    }
})