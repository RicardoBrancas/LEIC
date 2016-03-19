cmake_minimum_required(VERSION 3.2)
project(Projeto1)

enable_testing(TRUE)

set(CMAKE_CXX_FLAGS "-g -Wall")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/out")
set(TESTS_FOLDER "${CMAKE_SOURCE_DIR}/tests")

add_executable(projeto1 main.c)

add_test(Test1
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test01.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test1.out
  -D input_test=${TESTS_FOLDER}/exemplo01.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test2
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test02.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test2.out
  -D input_test=${TESTS_FOLDER}/exemplo02.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test3
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test03.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test3.out
  -D input_test=${TESTS_FOLDER}/exemplo03.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test4
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test04.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test4.out
  -D input_test=${TESTS_FOLDER}/exemplo04.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
