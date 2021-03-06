package eu.quiqua.geojson.model.geometry

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class ValidationResultSpec : Spek({
    describe("A ValidationResult.Ok object") {
        it("Can be created without any arguments") {
            assert.that(ValidationResult.Ok(), present())
        }
    }

    describe("A Validation.Error object") {
        val reason = "foo"
        context("OutOfRange") {
            val outOfRange = ValidationResult.Error.OutOfRange(reason = reason)
            it("Has a reason why the validation failed") {
                assert.that(outOfRange.reason, equalTo(reason))
            }
        }
        context("TooFewElements") {
            val tooFewElements = ValidationResult.Error.TooFewElements(reason = reason)
            it("Has a reason why the validation failed") {
                assert.that(tooFewElements.reason, equalTo(reason))
            }
        }
        context("IncompatibleCoordinateDimensions") {
            val incompatibleDimensions = ValidationResult.Error.IncompatibleCoordinateDimensions(reason = reason)
            it("Has a reason why the validation failed") {
                assert.that(incompatibleDimensions.reason, equalTo(reason))
            }
        }
        context("NoLinearRing") {
            val noLinearRing = ValidationResult.Error.NoLinearRing(reason = reason)
            it("Has a reason why the validation failed") {
                assert.that(noLinearRing.reason, equalTo(reason))
            }
        }
    }
    describe("A Validation.Warning object") {
        val reason = "bar"
        context("AvoidSingleGeometry") {
            val avoidSingleGeometry = ValidationResult.Warning.AvoidSingleGeometry(reason = reason)
            it("Has a reason why the validation failed") {
                assert.that(avoidSingleGeometry.reason, equalTo(reason))
            }
        }
        context("AvoidNestedGeometryCollections") {
            val avoidNestedGeometryCollection = ValidationResult.Warning.AvoidNestedGeometryCollections(reason = reason)
            it("Has a reason why the validation failed") {
                assert.that(avoidNestedGeometryCollection.reason, equalTo(reason))
            }
        }
    }
})
