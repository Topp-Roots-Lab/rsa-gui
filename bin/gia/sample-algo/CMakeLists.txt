cmake_minimum_required(VERSION 2.6)

project(histogram-example)

get_filename_component(CMAKE_INSTALL_PREFIX "../" ABSOLUTE)

include_directories("${CMAKE_INSTALL_PREFIX}/include/")
link_directories("${CMAKE_INSTALL_PREFIX}/lib/")

set(EXECUTABLE_OUTPUT_PATH "${CMAKE_INSTALL_PREFIX}/algorithms")
set(LIBRARY_OUTPUT_PATH "${CMAKE_INSTALL_PREFIX}/algorithms")

###

set(OPENCV_LIBS cv cvaux cxcore highgui)
if(${CMAKE_SYSTEM_NAME} MATCHES "Windows")
set(OPENCV_LIBS cv200 cvaux200 cxcore200 highgui200)
endif()

if(${CMAKE_SYSTEM_NAME} MATCHES "Windows")
set(PTHREAD_LIBS pthreadVC2)
ELSE()
set(PTHREAD_LIBS pthread dl)
ENDIF() 

### 3rd party libs stuff done

file(GLOB algo_srcs "*.cpp")
source_group("sources" FILES ${algo_srcs})

file(GLOB algo_hdrs "*.h")
source_group("headers" FILES ${algo_hdrs})

FOREACH(algo ${algo_srcs})
get_filename_component(algo_name ${algo} NAME_WE)
MESSAGE("Processing ${algo_name} algorithm")
add_library(${algo_name} MODULE ${algo} ${algo_hdrs})
add_executable("${algo_name}_test" ${algo} ${algo_hdrs}) 
target_link_libraries(${algo_name} bionic ${OPENCV_LIBS} tinyxml curl)
target_link_libraries(${algo_name}_test bionic ${OPENCV_LIBS} tinyxml curl)
list(APPEND algos ${algo_name})
ENDFOREACH(algo)

INSTALL(TARGETS ${algos}
	RUNTIME DESTINATION "${CMAKE_INSTALL_PREFIX}/algorithms/"
	LIBRARY DESTINATION "${CMAKE_INSTALL_PREFIX}/algorithms/"
	ARCHIVE DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/"
)