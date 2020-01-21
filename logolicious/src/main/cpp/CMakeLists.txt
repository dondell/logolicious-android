cmake_minimum_required(VERSION 3.4.1)

add_library(native-lib SHARED
            native-lib.c)

# Specifies a path to native header files.
include_directories(src/main/cpp/include/)

# Include libraries needed for native-lib lib
target_link_libraries(native-lib
                      android
                      log)
