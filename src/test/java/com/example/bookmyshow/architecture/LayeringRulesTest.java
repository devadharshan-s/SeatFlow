package com.example.bookmyshow.architecture;

import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class LayeringRulesTest extends BaseArchitectureTest {

    @Test
    void controllers_should_not_access_repositories_directly() {

        noClasses()
                .that()
                .resideInAnyPackage("..controller..", "..api..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..repository..")
                .check(classes);
    }

    @Test
    void controllers_should_not_access_entities_directly() {

        noClasses()
                .that()
                .resideInAnyPackage("..controller..", "..api..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..model..")
                .check(classes);
    }
}

