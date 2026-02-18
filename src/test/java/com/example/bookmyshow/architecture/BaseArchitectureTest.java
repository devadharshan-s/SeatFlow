package com.example.bookmyshow.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;

public class BaseArchitectureTest {

    protected static JavaClasses classes;

    @BeforeAll
    static void loadClasses() {
        classes = new ClassFileImporter()
                .importPackages("com.example.bookmyshow");
    }
}
