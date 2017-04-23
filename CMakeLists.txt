cmake_minimum_required(VERSION 2.8)
project(ASA_2)

enable_testing(TRUE)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -Wall -ansi -lm")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -ansi -lm")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/out")
set(TESTS_FOLDER "${CMAKE_SOURCE_DIR}/tests")
SET(CMAKE_BUILD_TYPE Debug)

add_executable(main main.cpp)

foreach(n RANGE 1 5)
  add_test("Example${n}"
    ${CMAKE_COMMAND}
    -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
    -D output_blessed=${TESTS_FOLDER}/Example${n}.out
    -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/Example${n}.out
    -D input_test=${TESTS_FOLDER}/Example${n}.in
    -P ${CMAKE_SOURCE_DIR}/test.cmake
  )
endforeach(n)

foreach(n RANGE 1 3)
  add_test("Test${n}"
    ${CMAKE_COMMAND}
    -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
    -D output_blessed=${TESTS_FOLDER}/test${n}.out
    -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test${n}.out
    -D input_test=${TESTS_FOLDER}/test${n}.in
    -P ${CMAKE_SOURCE_DIR}/test.cmake
  )
endforeach(n)
