package com.example.bookmyshow.architecture;

import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ModuleBoundaryRulesTest extends BaseArchitectureTest {

    @Test
    void show_must_not_depend_on_theatre_internal() {

        noClasses()
                .that()
                .resideInAPackage("..show..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..theatre.internal..")
                .check(classes);
    }

    @Test
    void booking_must_not_depend_on_show_internal() {

        noClasses()
                .that()
                .resideInAPackage("..booking..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..show.internal..")
                .check(classes);
    }
}
