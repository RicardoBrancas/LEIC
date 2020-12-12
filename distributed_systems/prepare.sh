#!/bin/sh

(cd ws-handlers; mvn -q compile install)
(cd station-ws; mvn -q compile)
(cd binas-ws; mvn -q compile)
(cd binas-ws-cli; mvn -q compile)
