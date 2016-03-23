cmake_minimum_required(VERSION 3.2)
project(Projeto1)

enable_testing(TRUE)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -g -Wall")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/out")
set(TESTS_FOLDER "${CMAKE_SOURCE_DIR}/tests")
SET(CMAKE_BUILD_TYPE Debug)

add_executable(projeto1 main.c)

add_test(Exemplo1
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/exemplo01.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/exemplo01.out
  -D input_test=${TESTS_FOLDER}/exemplo01.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Exemplo2
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/exemplo02.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/exemplo02.out
  -D input_test=${TESTS_FOLDER}/exemplo02.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Exemplo3
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/exemplo03.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/exemplo03.out
  -D input_test=${TESTS_FOLDER}/exemplo03.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Exemplo4
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/exemplo04.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/exemplo04.out
  -D input_test=${TESTS_FOLDER}/exemplo04.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)

add_test(Test1
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test01.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test01.out
  -D input_test=${TESTS_FOLDER}/test01.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test2
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test02.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test02.out
  -D input_test=${TESTS_FOLDER}/test02.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test3
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test03.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test03.out
  -D input_test=${TESTS_FOLDER}/test03.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test4
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test04.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test04.out
  -D input_test=${TESTS_FOLDER}/test04.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test5
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test05.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test05.out
  -D input_test=${TESTS_FOLDER}/test05.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test6
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test06.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test06.out
  -D input_test=${TESTS_FOLDER}/test06.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test7
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test07.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test07.out
  -D input_test=${TESTS_FOLDER}/test07.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test8
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/projeto1
  -D output_blessed=${TESTS_FOLDER}/test08.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test08.out
  -D input_test=${TESTS_FOLDER}/test08.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
