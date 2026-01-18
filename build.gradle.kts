plugins {
    java
}

dependencies {
    compileOnly(files("../libs/HytaleServer.jar"))
}

// Root project configuration
// The actual plugin code is in the 'app' module
