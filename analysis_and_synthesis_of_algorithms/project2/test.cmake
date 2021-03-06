execute_process(
 COMMAND ${test_cmd}
 INPUT_FILE ${input_test}
 OUTPUT_FILE ${output_test}
 TIMEOUT 60
)
execute_process(
  COMMAND ${CMAKE_COMMAND} -E compare_files ${output_blessed} ${output_test}
  RESULT_VARIABLE test_not_successful
)

if( test_not_successful  )
 message( SEND_ERROR "" )
endif( test_not_successful )
