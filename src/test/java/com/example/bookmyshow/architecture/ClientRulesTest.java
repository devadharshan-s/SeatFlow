package com.example.bookmyshow.architecture;

import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ClientRulesTest extends BaseArchitectureTest {

    @Test
    void services_should_not_use_rest_client_directly() {

        noClasses()
                .that()
                .resideInAPackage("..service..")
                .should()
                .dependOnClassesThat()
                .haveSimpleName("RestClient")
                .check(classes);
    }

    @Test
    void only_clients_should_use_rest_client() {

        noClasses()
                .that()
                .resideOutsideOfPackage("..client..")
                .should()
                .dependOnClassesThat()
                .haveSimpleName("RestClient")
                .check(classes);
    }
}
