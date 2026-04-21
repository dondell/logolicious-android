cmake_minimum_required(VERSION 3.4.1)

add_library(native-lib SHARED
            native-lib.c)

# Support 16 KB page sizes
target_link_libraries(native-lib
                      android
                      log)

target_link_options(native-lib PRIVATE "-Wl,-z,max-page-size=16384")

# Specifies a path to native header files.
include_directories(src/main/cpp/include/)
