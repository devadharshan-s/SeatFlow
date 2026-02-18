package com.example.bookmyshow.architecture;

import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class InternalPackageRulesTest extends BaseArchitectureTest {

    @Test
    void internal_packages_must_not_be_accessed_from_outside() {

        noClasses()
                .that()
                .resideOutsideOfPackage("..internal..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..internal..")
                .check(classes);
    }
}