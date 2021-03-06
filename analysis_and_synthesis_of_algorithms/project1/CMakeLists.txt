cmake_minimum_required(VERSION 2.8)
project(ASA_1)

enable_testing(TRUE)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -Wall -ansi -lm")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/out")
set(TESTS_FOLDER "${CMAKE_SOURCE_DIR}/tests")
SET(CMAKE_BUILD_TYPE Debug)

add_executable(main main.cpp)

add_test(Exemplo1
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
  -D output_blessed=${TESTS_FOLDER}/example1.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/example1.out
  -D input_test=${TESTS_FOLDER}/example1.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Exemplo2
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
  -D output_blessed=${TESTS_FOLDER}/example2.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/example2.out
  -D input_test=${TESTS_FOLDER}/example2.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Exemplo3
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
  -D output_blessed=${TESTS_FOLDER}/example3.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/example3.out
  -D input_test=${TESTS_FOLDER}/example3.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Exemplo4
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
  -D output_blessed=${TESTS_FOLDER}/example4.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/example4.out
  -D input_test=${TESTS_FOLDER}/example4.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)
add_test(Test5
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
  -D output_blessed=${TESTS_FOLDER}/test5.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test5.out
  -D input_test=${TESTS_FOLDER}/test5.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)

foreach(v RANGE 5 100 2)
  foreach(e RANGE 1 3)
    foreach(r RANGE 2)
      add_test("test${v}-${e}-${r}"
        ${CMAKE_COMMAND}
        -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
        -D output_blessed=${TESTS_FOLDER}/test${v}-${e}-${r}.out
        -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test${v}-${e}-${r}.out
        -D input_test=${TESTS_FOLDER}/test${v}-${e}-${r}.in
        -P ${CMAKE_SOURCE_DIR}/test.cmake
      )
    endforeach(r)
  endforeach(e)
endforeach(v)

foreach(a 10 100 1000)
  foreach(b RANGE 1 5)
      add_test("gasa${a}-${b}"
        ${CMAKE_COMMAND}
        -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
        -D output_blessed=${TESTS_FOLDER}/gasa_${a}_${b}.out
        -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/gasa_${a}_${b}.out
        -D input_test=${TESTS_FOLDER}/gasa_${a}_${b}.in
        -P ${CMAKE_SOURCE_DIR}/test.cmake
      )
  endforeach(b)
endforeach(a)

add_test(HUGE
  ${CMAKE_COMMAND}
  -D test_cmd=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/main
  -D output_blessed=${TESTS_FOLDER}/test_huge.out
  -D output_test=${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/test_huge.out
  -D input_test=${TESTS_FOLDER}/test_huge.in
  -P ${CMAKE_SOURCE_DIR}/test.cmake
)

