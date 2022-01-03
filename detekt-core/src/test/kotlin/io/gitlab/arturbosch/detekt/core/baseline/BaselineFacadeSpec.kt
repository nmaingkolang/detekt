package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.createTempDirectoryForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class BaselineFacadeSpec : Spek({

    describe("a baseline facade") {

        val dir by memoized { createTempDirectoryForTest("baseline_format") }
        val validBaseline = resourceAsPath("/baseline_feature/valid-baseline.xml")

        it("returns a BaselineFilteredResult when the baseline exists") {
            val detektion = BaselineFacade().transformResult(validBaseline, TestDetektion())

            assertThat(detektion).isInstanceOf(BaselineFilteredResult::class.java)
        }

        it("returns the same detektion when the baseline doesn't exist") {
            val initialDetektion = TestDetektion()
            val detektion = BaselineFacade().transformResult(dir.resolve("baseline.xml"), initialDetektion)

            assertThat(detektion).isEqualTo(initialDetektion)
        }

        it("creates a baseline file") {
            val fullPath = dir.resolve("baseline.xml")
            assertNonEmptyBaseline(fullPath)
        }

        it("creates on top of an existing a baseline file") {
            val fullPath = dir.resolve("baseline2.xml")

            Files.copy(validBaseline, fullPath, StandardCopyOption.REPLACE_EXISTING)

            assertNonEmptyBaseline(fullPath)
        }
    }
})

private fun assertNonEmptyBaseline(fullPath: Path) {
    BaselineFacade().createOrUpdate(fullPath, emptyList())
    val lines = Files.readAllLines(fullPath)
    assertThat(lines).isNotEmpty
}
