execute_process(
  COMMAND ${generator} ${v} ${e} ${r}
  OUTPUT_FILE ${input_test}
)

if(${r} EQUAL 1)
  execute_process(
    COMMAND echo "Incoerente"
    OUTPUT_FILE ${output_blessed}
  )
elseif(${r} EQUAL 2)
  execute_process(
    COMMAND echo "Insuficiente"
    OUTPUT_FILE ${output_blessed}
  )
endif(${r} EQUAL 1)

execute_process(
 COMMAND ${test_cmd}
 INPUT_FILE ${input_test}
 OUTPUT_FILE ${output_test}
 TIMEOUT 10
)
execute_process(
  COMMAND ${CMAKE_COMMAND} -E compare_files ${output_blessed} ${output_test}
  RESULT_VARIABLE test_not_successful
)

if( (test_not_successful) AND (NOT (${r} EQUAL 0)) )
 message( SEND_ERROR "" )
endif( (test_not_successful) AND (NOT (${r} EQUAL 0)) )
