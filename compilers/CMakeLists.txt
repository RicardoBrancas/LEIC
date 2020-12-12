cmake_minimum_required(VERSION 3.10)

include_directories(.)

file(GLOB SRC_AST ast/*.h)
file(GLOB SRC_TARGETS targets/*.cpp)
file(GLOB SRC_TARGETS_H targets/*.h)
file(GLOB SRC *.cpp)

add_executable(gr8 ${SRC} ${SRC_AST} ${SRC_TARGETS} ${SRC_TARGETS_H})