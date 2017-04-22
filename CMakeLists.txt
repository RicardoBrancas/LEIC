cmake_minimum_required(VERSION 2.8)
project(ASA_2)

enable_testing(TRUE)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -Wall -ansi -lm")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/out")
set(TESTS_FOLDER "${CMAKE_SOURCE_DIR}/tests")
SET(CMAKE_BUILD_TYPE Debug)

add_executable(main main.cpp)

